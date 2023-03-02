package com.github.maracas.roseau.model;

import com.github.ucov.models.UsagePosition;

import java.util.List;

/**
 * Represents a constructor declaration within a Java type.
 * This class extends the {@link ElementDeclaration} class and contains information about the constructor's parameters, return type, class, and more.
 */
public class ConstructorDeclaration extends ElementDeclaration {

    /**
     * The return data type of the constructor.
     */
    private String returnType;

    /**
     * List of the constructor's parameter data types.
     */
    private List<String> parametersTypes;

    /**
     * List of referenced types for each constructor parameter.
     */
    private List<List<String>> parametersReferencedTypes;

    /**
     * List of the constructor's formal type parameters.
     */
    private List<String> formalTypeParameters;

    private List<List<String>> formalTypeParamsBounds;

    /**
     * The constructor's signature.
     */
    private Signature signature;

    /**
     * List of exceptions thrown by the constructor.
     */
    private List<String> exceptions;

    public ConstructorDeclaration() {

    }

    public ConstructorDeclaration(String fullyQualifiedName, String name, AccessModifier visibility, String returnType, List<String> referencedTypes, List<String> parametersTypes, List<List<String>> parametersReferencedTypes, List<String> formalTypeParameters, List<List<String>> formalTypeParamsBounds, List<NonAccessModifiers> Modifiers, Signature signature, List<String> exceptions, UsagePosition position) {
        super(fullyQualifiedName, name, visibility, Modifiers, referencedTypes, position);
        this.returnType = returnType;
        this.parametersTypes = parametersTypes;
        this.parametersReferencedTypes = parametersReferencedTypes;
        this.formalTypeParameters = formalTypeParameters;
        this.formalTypeParamsBounds = formalTypeParamsBounds;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    /**
     * Retrieves the return data type of the constructor.
     *
     * @return Constructor's return data type
     */
    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Retrieves the list of parameter data types of the constructor.
     *
     * @return List of parameter data types
     */
    public List<String> getParametersTypes() {
        return parametersTypes;
    }

    public void setParametersTypes(List<String> parametersTypes) {
        this.parametersTypes = parametersTypes;
    }

    /**
     * Retrieves the list of referenced types for each parameter of the constructor.
     *
     * @return Lists of referenced types for parameters
     */
    public List<List<String>> getParametersReferencedTypes() {
        return parametersReferencedTypes;
    }

    public void setParametersReferencedTypes(List<List<String>> parametersReferencedTypes) {
        this.parametersReferencedTypes = parametersReferencedTypes;
    }

    /**
     * Retrieves the constructor's formal type parameters.
     *
     * @return List of formal type parameters
     */
    public List<String> getFormalTypeParameters() {
        return formalTypeParameters;
    }

    public void setFormalTypeParameters(List<String> formalTypeParameters) {
        this.formalTypeParameters = formalTypeParameters;
    }

    /**
     * Retrieves a list of lists containing the formal type parameters' bounds.
     *
     * @return formal type parameters bounds
     */
    public List<List<String>> getFormalTypeParamsBounds() {
        return formalTypeParamsBounds;
    }

    public void setFormalTypeParamsBounds(List<List<String>> formalTypeParamsBounds) {
        this.formalTypeParamsBounds = formalTypeParamsBounds;
    }

    /**
     * Retrieves the signature of the constructor.
     *
     * @return The constructor's signature
     */
    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    /**
     * Retrieves the list of exceptions thrown by the constructor.
     *
     * @return List of exceptions thrown by the constructor
     */
    public List<String> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * Generates a string representation of the ConstructorDeclaration.
     *
     * @return A formatted string containing the constructor's name, type, return type, parameter types,
     * visibility, modifiers, exceptions, and position.
     */
    @Override
    public String toString() {
        return "Constructor Name: " + getName() + "\n" +
                //"Type: " + getType().getName() + "\n" +
                "Return Type: " + getReturnType() + "\n" +
                "Parameter Types: " + getParametersTypes() + "\n" +
                "Visibility: " + getVisibility() + "\n" +
                "Modifiers: " + getModifiers() + "\n" +
                "Exceptions: " + getExceptions() + "\n" +
                "Position: " + getPosition() + "\n\n";
    }
}
