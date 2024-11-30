package com.github.ucov.sum;

import com.github.maracas.roseau.api.model.*;
import com.github.ucov.RoseauFQNGenertor;
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

        for (TypeDecl apiType : mainProjectApiModel.getExportedTypes().toList()) {
            Usage usage = new Usage(
                    projectId,
                    projectType,
                    apiType.getQualifiedName(),
                    SymbolKind.SYMBOL_KIND_TYPE,
                    SymbolUse.SYMBOL_USE_TYPE_REFERENCE,
                    apiType.getLocation(),
                    projectLocation
            );
            if (!usageModelCollection.contains(usage)) {
                usageModelCollection.add(usage);
            }

            if (apiType.isInterface()) {
                usage = new Usage(
                        projectId,
                        projectType,
                        apiType.getQualifiedName(),
                        SymbolKind.SYMBOL_KIND_INTERFACE,
                        SymbolUse.SYMBOL_USE_EXTENSION,
                        apiType.getLocation(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }

                usage = new Usage(
                        projectId,
                        projectType,
                        apiType.getQualifiedName(),
                        SymbolKind.SYMBOL_KIND_INTERFACE,
                        SymbolUse.SYMBOL_USE_IMPLEMENTATION,
                        apiType.getLocation(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }
            }

            if (apiType.isClass() &&
                    !apiType.getModifiers().contains(Modifier.FINAL)) {
                usage = new Usage(
                        projectId,
                        projectType,
                        apiType.getQualifiedName(),
                        SymbolKind.SYMBOL_KIND_CLASS,
                        SymbolUse.SYMBOL_USE_INHERITANCE,
                        apiType.getLocation(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }
            }

            if (apiType.isClass()) {
                for (ConstructorDecl constructor : ((ClassDecl) apiType).getConstructors()) {
                    usage = new Usage(
                            projectId,
                            projectType,
                            RoseauFQNGenertor.getFullyQualifiedNameFromRoseauConstructorDecl(constructor),
                            SymbolKind.SYMBOL_KIND_CONSTRUCTOR,
                            SymbolUse.SYMBOL_USE_INVOCATION,
                            constructor.getLocation(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }

                    if (!apiType.getModifiers().contains(Modifier.ABSTRACT)) {
                        usage = new Usage(
                                projectId,
                                projectType,
                                apiType.getQualifiedName(),
                                SymbolKind.SYMBOL_KIND_CLASS,
                                SymbolUse.SYMBOL_USE_INSTANTIATION,
                                apiType.getLocation(),
                                projectLocation
                        );
                        if (!usageModelCollection.contains(usage)) {
                            usageModelCollection.add(usage);
                        }
                    }
                }
            }

            for (MethodDecl method : apiType.getAllMethods().toList()) {
                if (!method.getModifiers().contains(Modifier.ABSTRACT)) {
                    boolean isStatic = method.getModifiers().contains(Modifier.STATIC);
                    usage = new Usage(
                            projectId,
                            projectType,
                            RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method),
                            SymbolKind.SYMBOL_KIND_METHOD,
                            isStatic ? SymbolUse.SYMBOL_USE_STATIC_INVOCATION : SymbolUse.SYMBOL_USE_INVOCATION,
                            method.getLocation(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }
                }

                if (!method.getModifiers().contains(Modifier.FINAL)) {
                    usage = new Usage(
                            projectId,
                            projectType,
                            RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method),
                            SymbolKind.SYMBOL_KIND_METHOD,
                            SymbolUse.SYMBOL_USE_OVERRIDING,
                            method.getLocation(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }

                    // Methods must be overridable to be virtually invokable
                    usage = new Usage(
                            projectId,
                            projectType,
                            RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method),
                            SymbolKind.SYMBOL_KIND_METHOD,
                            SymbolUse.SYMBOL_USE_VIRTUAL_INVOCATION,
                            method.getLocation(),
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usage)) {
                        usageModelCollection.add(usage);
                    }
                }
            }

            for (FieldDecl field : apiType.getAllFields().toList()) {
                usage = new Usage(
                        projectId,
                        projectType,
                        field.getQualifiedName(),
                        SymbolKind.SYMBOL_KIND_FIELD,
                        SymbolUse.SYMBOL_USE_INSTANCE_FIELD_READ,
                        field.getLocation(),
                        projectLocation
                );
                if (!usageModelCollection.contains(usage)) {
                    usageModelCollection.add(usage);
                }

                if (!field.getModifiers().contains(Modifier.FINAL)) {
                    usage = new Usage(
                            projectId,
                            projectType,
                            field.getQualifiedName(),
                            SymbolKind.SYMBOL_KIND_FIELD,
                            SymbolUse.SYMBOL_USE_INSTANCE_FIELD_WRITE,
                            field.getLocation(),
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
