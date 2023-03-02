package com.github.ucov.reports.html.types;

public record UsageElement(int elementId,
                           String packageName,
                           String className,
                           String name,
                           String filePath,
                           int beginLine,
                           int endLine,
                           int beginColumn,
                           int endColumn,
                           UsageType usageType,
                           String role,
                           Context context) {
}
