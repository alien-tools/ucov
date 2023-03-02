package com.github.maracas.roseau.model;

import com.github.ucov.models.UsagePosition;

import java.util.List;

/**
 * This abstract class represents an element declaration, which can be a type,
 * a method, a constructor, or a field in the library.
 * <p>
 * It provides information about the element's qualified name, visibility, non-access modifiers, referenced types,
 * and position within the source code.
 */
public abstract class ElementDeclaration {
    private String fullyQualifiedName;

    /**
     * The name of the element.
     */
    private String name;

    /**
     * The visibility of the element.
     */
    private AccessModifier visibility;

    /**
     * List of non-access modifiers applied to the element.
     */
    private List<NonAccessModifiers> Modifiers;

    /**
     * List of types referenced by the element.
     */
    private List<String> referencedTypes;

    /**
     * The exact position of the element declaration
     */
    private UsagePosition position;

    public ElementDeclaration() {

    }

    public ElementDeclaration(String fullyQualifiedName, String name, AccessModifier visibility, List<NonAccessModifiers> Modifiers, List<String> referencedTypes, UsagePosition position) {
        this.name = name;
        this.visibility = visibility;
        this.Modifiers = Modifiers;
        this.referencedTypes = referencedTypes;
        this.position = position;
        this.fullyQualifiedName = fullyQualifiedName;
    }

    /**
     * Retrieves the name of the element.
     *
     * @return Element's name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the visibility of the element.
     *
     * @return Element's visibility
     */
    public AccessModifier getVisibility() {
        return visibility;
    }

    public void setVisibility(AccessModifier visibility) {
        this.visibility = visibility;
    }

    /**
     * Retrieves the list of non-access modifiers applied to the element.
     *
     * @return Element's non-access modifiers
     */
    public List<NonAccessModifiers> getModifiers() {
        return Modifiers;
    }

    public void setModifiers(List<NonAccessModifiers> modifiers) {
        Modifiers = modifiers;
    }

    /**
     * Retrieves the list of types referenced by this element.
     *
     * @return List of types referenced by the element
     */
    public List<String> getReferencedTypes() {
        return referencedTypes;
    }

    public void setReferencedTypes(List<String> referencedTypes) {
        this.referencedTypes = referencedTypes;
    }

    /**
     * Retrieves the position of the element declaration.
     *
     * @return Element's position.
     */
    public UsagePosition getPosition() {
        return position;
    }

    public void setPosition(UsagePosition position) {
        this.position = position;
    }

    /**
     * Retrieves the fully qualified name of this element.
     *
     * @return Element's fully qualified name.
     */
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }
}
