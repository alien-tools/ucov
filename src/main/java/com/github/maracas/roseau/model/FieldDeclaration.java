package com.github.maracas.roseau.model;

import com.github.ucov.models.UsagePosition;

import java.util.List;

/**
 * Represents a field declaration in a Java type.
 * This class extends the {@link ElementDeclaration} class and contains information about the field's data type and the {@link TypeDeclaration} to which it belongs.
 */
public class FieldDeclaration extends ElementDeclaration {
    /**
     * The data type of the field (e.g., int, double, class types, interface types).
     */
    private String dataType;

    public FieldDeclaration() {

    }

    public FieldDeclaration(String fullyQualifiedName, String name, AccessModifier visibility, String dataType, List<NonAccessModifiers> Modifiers, List<String> referencedTypes, UsagePosition position) {
        super(fullyQualifiedName, name, visibility, Modifiers, referencedTypes, position);
        this.dataType = dataType;
    }

    /**
     * Retrieves the data type of the field (e.g., int, double, class types, interface types).
     *
     * @return Field's data type
     */
    public String getdataType() {
        return dataType;
    }

    public void setdataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * Generates a string representation of the FieldDeclaration.
     *
     * @return A formatted string containing the field's name, data type, type, visibility,
     * modifiers, and position.
     */
    @Override
    public String toString() {
        return "Field Name: " + getName() + "\n" +
                "Data Type: " + getdataType() + "\n" +
                //"Type: " + getType().getName() + "\n" +
                "Visibility: " + getVisibility() + "\n" +
                "Modifiers: " + getModifiers() + "\n" +
                "Position: " + getPosition() + "\n\n";

    }
}
