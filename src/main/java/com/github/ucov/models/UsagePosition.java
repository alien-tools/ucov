package com.github.ucov.models;

import spoon.reflect.cu.SourcePosition;

import java.nio.file.Path;

public record UsagePosition(String path, int line, int column, int endLine, int endColumn) {
    public static UsagePosition getFromSourcePosition(SourcePosition position) {
        String path = "";
        int line = -1;
        int column = -1;
        int endLine = -1;
        int endColumn = -1;

        if (position != null && position.isValidPosition()) {
            if (position.getFile() != null) {
                path = position.getFile().getAbsolutePath();
            }

            line = position.getLine();
            column = position.getColumn();
            endLine = position.getEndLine();
            endColumn = position.getEndColumn();
        }

        return new UsagePosition(path, line, column, endLine, endColumn);
    }

    public String getPositionAsString(Path projectLocation) {
        return path().replaceAll("(?i)" + projectLocation.toAbsolutePath().toString(), "").replace('\\', '/') + "(" + line() + ":" + column() + ")";
    }

    public String getEndPositionAsString(Path projectLocation) {
        return path().replaceAll("(?i)" + projectLocation.toAbsolutePath().toString(), "").replace('\\', '/') + "(" + endLine() + ":" + endColumn() + ")";
    }
}
