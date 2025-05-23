package com.github.ucov.models;

import com.github.maracas.roseau.api.model.SourceLocation;

import java.nio.file.Path;
import java.util.Objects;

public record Usage(String projectId, String projectType, String fullyQualifiedName, SymbolKind usageKind,
                    SymbolUse usageType, SourceLocation usagePosition,
                    Path projectLocation) implements Comparable<Usage> {

    public String toCSVRowString() {
        String SEPARATOR = "|";
        String filePath = "";

        String projectLocationStr = projectLocation.toAbsolutePath().toString();

        if (usagePosition.file() != null) {
            String usagePositionStr = usagePosition.file().toString();
            filePath = usagePositionStr.replaceAll("(?i)" + projectLocationStr, "").replace('\\', '/');
        }


        return projectId + SEPARATOR + projectType + SEPARATOR + projectLocationStr.replace('\\', '/') + SEPARATOR + filePath + "(" + usagePosition.line() + ")" + SEPARATOR + fullyQualifiedName + SEPARATOR + usageKind + SEPARATOR + usageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usage usage = (Usage) o;
        return Objects.equals(projectId, usage.projectId) && Objects.equals(projectType, usage.projectType) && Objects.equals(fullyQualifiedName, usage.fullyQualifiedName) && usageKind == usage.usageKind && usageType == usage.usageType && Objects.equals(usagePosition, usage.usagePosition) && Objects.equals(projectLocation, usage.projectLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, projectType, fullyQualifiedName, usageKind, usageType, usagePosition, projectLocation);
    }

    @Override
    public int compareTo(Usage o) {
        return toCSVRowString().compareTo(o.toCSVRowString());
    }
}
