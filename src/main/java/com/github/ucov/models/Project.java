package com.github.ucov.models;

import java.nio.file.Path;

public record Project(String id, ProjectType type, Path location) {
    @Override
    public String toString() {
        String SEPARATOR = "|";
        return id + SEPARATOR + type + SEPARATOR + location.toAbsolutePath();
    }
}
