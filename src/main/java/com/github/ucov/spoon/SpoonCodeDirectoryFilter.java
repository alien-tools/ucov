package com.github.ucov.spoon;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class SpoonCodeDirectoryFilter extends AbstractFilter<CtElement> {
    private final ArrayList<Path> paths;
    private final boolean invert;

    public SpoonCodeDirectoryFilter(ArrayList<Path> paths, boolean invert) {
        this.paths = paths;
        this.invert = invert;
    }

    @Override
    public boolean matches(CtElement element) {
        boolean matched = false;

        if (element.getPosition().getFile() != null) {
            String elementPath = element.getPosition().getFile().getAbsolutePath();

            for (Path path : paths) {
                boolean matches = elementPath.toLowerCase().startsWith(path.toAbsolutePath().toString().toLowerCase() + File.separatorChar);
                if (matches) {
                    matched = true;
                    break;
                }
            }
        }

        return invert != matched;
    }
}
