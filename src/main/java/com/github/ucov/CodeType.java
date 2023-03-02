package com.github.ucov;

import java.util.EnumSet;

public enum CodeType {
    MAIN,
    TEST,
    SAMPLE;

    public static final EnumSet<CodeType> ALL = EnumSet.allOf(CodeType.class);
}
