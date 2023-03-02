package com.github.ucov.models;

import java.nio.file.Path;
import java.util.Objects;

public record Usage(String projectId, String projectType, String fullyQualifiedName, SymbolKind usageKind,
                    SymbolUse usageType, UsagePosition usagePosition,
                    Path projectLocation) implements Comparable<Usage> {

    public String toCSVRowString() {
        String SEPARATOR = "|";

        return projectId + SEPARATOR +
                projectType + SEPARATOR +
                projectLocation.toAbsolutePath().toString().replace('\\', '/') + SEPARATOR +
                usagePosition.getPositionAsString(projectLocation) + SEPARATOR +
                usagePosition.getEndPositionAsString(projectLocation) + SEPARATOR +
                fullyQualifiedName + SEPARATOR +
                usageKind + SEPARATOR +
                usageType;
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
