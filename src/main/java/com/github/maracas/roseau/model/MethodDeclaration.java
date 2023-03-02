package com.github.maracas.roseau.model;

import com.github.ucov.models.UsagePosition;

import java.util.List;

/**
 * Represents a method declaration within a Java type.
 * This class extends the {@link ElementDeclaration} class and contains information about the method's parameters, return type, thrown exceptions, and more.
 */
public class MethodDeclaration extends ElementDeclaration {

    /**
     * The return data type of the method.
     */
    private String returnType;

    /**
     * List of the method's parameter data types.
     */
    private List<String> parametersTypes;

    /**
     * List of referenced types for each parameter.
     */
    private List<List<String>> parametersReferencedTypes;

    /**
     * List of the method's formal type parameters.
     */
    private List<String> formalTypeParameters;

    private List<List<String>> formalTypeParamsBounds;

    /**
     * The method's signature.
     */
    private Signature signature;

    /**
     * List of exceptions thrown by the method.
     */
    private List<String> exceptions;

    /**
     * List of boolean values indicating varargs status for each parameter.
     */
    private List<Boolean> parametersVarargsCheck;

    /**
     * A flag indicating whether the method is a default method.
     */
    private boolean isDefault;

    public MethodDeclaration() {

    }

    public MethodDeclaration(String fullyQualifiedName, String name, AccessModifier visibility, String returnType, List<String> referencedTypes, List<String> parametersTypes, List<List<String>> parametersReferencedTypes, List<String> formalTypeParameters, List<List<String>> formalTypeParamsBounds, List<NonAccessModifiers> Modifiers, Signature signature, List<String> exceptions, List<Boolean> parametersVarargsCheck, boolean isDefault, UsagePosition position) {
        super(fullyQualifiedName, name, visibility, Modifiers, referencedTypes, position);
        this.returnType = returnType;
        this.parametersTypes = parametersTypes;
        this.parametersReferencedTypes = parametersReferencedTypes;
        this.formalTypeParameters = formalTypeParameters;
        this.formalTypeParamsBounds = formalTypeParamsBounds;
        this.signature = signature;
        this.exceptions = exceptions;
        this.parametersVarargsCheck = parametersVarargsCheck;
        this.isDefault = isDefault;

    }

    /**
     * Retrieves the return data type of the method.
     *
     * @return Method's return data type
     */
    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Retrieves the list of parameter data types of the method.
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
     * Retrieves the list of referenced types for each parameter of the method.
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
     * Retrieves the method's formal type parameters.
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
     * Retrieves the signature of the method.
     *
     * @return The method's signature
     */
    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    /**
     * Retrieves the list of exceptions thrown by the method.
     *
     * @return List of exceptions thrown by the method
     */
    public List<String> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * Retrieves the list of boolean values indicating varargs status for each parameter.
     *
     * @return List of varargs status for parameters
     */
    public List<Boolean> getParametersVarargsCheck() {
        return parametersVarargsCheck;
    }

    public void setParametersVarargsCheck(List<Boolean> parametersVarargsCheck) {
        this.parametersVarargsCheck = parametersVarargsCheck;
    }

    /**
     * Checks if the method is a default method.
     *
     * @return True if the method is a default method, false otherwise
     */
    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    /**
     * Generates a string representation of the MethodDeclaration.
     *
     * @return A formatted string containing the method's name, return type, parameter types,
     * visibility, modifiers, type, exceptions, and position.
     */
    @Override
    public String toString() {
        return "Method Name: " + getName() + "\n" +
                "Return Type: " + getReturnType() + "\n" +
                "Parameter Types: " + getParametersTypes() + "\n" +
                "Visibility: " + getVisibility() + "\n" +
                "Modifiers: " + getModifiers() + "\n" +
                //"Type: " + getType().getName() + "\n" +
                "Exceptions: " + getExceptions() + "\n" +
                "Position: " + getPosition() + "\n\n";
    }
}
