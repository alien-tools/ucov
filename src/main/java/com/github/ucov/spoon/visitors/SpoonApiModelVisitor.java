package com.github.ucov.spoon.visitors;

import com.github.maracas.roseau.api.model.*;
import com.github.ucov.RoseauFQNGenertor;
import com.github.ucov.models.SymbolKind;
import com.github.ucov.models.SymbolUse;
import com.github.ucov.models.Usage;
import spoon.reflect.code.*;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;

import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class SpoonApiModelVisitor extends CtScanner {
    private final API api;
    private final Filter<CtElement> filter;
    private final ArrayList<Usage> usageModelCollection = new ArrayList<>();
    private final String projectId;
    private final String projectType;
    private final Path projectLocation;

    public SpoonApiModelVisitor(API api, Filter<CtElement> filter, String projectId, String projectType, Path projectLocation) {
        this.api = api;
        this.filter = filter;
        this.projectId = projectId;
        this.projectType = projectType;
        this.projectLocation = projectLocation;
    }

    private SourceLocation convertSpoonPosition(SourcePosition position) {
        return position.isValidPosition()
                ? new SourceLocation(
                position.getFile() != null ? position.getFile().toPath() : null,
                position.getLine())
                : SourceLocation.NO_LOCATION;
    }

    @Override
    public <T> void visitCtInvocation(CtInvocation<T> invocation) {
        super.visitCtInvocation(invocation);

        if (!filter.matches(invocation)) {
            return;
        }

        if (invocation.getExecutable().getDeclaringType() == null) {
            return;
        }

        CtExecutableReference<T> executable = invocation.getExecutable();
        String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(invocation);
        SourceLocation position = convertSpoonPosition(invocation.getPosition());

        Collection<CtMethod<?>> topDefinitions = null;

        if (executable.getExecutableDeclaration() instanceof CtMethod<?> ctMethod) {
            topDefinitions = ctMethod.getTopDefinitions();
        }
        
        for (TypeDecl apiType : api.getExportedTypes().toList()) {
            for (MethodDecl method : apiType.getAllMethods().toList()) {
                if (RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method).equals(fullyQualifiedName)) {
                    if (!method.getModifiers().contains(Modifier.ABSTRACT)) {
                        boolean isStatic = method.getModifiers().contains(Modifier.STATIC);
                        Usage usageModel = new Usage(
                                projectId,
                                projectType,
                                RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method),
                                SymbolKind.SYMBOL_KIND_METHOD,
                                isStatic ? SymbolUse.SYMBOL_USE_STATIC_INVOCATION : SymbolUse.SYMBOL_USE_INVOCATION,
                                position,
                                projectLocation
                        );
                        if (!usageModelCollection.contains(usageModel)) {
                            usageModelCollection.add(usageModel);
                        }
                    } else {
                        Usage usageModel = new Usage(
                                projectId,
                                projectType,
                                RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method),
                                SymbolKind.SYMBOL_KIND_METHOD,
                                SymbolUse.SYMBOL_USE_VIRTUAL_INVOCATION,
                                position,
                                projectLocation
                        );
                        if (!usageModelCollection.contains(usageModel)) {
                            usageModelCollection.add(usageModel);
                        }
                    }
                }

                if (topDefinitions != null) {
                    for (CtMethod<?> topMethod : topDefinitions) {
                        CtType<?> declaringType = topMethod.getDeclaringType();
                        String mFQN = STR."\{declaringType.getQualifiedName()}.\{topMethod.getSignature()}";

                        if (RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method).equals(mFQN)) {
                            Usage usageModel = new Usage(
                                    projectId,
                                    projectType,
                                    RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method),
                                    SymbolKind.SYMBOL_KIND_METHOD,
                                    SymbolUse.SYMBOL_USE_VIRTUAL_INVOCATION,
                                    position,
                                    projectLocation
                            );
                            if (!usageModelCollection.contains(usageModel)) {
                                usageModelCollection.add(usageModel);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
        super.visitCtFieldRead(fieldRead);

        if (!filter.matches(fieldRead)) {
            return;
        }

        String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(fieldRead);
        SourceLocation position = convertSpoonPosition(fieldRead.getPosition());

        for (TypeDecl apiType : api.getExportedTypes().toList()) {
            for (FieldDecl field : apiType.getAllFields().toList()) {
                if (field.getQualifiedName().equals(fullyQualifiedName)) {
                    Usage usageModel = new Usage(
                            projectId,
                            projectType,
                            fullyQualifiedName,
                            SymbolKind.SYMBOL_KIND_FIELD,
                            SymbolUse.SYMBOL_USE_INSTANCE_FIELD_READ,
                            position,
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usageModel)) {
                        usageModelCollection.add(usageModel);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
        super.visitCtFieldWrite(fieldWrite);

        if (!filter.matches(fieldWrite)) {
            return;
        }

        String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(fieldWrite);
        SourceLocation position = convertSpoonPosition(fieldWrite.getPosition());

        for (TypeDecl apiType : api.getExportedTypes().toList()) {
            for (FieldDecl field : apiType.getAllFields().toList()) {
                if (field.getQualifiedName().equals(fullyQualifiedName)) {
                    Usage usageModel = new Usage(
                            projectId,
                            projectType,
                            fullyQualifiedName,
                            SymbolKind.SYMBOL_KIND_FIELD,
                            SymbolUse.SYMBOL_USE_INSTANCE_FIELD_WRITE,
                            position,
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usageModel)) {
                        usageModelCollection.add(usageModel);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
        super.visitCtConstructorCall(ctConstructorCall);

        if (!filter.matches(ctConstructorCall)) {
            return;
        }

        if (ctConstructorCall.getExecutable().getDeclaringType() == null) {
            return;
        }

        String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(ctConstructorCall);
        SourceLocation position = convertSpoonPosition(ctConstructorCall.getPosition());

        for (ClassDecl apiType : api.getExportedClasses().toList()) {
            for (ConstructorDecl constructor : apiType.getConstructors()) {
                if (RoseauFQNGenertor.getFullyQualifiedNameFromRoseauConstructorDecl(constructor).equals(fullyQualifiedName)) {
                    Usage usageModel = new Usage(
                            projectId,
                            projectType,
                            fullyQualifiedName,
                            SymbolKind.SYMBOL_KIND_CONSTRUCTOR,
                            SymbolUse.SYMBOL_USE_INVOCATION,
                            position,
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usageModel)) {
                        usageModelCollection.add(usageModel);
                    }
                    usageModel = new Usage(
                            projectId,
                            projectType,
                            ctConstructorCall.getExecutable().getDeclaringType().getQualifiedName(),
                            SymbolKind.SYMBOL_KIND_CLASS,
                            SymbolUse.SYMBOL_USE_INSTANTIATION,
                            position,
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usageModel)) {
                        usageModelCollection.add(usageModel);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
        super.visitCtTypeReference(reference);

        if (!filter.matches(reference)) {
            return;
        }

        String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(reference);

        SourceLocation position = convertSpoonPosition(reference.getPosition());

        for (TypeDecl apiType : api.getExportedTypes().toList()) {
            if (apiType.getQualifiedName().equals(fullyQualifiedName)) {
                Usage usageModel = new Usage(
                        projectId,
                        projectType,
                        fullyQualifiedName,
                        SymbolKind.SYMBOL_KIND_TYPE,
                        SymbolUse.SYMBOL_USE_TYPE_REFERENCE,
                        position,
                        projectLocation
                );
                if (!usageModelCollection.contains(usageModel)) {
                    usageModelCollection.add(usageModel);
                }
                return;
            }
        }
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        super.visitCtClass(ctClass);

        if (!filter.matches(ctClass)) {
            return;
        }

        SourceLocation position = convertSpoonPosition(ctClass.getPosition());

        for (CtTypeReference<?> superInterface : ctClass.getSuperInterfaces()) {
            String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(superInterface);
            for (TypeDecl apiType : api.getExportedTypes().toList()) {
                if (apiType.getQualifiedName().equals(fullyQualifiedName)) {
                    Usage usageModel = new Usage(
                            projectId,
                            projectType,
                            fullyQualifiedName,
                            SymbolKind.SYMBOL_KIND_INTERFACE,
                            SymbolUse.SYMBOL_USE_IMPLEMENTATION,
                            position,
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usageModel)) {
                        usageModelCollection.add(usageModel);
                    }
                    break;
                }
            }
        }

        CtTypeReference<?> superClass = ctClass.getSuperclass();
        if (superClass != null) {
            String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(superClass);
            for (TypeDecl apiType : api.getExportedTypes().toList()) {
                if (apiType.getQualifiedName().equals(fullyQualifiedName)) {
                    Usage usageModel = new Usage(
                            projectId,
                            projectType,
                            fullyQualifiedName,
                            SymbolKind.SYMBOL_KIND_CLASS,
                            SymbolUse.SYMBOL_USE_INHERITANCE,
                            position,
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usageModel)) {
                        usageModelCollection.add(usageModel);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> intrface) {
        super.visitCtInterface(intrface);

        if (!filter.matches(intrface)) {
            return;
        }

        SourceLocation position = convertSpoonPosition(intrface.getPosition());

        for (CtTypeReference<?> superInterface : intrface.getSuperInterfaces()) {
            String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(superInterface);

            for (TypeDecl apiType : api.getExportedTypes().toList()) {
                if (apiType.getQualifiedName().equals(fullyQualifiedName)) {
                    Usage usageModel = new Usage(
                            projectId,
                            projectType,
                            fullyQualifiedName,
                            SymbolKind.SYMBOL_KIND_INTERFACE,
                            SymbolUse.SYMBOL_USE_EXTENSION,
                            position,
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usageModel)) {
                        usageModelCollection.add(usageModel);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> method) {
        super.visitCtMethod(method);

        if (!filter.matches(method)) {
            return;
        }

        SourceLocation position = convertSpoonPosition(method.getPosition());

        // To optimize, we should start by fetching all methods from the API
        // that *could* be overridden in client code; and then build the
        // top definition list only for those methods that have a matching name

        Collection<CtMethod<?>> topDefinitions = method.getTopDefinitions();
        for (TypeDecl apiType : api.getExportedTypes().toList()) {
            for (MethodDecl m : apiType.getAllMethods().toList()) {
                for (CtMethod<?> topMethod : topDefinitions) {
                    String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(topMethod);

                    if (RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(m).equals(fullyQualifiedName)) {
                        Usage usageModel = new Usage(
                                projectId,
                                projectType,
                                fullyQualifiedName,
                                SymbolKind.SYMBOL_KIND_METHOD,
                                SymbolUse.SYMBOL_USE_OVERRIDING,
                                position,
                                projectLocation
                        );
                        if (!usageModelCollection.contains(usageModel)) {
                            usageModelCollection.add(usageModel);
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T> void visitCtLambda(CtLambda<T> lambda) {
        super.visitCtLambda(lambda);

        if (!filter.matches(lambda)) {
            return;
        }

        // Two potential uses for lambdas; (i) they implement a (functional) interface
        CtMethod<?> overriddenMethod = lambda.getOverriddenMethod();

        if (overriddenMethod == null) {
            return;
        }

        CtType<?> declaringType = overriddenMethod.getDeclaringType();
        SourceLocation position = convertSpoonPosition(lambda.getPosition());
        for (TypeDecl apiType : api.getExportedTypes().toList()) {
            if (apiType.getQualifiedName().equals(SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(declaringType))) {
                Usage usageModel = new Usage(
                        projectId,
                        projectType,
                        declaringType.getQualifiedName(),
                        SymbolKind.SYMBOL_KIND_INTERFACE,
                        SymbolUse.SYMBOL_USE_IMPLEMENTATION,
                        position,
                        projectLocation
                );
                if (!usageModelCollection.contains(usageModel)) {
                    usageModelCollection.add(usageModel);
                }
                break;
            }
        }

        // (ii) They override a method from the interface they implement
        String methodFqn = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(overriddenMethod);
        for (TypeDecl apiType : api.getExportedTypes().toList()) {
            for (MethodDecl method : apiType.getAllMethods().toList()) {
                if (RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(method).equals(methodFqn)) {
                    Usage usageModel = new Usage(
                            projectId,
                            projectType,
                            methodFqn,
                            SymbolKind.SYMBOL_KIND_METHOD,
                            SymbolUse.SYMBOL_USE_OVERRIDING,
                            position,
                            projectLocation
                    );
                    if (!usageModelCollection.contains(usageModel)) {
                        usageModelCollection.add(usageModel);
                    }
                    break;
                }
            }
        }

        // TODO: do we want to also retrieve and mark uses for the top definitions?
        // Collection<CtMethod<?>> topDefinitions = overriddenMethod.getTopDefinitions();
        /*for (TypeDecl apiType : api.getExportedTypes().toList()) {
            for (MethodDecl m : apiType.getAllMethods().toList()) {
                for (CtMethod<?> topMethod : topDefinitions) {
                    CtType<?> topMethodDeclaringType = topMethod.getDeclaringType();
                    String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(topMethod);
                    if (RoseauFQNGenertor.getFullyQualifiedNameFromRoseauMethodDecl(m).equals(fullyQualifiedName)) {
                        Usage usageModel = new Usage(
                                projectId,
                                projectType,
                                fullyQualifiedName,
                                SymbolKind.SYMBOL_KIND_METHOD,
                                SymbolUse.SYMBOL_USE_OVERRIDING,
                                position,
                                projectLocation
                        );
                        if (!usageModelCollection.contains(usageModel)) {
                            usageModelCollection.add(usageModel);
                        }
                    }
                }
            }
        }*/
    }

    public List<Usage> getUsageModelCollection() {
        return usageModelCollection.stream().sorted().toList();
    }
}
