package com.github.ucov.suf;

import com.github.maracas.roseau.api.model.API;
import com.github.ucov.CodeType;
import com.github.ucov.Main;
import com.github.ucov.UCovLibraryClientsProject;
import com.github.ucov.UCovLibraryProject;
import com.github.ucov.models.Project;
import com.github.ucov.models.ProjectType;
import com.github.ucov.models.Usage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SUFGenerator {
    private static List<Usage> Analyse(API mainProjectApiModel, UCovLibraryProject uCovLibraryProject, Project clientProject) {
        Main.UCOV_LOGGER.info(STR."Processing \{clientProject}");

        UCovLibraryClientsProject internalProject = new UCovLibraryClientsProject();
        Path projectLocation = clientProject.location();
        String projectId = clientProject.id();
        String projectType = clientProject.type().name();

        EnumSet<CodeType> enumSet = switch (clientProject.type()) {
            case LIBRARY_MAIN -> EnumSet.of(CodeType.MAIN);
            case LIBRARY_TEST -> EnumSet.of(CodeType.TEST);
            case LIBRARY_SAMPLE -> EnumSet.of(CodeType.SAMPLE);
            case CLIENT_ALL -> CodeType.ALL;
            case CLIENT_MAIN -> EnumSet.of(CodeType.MAIN);
            case CLIENT_TEST -> EnumSet.of(CodeType.TEST);
        };

        // Clients, Tests, Samples...
        internalProject.addInternalReference(projectLocation, enumSet);

        Main.UCOV_LOGGER.info("Fetching Usage models...");
        return internalProject.getUsageModels(mainProjectApiModel, uCovLibraryProject.getExternalReferences(), projectId, projectType, projectLocation);
    }

    public static ArrayList<Usage> getSUF(API mainProjectApiModel, UCovLibraryProject externalProject, ArrayList<Project> projects) {
        ArrayList<Usage> usageModels = new ArrayList<>();

        // Add project uses if provided
        List<Project> clientProjects = projects.stream().filter(t -> t.type() != ProjectType.LIBRARY_MAIN).toList();
        for (Project project : clientProjects) {
            for (Usage p : Analyse(mainProjectApiModel, externalProject, project)) {
                if (!usageModels.contains(p)) {
                    usageModels.add(p);
                }
            }
        }

        return usageModels;
    }
}