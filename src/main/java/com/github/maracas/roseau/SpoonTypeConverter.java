package com.github.maracas.roseau;

import com.github.maracas.roseau.model.*;
import com.github.ucov.models.UsagePosition;
import spoon.reflect.declaration.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpoonTypeConverter {

    // Converting spoon's access ModifierKind to roseau's enum : AccessModifier
    private static AccessModifier convertVisibility(ModifierKind visibility) {
        return switch (visibility) {
            case PUBLIC -> AccessModifier.PUBLIC;
            case PRIVATE -> AccessModifier.PRIVATE;
            case PROTECTED -> AccessModifier.PROTECTED;
            default -> AccessModifier.DEFAULT;
        };
    }

    // Converting spoon's Non-access ModifierKind to roseau's enum :
    // NonAccessModifier
    private static NonAccessModifiers convertNonAccessModifier(ModifierKind modifier) {
        return switch (modifier) {
            case STATIC -> NonAccessModifiers.STATIC;
            case FINAL -> NonAccessModifiers.FINAL;
            case ABSTRACT -> NonAccessModifiers.ABSTRACT;
            case SYNCHRONIZED -> NonAccessModifiers.SYNCHRONIZED;
            case VOLATILE -> NonAccessModifiers.VOLATILE;
            case TRANSIENT -> NonAccessModifiers.TRANSIENT;
            case SEALED -> NonAccessModifiers.SEALED;
            case NON_SEALED -> NonAccessModifiers.NON_SEALED;
            case NATIVE -> NonAccessModifiers.NATIVE;
            default -> NonAccessModifiers.STRICTFP;
        };
    }

    // Filtering access modifiers because the convertVisibility() handles them
    // already
    private static List<NonAccessModifiers> filterNonAccessModifiers(Set<ModifierKind> modifiers) {
        List<NonAccessModifiers> nonAccessModifiers = new ArrayList<>();

        for (ModifierKind modifier : modifiers) {
            if (modifier != ModifierKind.PUBLIC && modifier != ModifierKind.PRIVATE && modifier != ModifierKind.PROTECTED) {
                nonAccessModifiers.add(convertNonAccessModifier(modifier));
            }
        }

        return nonAccessModifiers;
    }

    // Returning the type's kind ( whether if it's a
    // class/enum/interface/annotation/record )
    private static TypeType convertTypeType(CtType<?> type) {
        if (type.isClass()) return TypeType.CLASS;
        if (type.isInterface()) return TypeType.INTERFACE;
        if (type.isEnum()) return TypeType.ENUM;
        if (type.isAnnotationType()) return TypeType.ANNOTATION;
        else return TypeType.RECORD;
    }

    // The conversion functions : Moving from spoon's Ct kinds to roseau's
    // Declaration kinds

    public static String getCtTypeFullyQualifiedName(CtType<?> spoonType) {
        // If type=type.FQN, field=parentType.FQN+fieldName, method/cons=type.FQN+signature
        return spoonType.getQualifiedName();
    }

    public static String getCtFieldFullyQualifiedName(CtTypeMember spoonField) {
        // If type=type.FQN, field=parentType.FQN+fieldName, method/cons=type.FQN+signature
        return getCtTypeFullyQualifiedName(spoonField.getDeclaringType()) + "." + spoonField.getSimpleName();
    }

    public static String getCtMethodFullyQualifiedName(CtMethod<?> spoonMethod) {
        // If type=type.FQN, field=parentType.FQN+fieldName, method/cons=type.FQN+signature
        return getCtTypeFullyQualifiedName(spoonMethod.getDeclaringType()) + "." + spoonMethod.getSignature();
    }

    public static String getCtConstructorFullyQualifiedName(CtConstructor<?> spoonConstructor) {
        // If type=type.FQN, field=parentType.FQN+fieldName, method/cons=type.FQN+signature
        return spoonConstructor.getSignature();
    }

    private static UsagePosition getPosition(CtElement spoonMethod) {
        return UsagePosition.getFromSourcePosition(spoonMethod.getPosition());
    }

    private static List<String> getExceptions(CtExecutable<?> spoonMethod) {
        return spoonMethod.getThrownTypes().stream().filter(exception -> !exception.getQualifiedName().equals("java.lang.RuntimeException") && (exception.getSuperclass() == null || !exception.getSuperclass().getQualifiedName().equals("java.lang.RuntimeException"))).map(CtTypeInformation::getQualifiedName).toList();
    }

    private static Signature getSignature(String name, List<String> parametersTypes) {
        return new Signature(name, parametersTypes);
    }

    private static List<NonAccessModifiers> getModifiers(CtModifiable spoonMethod) {
        return filterNonAccessModifiers(spoonMethod.getModifiers());
    }

    private static List<List<String>> getFormalTypeParamsBounds(CtFormalTypeDeclarer spoonMethod) {
        return spoonMethod.getFormalCtTypeParameters().stream().map(formalTypeParameter -> formalTypeParameter.getReferencedTypes().stream().map(Object::toString).toList()).toList();
    }

    private static List<String> getFormalTypeParameters(CtFormalTypeDeclarer spoonMethod) {
        return spoonMethod.getFormalCtTypeParameters().stream().map(CtTypeInformation::getQualifiedName).toList();
    }

    private static List<List<String>> getParametersReferencedTypes(CtExecutable<?> spoonMethod) {
        return spoonMethod.getParameters().stream().map(parameter -> parameter.getReferencedTypes().stream().map(Object::toString).toList()).toList();
    }

    private static List<String> getParametersTypes(CtExecutable<?> spoonMethod) {
        return spoonMethod.getParameters().stream().map(parameterType -> parameterType.getType().getQualifiedName()).toList();
    }

    private static List<String> getReturnTypeReferencedType(CtElement spoonMethod) {
        return spoonMethod.getReferencedTypes().stream().map(Object::toString).toList();
    }

    private static String getReturnType(CtTypedElement<?> spoonMethod) {
        return spoonMethod.getType().getQualifiedName();
    }

    private static AccessModifier getVisibility(CtModifiable spoonMethod) {
        return convertVisibility(spoonMethod.getVisibility());
    }

    private static String getName(CtNamedElement spoonMethod) {
        return spoonMethod.getSimpleName();
    }

    public static TypeDeclaration getDeclarationFromCtType(CtType<?> spoonType) {
        // get relevant information from the spoonType
        String fullyQualifiedName = getCtTypeFullyQualifiedName(spoonType);
        String name = getName(spoonType);
        AccessModifier visibility = getVisibility(spoonType);
        TypeType typeType = convertTypeType(spoonType);
        List<NonAccessModifiers> modifiers = getModifiers(spoonType);

        String superclassName = "None";
        if (spoonType.getSuperclass() != null) {
            superclassName = spoonType.getSuperclass().getQualifiedName();
        }

        List<String> superinterfacesNames = spoonType.getSuperInterfaces().stream().map(CtTypeInformation::getQualifiedName).toList();

        List<String> referencedTypes = getReturnTypeReferencedType(spoonType);
        List<String> formalTypeParameters = getFormalTypeParameters(spoonType);
        List<List<String>> formalTypeParamsBounds = getFormalTypeParamsBounds(spoonType);
        boolean isNested = !spoonType.isTopLevel();
        UsagePosition position = getPosition(spoonType);

        // Creating a new TypeDeclaration object using the get information
        return new TypeDeclaration(fullyQualifiedName, name, visibility, typeType, modifiers, superclassName, superinterfacesNames, referencedTypes, formalTypeParameters, formalTypeParamsBounds, isNested, position, spoonType.getPackage().getQualifiedName());
    }

    public static FieldDeclaration getDeclarationFromCtField(CtField<?> spoonField) {
        // get relevant information from the spoonField
        String fullyQualifiedName = getCtFieldFullyQualifiedName(spoonField);
        String name = getName(spoonField);
        AccessModifier visibility = getVisibility(spoonField);
        String dataType = spoonField.getType().getQualifiedName();
        List<NonAccessModifiers> modifiers = getModifiers(spoonField);
        List<String> referencedTypes = getReturnTypeReferencedType(spoonField);
        UsagePosition position = getPosition(spoonField);

        // Creating a new FieldDeclaration object using the get information
        return new FieldDeclaration(fullyQualifiedName, name, visibility, dataType, modifiers, referencedTypes, position);
    }

    public static MethodDeclaration getDeclarationFromCtMethod(CtMethod<?> spoonMethod) {
        // get relevant information from the spoonMethod
        String fullyQualifiedName = getCtMethodFullyQualifiedName(spoonMethod);
        String name = getName(spoonMethod);
        AccessModifier visibility = getVisibility(spoonMethod);
        String returnType = getReturnType(spoonMethod);
        List<String> returnTypeReferencedType = getReturnTypeReferencedType(spoonMethod);
        List<String> parametersTypes = getParametersTypes(spoonMethod);
        List<List<String>> parametersReferencedTypes = getParametersReferencedTypes(spoonMethod);
        List<String> formalTypeParameters = getFormalTypeParameters(spoonMethod);
        List<List<String>> formalTypeParamsBounds = getFormalTypeParamsBounds(spoonMethod);
        List<NonAccessModifiers> modifiers = getModifiers(spoonMethod);
        Signature signature = getSignature(name, parametersTypes);
        List<String> exceptions = getExceptions(spoonMethod);
        UsagePosition position = getPosition(spoonMethod);

        List<Boolean> parametersVarargsCheck = spoonMethod.getParameters().stream().map(CtParameter::isVarArgs).toList();
        boolean isDefault = spoonMethod.isDefaultMethod();

        // Creating a new MethodDeclaration object using the get information
        return new MethodDeclaration(fullyQualifiedName, name, visibility, returnType, returnTypeReferencedType, parametersTypes, parametersReferencedTypes, formalTypeParameters, formalTypeParamsBounds, modifiers, signature, exceptions, parametersVarargsCheck, isDefault, position);
    }

    public static ConstructorDeclaration getDeclarationFromCtConstructor(CtConstructor<?> spoonConstructor) {
        // get relevant information from the spoonConstructor
        String fullyQualifiedName = getCtConstructorFullyQualifiedName(spoonConstructor);
        String name = getName(spoonConstructor);
        AccessModifier visibility = getVisibility(spoonConstructor);
        String returnType = getReturnType(spoonConstructor);
        List<String> returnTypeReferencedType = getReturnTypeReferencedType(spoonConstructor);
        List<String> parametersTypes = getParametersTypes(spoonConstructor);
        List<List<String>> parametersReferencedTypes = getParametersReferencedTypes(spoonConstructor);
        List<String> formalTypeParameters = getFormalTypeParameters(spoonConstructor);
        List<List<String>> formalTypeParamsBounds = getFormalTypeParamsBounds(spoonConstructor);
        List<NonAccessModifiers> modifiers = getModifiers(spoonConstructor);
        Signature signature = getSignature(name, parametersTypes);
        List<String> exceptions = getExceptions(spoonConstructor);
        UsagePosition position = getPosition(spoonConstructor);

        // Creating a new ConstructorDeclaration object using the get information
        return new ConstructorDeclaration(fullyQualifiedName, name, visibility, returnType, returnTypeReferencedType, parametersTypes, parametersReferencedTypes, formalTypeParameters, formalTypeParamsBounds, modifiers, signature, exceptions, position);
    }
}
