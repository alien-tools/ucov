package com.github.ucov.sum;

import com.github.maracas.roseau.model.*;
import com.github.ucov.models.Project;
import com.github.ucov.models.SymbolKind;
import com.github.ucov.models.SymbolUse;
import com.github.ucov.models.Usage;

import java.nio.file.Path;
import java.util.ArrayList;

public class SUMGenerator {
    public static ArrayList<Usage> getSUM(API mainProjectApiModel, Project project) {
        String projectId = project.id();
        String projectType = project.type().name();
        Path projectLocation = project.location();

        ArrayList<Usage> usageModelCollection = new ArrayList<>();

        for (TypeDeclaration apiType : mainProjectApiModel.getallTheTypes()) {
            Usage usage = new Usage(
                    projectId,
                    projectType,
                    apiType.getFullyQualifiedName(),
                    SymbolKind.SYMBOL_KIND_TYPE,
                    SymbolUse.SYMBOL_USE_TYPE_REFERENCE,
                    apiType.getPosition(),
                    projectLocation
            );
            if (!usageModelCollection.contains(usage)) {
                usageModelCollection.add(usage);
            }

            if (apiType.getTypeType() == TypeType.INTERFACE) {
                usage = new Usage(
                        projectId,
                        projectType,
                        apiType.getFullyQualifiedName(),
                        SymbolKind.SYMBOL_KIND_INTERFACE,
                        SymbolUse.SYMBOL_USE_EXTENSION,
                        apiType.getPosition(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }

                usage = new Usage(
                        projectId,
                        projectType,
                        apiType.getFullyQualifiedName(),
                        SymbolKind.SYMBOL_KIND_INTERFACE,
                        SymbolUse.SYMBOL_USE_IMPLEMENTATION,
                        apiType.getPosition(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }
            }

            if (apiType.getTypeType() == TypeType.CLASS &&
                    !apiType.getModifiers().contains(NonAccessModifiers.FINAL)) {
                usage = new Usage(
                        projectId,
                        projectType,
                        apiType.getFullyQualifiedName(),
                        SymbolKind.SYMBOL_KIND_CLASS,
                        SymbolUse.SYMBOL_USE_INHERITANCE,
                        apiType.getPosition(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }
            }

            for (ConstructorDeclaration constructor : apiType.getConstructors()) {
                usage = new Usage(
                        projectId,
                        projectType,
                        constructor.getFullyQualifiedName(),
                        SymbolKind.SYMBOL_KIND_CONSTRUCTOR,
                        SymbolUse.SYMBOL_USE_INVOCATION,
                        constructor.getPosition(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }

                if (!apiType.getModifiers().contains(NonAccessModifiers.ABSTRACT)) {
                    usage = new Usage(
                            projectId,
                            projectType,
                            apiType.getFullyQualifiedName(),
                            SymbolKind.SYMBOL_KIND_CLASS,
                            SymbolUse.SYMBOL_USE_INSTANTIATION,
                            apiType.getPosition(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }
                }
            }

            for (MethodDeclaration method : apiType.getMethods()) {
                if (!method.getModifiers().contains(NonAccessModifiers.ABSTRACT)) {
                    boolean isStatic = method.getModifiers().contains(NonAccessModifiers.STATIC);
                    usage = new Usage(
                            projectId,
                            projectType,
                            method.getFullyQualifiedName(),
                            SymbolKind.SYMBOL_KIND_METHOD,
                            isStatic ? SymbolUse.SYMBOL_USE_STATIC_INVOCATION : SymbolUse.SYMBOL_USE_INVOCATION,
                            method.getPosition(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }
                }

                if (!method.getModifiers().contains(NonAccessModifiers.FINAL)) {
                    usage = new Usage(
                            projectId,
                            projectType,
                            method.getFullyQualifiedName(),
                            SymbolKind.SYMBOL_KIND_METHOD,
                            SymbolUse.SYMBOL_USE_OVERRIDING,
                            method.getPosition(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }

                    // Methods must be overridable to be virtually invokable
                    usage = new Usage(
                            projectId,
                            projectType,
                            method.getFullyQualifiedName(),
                            SymbolKind.SYMBOL_KIND_METHOD,
                            SymbolUse.SYMBOL_USE_VIRTUAL_INVOCATION,
                            method.getPosition(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }
                }
            }

            for (FieldDeclaration field : apiType.getFields()) {
                usage = new Usage(
                        projectId,
                        projectType,
                        field.getFullyQualifiedName(),
                        SymbolKind.SYMBOL_KIND_FIELD,
                        SymbolUse.SYMBOL_USE_INSTANCE_FIELD_READ,
                        field.getPosition(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }

                if (!field.getModifiers().contains(NonAccessModifiers.FINAL)) {
                    usage = new Usage(
                            projectId,
                            projectType,
                            field.getFullyQualifiedName(),
                            SymbolKind.SYMBOL_KIND_FIELD,
                            SymbolUse.SYMBOL_USE_INSTANCE_FIELD_WRITE,
                            field.getPosition(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }
                }
            }
        }

        return usageModelCollection;
    }
}
