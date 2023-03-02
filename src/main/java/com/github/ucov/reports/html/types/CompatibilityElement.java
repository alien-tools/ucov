package com.github.ucov.reports.html.types;

public record CompatibilityElement(
        int compatibilityID,
        String packageName,
        String fileName,
        int nbFieldTested,
        int nbFieldHalfTested,
        int nbFieldNoTested,
        int nbFieldTotal,
        int nbMethodTested,
        int nbMethodHalfTested,
        int nbMethodNoTested,
        int nbMethodTotal,
        int nbTypeTested,
        int nbTypeHalfTested,
        int nbTypeNoTested,
        int nbTypeTotal,
        int nbConstructorTested,
        int nbConstructorHalfTested,
        int nbConstructorNoTested,
        int nbConstructorTotal
) {
}
