package com.github.ucov.spoon.visitors;

import com.github.maracas.roseau.model.*;
import com.github.ucov.models.SymbolKind;
import com.github.ucov.models.SymbolUse;
import com.github.ucov.models.Usage;
import com.github.ucov.models.UsagePosition;
import spoon.reflect.code.*;
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
        UsagePosition position = UsagePosition.getFromSourcePosition(invocation.getPosition());

        Collection<CtMethod<?>> topDefinitions = null;

        if (executable.getExecutableDeclaration() instanceof CtMethod<?> ctMethod) {
            topDefinitions = ctMethod.getTopDefinitions();
        }

        for (TypeDeclaration apiType : api.getallTheTypes()) {
            for (MethodDeclaration method : apiType.getMethods()) {
                if (method.getFullyQualifiedName().equals(fullyQualifiedName)) {
                    if (!method.getModifiers().contains(NonAccessModifiers.ABSTRACT)) {
                        boolean isStatic = method.getModifiers().contains(NonAccessModifiers.STATIC);
                        Usage usageModel = new Usage(
                                projectId,
                                projectType,
                                method.getFullyQualifiedName(),
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
                                method.getFullyQualifiedName(),
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
                        String mFQN = declaringType.getQualifiedName() + "." + topMethod.getSignature();
                        if (method.getFullyQualifiedName().equals(mFQN)) {
                            Usage usageModel = new Usage(
                                    projectId,
                                    projectType,
                                    method.getFullyQualifiedName(),
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
        UsagePosition position = UsagePosition.getFromSourcePosition(fieldRead.getPosition());

        for (TypeDeclaration apiType : api.getallTheTypes()) {
            for (FieldDeclaration field : apiType.getFields()) {
                if (field.getFullyQualifiedName().equals(fullyQualifiedName)) {
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
        UsagePosition position = UsagePosition.getFromSourcePosition(fieldWrite.getPosition());

        for (TypeDeclaration apiType : api.getallTheTypes()) {
            for (FieldDeclaration field : apiType.getFields()) {
                if (field.getFullyQualifiedName().equals(fullyQualifiedName)) {
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
        UsagePosition position = UsagePosition.getFromSourcePosition(ctConstructorCall.getPosition());

        for (TypeDeclaration apiType : api.getallTheTypes()) {
            for (ConstructorDeclaration constructor : apiType.getConstructors()) {
                if (constructor.getFullyQualifiedName().equals(fullyQualifiedName)) {
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

        UsagePosition position = UsagePosition.getFromSourcePosition(reference.getPosition());

        for (TypeDeclaration apiType : api.getallTheTypes()) {
            if (apiType.getFullyQualifiedName().equals(fullyQualifiedName)) {
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

        UsagePosition position = UsagePosition.getFromSourcePosition(ctClass.getPosition());

        for (CtTypeReference<?> superInterface : ctClass.getSuperInterfaces()) {
            String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(superInterface);
            for (TypeDeclaration apiType : api.getallTheTypes()) {
                if (apiType.getFullyQualifiedName().equals(fullyQualifiedName)) {
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
            for (TypeDeclaration apiType : api.getallTheTypes()) {
                if (apiType.getFullyQualifiedName().equals(fullyQualifiedName)) {
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

        UsagePosition position = UsagePosition.getFromSourcePosition(intrface.getPosition());

        for (CtTypeReference<?> superInterface : intrface.getSuperInterfaces()) {
            String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(superInterface);

            for (TypeDeclaration apiType : api.getallTheTypes()) {
                if (apiType.getFullyQualifiedName().equals(fullyQualifiedName)) {
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

        UsagePosition position = UsagePosition.getFromSourcePosition(method.getPosition());

        // To optimize, we should start by fetching all methods from the API
        // that *could* be overridden in client code; and then build the
        // top definition list only for those methods that have a matching name

        Collection<CtMethod<?>> topDefinitions = method.getTopDefinitions();
        for (TypeDeclaration apiType : api.getallTheTypes()) {
            for (MethodDeclaration m : apiType.getMethods()) {
                for (CtMethod<?> topMethod : topDefinitions) {
                    String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(topMethod);
                    if (m.getFullyQualifiedName().equals(fullyQualifiedName)) {
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
        UsagePosition position = UsagePosition.getFromSourcePosition(lambda.getPosition());
        for (TypeDeclaration apiType : api.getallTheTypes()) {
            if (apiType.getFullyQualifiedName().equals(SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(declaringType))) {
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
        for (TypeDeclaration apiType : api.getallTheTypes()) {
            for (MethodDeclaration method : apiType.getMethods()) {
                if (method.getFullyQualifiedName().equals(methodFqn)) {
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
        /*for (TypeDeclaration apiType : api.getAllTheTypes()) {
            for (MethodDeclaration m : apiType.getMethods()) {
                for (CtMethod<?> topMethod : topDefinitions) {
                    CtType<?> topMethodDeclaringType = topMethod.getDeclaringType();
                    String fullyQualifiedName = SpoonFullyQualifiedNameExtractor.getFullyQualifiedName(topMethod);
                    if (m.getFullyQualifiedName().equals(fullyQualifiedName)) {
                        Usage usageModel = new Usage(
                                projectId,
                                projectType,
                                fullyQualifiedName,
                                SymbolKind.SYMBOL_KIND_METHOD,
                                SymbolUse.SYMBOL_USE_OVERRIDING,
                                position,
                                projectLocation
                        );
                        usageModelCollection.add(usageModel);
                    }
                }
            }
        }*/
    }

    public List<Usage> getUsageModelCollection() {
        return usageModelCollection.stream().sorted().toList();
    }
}
