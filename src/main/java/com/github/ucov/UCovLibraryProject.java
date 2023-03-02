package com.github.ucov;

import com.github.maracas.roseau.APIExtractor;
import com.github.maracas.roseau.model.API;
import com.github.ucov.spoon.SpoonCodeDirectoryFilter;
import com.github.ucov.spoon.SpoonLauncherUtilities;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

import java.nio.file.Path;
import java.util.*;

public class UCovLibraryProject {
    private final Map<Path, EnumSet<CodeType>> externalReferences = new HashMap<>();
    private API cachedAPI = null;

    public void addExternalReference(Path path, EnumSet<CodeType> codeTypes) {
        externalReferences.put(path, codeTypes);
    }

    private CtModel getExternalReferencesModel() {
        Launcher launcher = SpoonLauncherUtilities.getCommonLauncherInstance();

        for (Map.Entry<Path, EnumSet<CodeType>> externalReference : externalReferences.entrySet()) {
            SpoonLauncherUtilities.applyProjectToLauncher(launcher, externalReference.getKey(), externalReference.getValue());
        }

        return launcher.buildModel();
    }

    private ArrayList<Path> getExternalReferencePaths() {
        ArrayList<Path> paths = new ArrayList<>();

        for (Map.Entry<Path, EnumSet<CodeType>> externalReference : externalReferences.entrySet()) {
            for (Path p : SpoonLauncherUtilities.getProjectPaths(externalReference.getKey(), externalReference.getValue())) {
                if (!paths.contains(p)) {
                    paths.add(p);
                }
            }
        }

        return paths;
    }

    public Map<Path, EnumSet<CodeType>> getExternalReferences() {
        return externalReferences;
    }

    private Filter<CtElement> getExternalReferenceFilter() {
        // Paths to all sources owned by external references (Libraries)
        ArrayList<Path> externalReferencePaths = getExternalReferencePaths();

        // Filter to match libraries only
        return new SpoonCodeDirectoryFilter(externalReferencePaths, false);
    }

    public API getAPIModels() {
        if (cachedAPI == null) {
            Main.UCOV_LOGGER.info("Fetching API models...");

            // API model for libraries
            cachedAPI = APIExtractor.getAPIFromCtModel(getExternalReferencesModel());
        }

        return cachedAPI;
    }
}
