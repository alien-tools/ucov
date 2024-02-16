package com.github.ucov;

import com.github.maracas.roseau.api.model.API;
import com.github.ucov.models.Usage;
import com.github.ucov.spoon.SpoonCodeDirectoryFilter;
import com.github.ucov.spoon.SpoonLauncherUtilities;
import com.github.ucov.spoon.visitors.SpoonApiModelVisitor;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

import java.nio.file.Path;
import java.util.*;

public class UCovLibraryClientsProject {
    private final Map<Path, EnumSet<CodeType>> internalReferences = new HashMap<>();

    public void addInternalReference(Path path, EnumSet<CodeType> codeTypes) {
        internalReferences.put(path, codeTypes);
    }

    private CtModel getInternalReferencesModel(Map<Path, EnumSet<CodeType>> externalReferences) {
        Launcher launcher = SpoonLauncherUtilities.getCommonLauncherInstance();

        for (Map.Entry<Path, EnumSet<CodeType>> externalReference : externalReferences.entrySet()) {
            SpoonLauncherUtilities.applyProjectToLauncher(launcher, externalReference.getKey(), externalReference.getValue());
        }

        for (Map.Entry<Path, EnumSet<CodeType>> internalReference : internalReferences.entrySet()) {
            SpoonLauncherUtilities.applyProjectToLauncher(launcher, internalReference.getKey(), internalReference.getValue());
        }

        return launcher.buildModel();
    }

    private ArrayList<Path> getClientReferencesPaths() {
        ArrayList<Path> paths = new ArrayList<>();

        for (Map.Entry<Path, EnumSet<CodeType>> internalReference : internalReferences.entrySet()) {
            for (Path p : SpoonLauncherUtilities.getProjectPaths(internalReference.getKey(), internalReference.getValue())) {
                if (!paths.contains(p)) {
                    paths.add(p);
                }
            }
        }

        return paths;
    }

    private Filter<CtElement> getClientReferencesFilter() {
        // Paths to all sources owned by internal references (Clients)
        ArrayList<Path> clientReferencesPaths = getClientReferencesPaths();

        // Filter to match internal clients only
        return new SpoonCodeDirectoryFilter(clientReferencesPaths, false);
    }

    public List<Usage> getUsageModels(API api, Map<Path, EnumSet<CodeType>> externalReferences, String projectId, String projectType, Path projectLocation) {
        // The visitor
        SpoonApiModelVisitor visitor = new SpoonApiModelVisitor(api, getClientReferencesFilter(), projectId, projectType, projectLocation);

        CtModel model;
        try {
            model = getInternalReferencesModel(externalReferences);
        } catch (Exception ignored) {
            return new ArrayList<>();
        }

        // Run through the AST
        model.getAllModules().forEach(t -> t.accept(visitor));

        // Get the usage models
        return visitor.getUsageModelCollection();
    }
}
