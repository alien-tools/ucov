package com.github.ucov.reports.html.types;

public class ApiElement {
    public final int elementId;
    public final String packageName;
    public final String className;
    public final String name;
    public final String filePath;
    public final String fileName;
    public final int beginLine;
    public final int endLine;
    public final int beginColumn;
    public final int endColumn;
    public final SymbolType symbolType;
    public final Visibility visibility;
    public final Modifier modifier;
    public CompatibilityStatus compatibilityTest;
    public CompatibilityStatus compatibilityClient;
    public CompatibilityStatus compatibilityExample;

    public ApiElement(int elementId,
                      String packageName,
                      String className,
                      String name,
                      String filePath,
                      String fileName,
                      int beginLine,
                      int endLine,
                      int beginColumn,
                      int endColumn,
                      SymbolType symbolType,
                      Visibility visibility,
                      Modifier modifier,
                      CompatibilityStatus compatibilityTest,
                      CompatibilityStatus compatibilityClient,
                      CompatibilityStatus compatibilityExample) {
        this.elementId = elementId;
        this.packageName = packageName;
        this.className = className;
        this.name = name;
        this.filePath = filePath;
        this.fileName = fileName;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.beginColumn = beginColumn;
        this.endColumn = endColumn;
        this.symbolType = symbolType;
        this.visibility = visibility;
        this.modifier = modifier;
        this.compatibilityTest = compatibilityTest;
        this.compatibilityClient = compatibilityClient;
        this.compatibilityExample = compatibilityExample;
    }

    public CompatibilityStatus compareStatus() {
        if (this.compatibilityTest == CompatibilityStatus.TESTED || this.compatibilityClient == CompatibilityStatus.TESTED || this.compatibilityExample == CompatibilityStatus.TESTED) {
            return CompatibilityStatus.TESTED;
        } else if (this.compatibilityTest == CompatibilityStatus.HALF_TESTED || this.compatibilityClient == CompatibilityStatus.HALF_TESTED || this.compatibilityExample == CompatibilityStatus.HALF_TESTED) {
            return CompatibilityStatus.HALF_TESTED;
        } else {
            return CompatibilityStatus.NO_TESTED;
        }
    }
}
