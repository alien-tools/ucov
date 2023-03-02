package com.github.ucov.reports.html.types;

public enum UsageType {
    FIELD_READ,
    FIELD_WRITE,

    METHOD_CALL,
    METHOD_OVERRIDE,

    TYPE_EXTEND,
    TYPE_IMPLEMENT,
    TYPE_REFERENCE,

    CONSTRUCTOR_CALL
}
