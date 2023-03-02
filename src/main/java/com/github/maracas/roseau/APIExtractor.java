package com.github.maracas.roseau;

import com.github.maracas.roseau.model.*;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class represents roseau's API getion tool.
 */

public final class APIExtractor {
    // Returning the packages as spoon CtPackages
    private static List<CtPackage> rawSpoonPackages(CtModel model) {
        return model.getAllPackages().stream().toList();
    }

    private static boolean typeIsAccessible(CtType<?> type) {
        ModifierKind visibility = type.getVisibility();
        if (visibility == null) {
            return false;
        }

        return switch (visibility) {
            case PUBLIC -> true;
            case PROTECTED -> !type.isFinal() && !type.getModifiers().contains(ModifierKind.SEALED);
            default -> false;
        };
    }

    private static boolean memberIsAccessible(CtModifiable member) {
        return member.isPublic() || member.isProtected();
    }

    // Returning the accessible types of a package as spoon CtTypes
    private static List<CtType<?>> getSpoonTypes(CtPackage pkg) {
        List<CtType<?>> types = new ArrayList<>();
        pkg.getTypes().stream()
                .filter(APIExtractor::typeIsAccessible)
                .forEach(type -> {
                    types.add(type);
                    getSpoonNestedTypes(type, types);
                });
        return types;
    }

    // Handling nested types
    private static void getSpoonNestedTypes(CtType<?> parentType, List<CtType<?>> types) {
        parentType.getNestedTypes().stream()
                .filter(APIExtractor::typeIsAccessible)
                .forEach(type -> {
                    types.add(type);
                    getSpoonNestedTypes(type, types);
                });
    }

    // Returning the accessible fields of a type as spoon CtFields
    private static List<CtField<?>> getSpoonFields(CtType<?> type) {
        return type.getFields().stream()
                .filter(APIExtractor::memberIsAccessible)
                .toList();
    }

    // Returning the accessible methods of a type as spoon CtMethods
    private static List<CtMethod<?>> getSpoonMethods(CtType<?> type) {
        return type.getMethods().stream()
                .filter(APIExtractor::memberIsAccessible)
                .toList();
    }

    // Returning the accessible constructors of a type as spoon CtConstructors
    private static List<CtConstructor<?>> getSpoonConstructors(CtType<?> type) {
        if (type instanceof CtClass<?> cls) {
            return new ArrayList<>(cls.getConstructors().stream()
                    .filter(APIExtractor::memberIsAccessible)
                    .toList());
        }
        return Collections.emptyList();

    }

    // The conversion functions : Moving from spoon's Ct kinds to roseau's
    // Declaration kinds

    private static List<FieldDeclaration> getDeclarationsFromCtFields(List<CtField<?>> spoonFields) {
        return spoonFields.stream()
                .map(SpoonTypeConverter::getDeclarationFromCtField)
                .toList(); // Adding it to the list of FieldDeclarations
    }

    private static List<MethodDeclaration> getDeclarationsFromCtMethods(List<CtMethod<?>> spoonMethods) {
        return spoonMethods.stream()
                .map(SpoonTypeConverter::getDeclarationFromCtMethod)
                .toList(); // Adding it to the list of MethodDeclarations
    }

    private static List<ConstructorDeclaration> getDeclarationsFromCtConstructors(
            List<CtConstructor<?>> spoonConstructors) {
        return spoonConstructors.stream()
                .map(SpoonTypeConverter::getDeclarationFromCtConstructor)
                .toList(); // Adding it to the list of ConstructorDeclarations
    }

    // Get all the superclasses of a type, direct or not
    private static List<TypeDeclaration> getAllSuperclasses(String className, List<TypeDeclaration> allTypes) {
        List<TypeDeclaration> superclasses = new ArrayList<>();
        TypeDeclaration currentType = getTypeByName(className, allTypes);

        if (currentType != null) {
            String superclassName = currentType.getSuperclassName();
            if (!superclassName.equals("None")) {
                List<TypeDeclaration> directSuperclasses = getAllSuperclasses(superclassName, allTypes);
                superclasses.add(currentType);
                superclasses.addAll(directSuperclasses);
            } else {
                superclasses.add(currentType);
            }
        }

        return superclasses;
    }

    // Helper to get a TypeDeclaration by name
    private static TypeDeclaration getTypeByName(String className, List<TypeDeclaration> allTypes) {
        return allTypes.stream()
                .filter(typeDeclaration -> typeDeclaration.getName().equals(className))
                .findFirst()
                .orElse(null);
    }

    private static void fillTypeDeclaration(TypeDeclaration typeDeclaration, CtType<?> type) {
        List<CtField<?>> fields = getSpoonFields(type); // Returning the accessible fields of
        // accessible types
        List<FieldDeclaration> fieldsConverted = getDeclarationsFromCtFields(fields); // Transforming them into fieldDeclarations
        typeDeclaration.setFields(fieldsConverted); // Adding them to the TypeDeclaration they belong to

        // Doing the same thing for methods and constructors
        List<CtMethod<?>> methods = getSpoonMethods(type);
        List<MethodDeclaration> methodsConverted = getDeclarationsFromCtMethods(methods);
        typeDeclaration.setMethods(methodsConverted);

        List<CtConstructor<?>> constructors = getSpoonConstructors(type);
        List<ConstructorDeclaration> constructorsConverted = getDeclarationsFromCtConstructors(constructors);
        typeDeclaration.setConstructors(constructorsConverted);
    }

    /**
     * Extracts the library's (model's) structured API.
     *
     * @return Library's (model's) API.
     */
    public static API getAPIFromCtModel(CtModel model) {
        CtModel spoonModel = Objects.requireNonNull(model);

        List<CtPackage> ctPackageList = rawSpoonPackages(spoonModel); // Returning packages
        List<TypeDeclaration> typeDeclarationList = new ArrayList<>();

        for (CtPackage ctPackage : ctPackageList) { // Looping over the packages to get all the library's types
            List<CtType<?>> ctTypeList = getSpoonTypes(ctPackage); // Only returning the packages' accessible types
            // Transforming the spoon's CtTypes into TypeDeclarations
            for (CtType<?> ctType : ctTypeList) { // Looping over spoon's types to fill the TypeDeclarations' fields / methods / constructors
                TypeDeclaration typeDeclaration = SpoonTypeConverter.getDeclarationFromCtType(ctType);
                fillTypeDeclaration(typeDeclaration, ctType);
                typeDeclarationList.add(typeDeclaration);
            }
        }

        // Adding the superclasses info
        API api = new API(typeDeclarationList);

        typeDeclarationList.forEach(typeDeclaration -> {
            String superclassName = typeDeclaration.getSuperclassName();
            if (!superclassName.equals("None")) {
                List<TypeDeclaration> superclasses = getAllSuperclasses(superclassName, typeDeclarationList);
                typeDeclaration.setAllSuperclasses(superclasses);
            }

            // Filling the superinterfaces too
            List<String> superinterfaceNames = typeDeclaration.getSuperinterfacesNames();
            List<TypeDeclaration> superinterfaces = new ArrayList<>();

            for (String superinterfaceName : superinterfaceNames) {
                typeDeclarationList.stream()
                        .filter(superInterfaceDec -> superInterfaceDec.getName().equals(superinterfaceName))
                        .findFirst().ifPresent(superinterfaces::add);
            }

            typeDeclaration.setSuperinterfaces(superinterfaces);
        });

        return api; // returning the library's API
    }
}
