package com.github.ucov;

import com.github.maracas.roseau.api.model.ConstructorDecl;
import com.github.maracas.roseau.api.model.MethodDecl;

public class RoseauFQNGenertor {
    public static String getFullyQualifiedNameFromRoseauMethodDecl(MethodDecl method) {
        String methodSignature = method.getSignature().replace(", ", ",");
        String methodQualifiedName = method.getQualifiedName();

        int methodNameLength = methodSignature.indexOf("(");

        String namespace = methodQualifiedName.substring(0, methodQualifiedName.length() - methodNameLength);

        return namespace + methodSignature;
    }

    public static String getFullyQualifiedNameFromRoseauConstructorDecl(ConstructorDecl constructor) {
        String constructorSignature = constructor.getSignature().replace(", ", ",");
        String constructorQualifiedName = constructor.getQualifiedName();

        int constructorNameLength = constructorSignature.indexOf("(");

        String namespace = constructorQualifiedName.substring(0, constructorQualifiedName.length() - constructorNameLength - 1);

        return namespace + constructorSignature.substring(constructorNameLength);
    }
}
