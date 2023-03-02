package com.github.ucov;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maracas.roseau.model.API;
import com.github.ucov.models.Project;
import com.github.ucov.models.ProjectType;
import com.github.ucov.models.Usage;
import com.github.ucov.reports.csv.CSVGenerator;
import com.github.ucov.suf.SUFGenerator;
import com.github.ucov.sum.SUMGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static final Logger UCOV_LOGGER = LogManager.getLogger();

    /**
     * Prints version, naming, and copyright information
     */
    private static void printBanner() {
        UCOV_LOGGER.info("UCov - Java Library Usage Coverage Static Source Analyser");
        UCOV_LOGGER.info("Version 1.0.0.0");
        UCOV_LOGGER.info("");
    }

    /**
     * Prints usage information to the console
     */
    private static void printUsage() {
        UCOV_LOGGER.info("Usage: <API JSON Report Output Location> " +
                "<Usage Model CSV Report Output Location> " +
                "<Usage Footprint CSV Report Output Location> " +
                "<Project ID 1;Project Type 1;Project Directory 1> " +
                "<Project ID 2;Project Type 2;Project Directory 2> " +
                "<Project ID 3;Project Type 3;Project Directory 3> ...");

        UCOV_LOGGER.info("Example: java -jar ucov.jar " +
                "\"I:\\UCov\\Projects\\bcel.API.json\" " +
                "\"I:\\UCov\\Projects\\bcel.SUM.csv\" " +
                "\"I:\\UCov\\Projects\\bcel.SUF.csv\" " +
                "\"commons-bcel;MAIN;I:\\UCov\\Projects\\commons-bcel\" " +
                "\"commons-bcel;TEST;I:\\UCov\\Projects\\commons-bcel\" " +
                "\"commons-bcel;SAMPLE;I:\\UCov\\Projects\\commons-bcel\" " +
                "\"haidnorJVM;CLIENT;I:\\UCov\\Projects\\clients\\FranzHaidnor\\haidnorJVM\" " +
                "\"commons-vfs;CLIENT;I:\\UCov\\Projects\\clients\\apache\\commons-vfs\"");
    }

    /**
     * Converts a string representation of a UCov project into an object
     * e.g.: "commons-bcel;MAIN;I:\UCov\Projects\commons-bcel" -> Project (Object)
     *
     * @param argument The cli argument to convert
     * @return A project object matching the provided argument
     */
    private static Project parseProjectCommandLineOption(String argument) {
        String[] elements = argument.split(";");
        String id = elements[0];
        String type = elements[1];
        String dir = elements[2];

        ProjectType typeVal = Enum.valueOf(ProjectType.class, type);
        Path dirVal = Path.of(dir);

        return new Project(id, typeVal, dirVal);
    }

    /**
     * This method deserializes an existing API JSON on disk
     *
     * @param apiReportOutputPath The path to the existing API JSON file
     * @return The deserialized API model if successful
     * @throws IOException if the deserialization fails
     */
    private static API readApiModelReport(Path apiReportOutputPath) throws IOException {
        UCOV_LOGGER.info("Reading API report JSON...");
        API mainProjectApiModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            mainProjectApiModel = mapper.readerFor(API.class).readValue(apiReportOutputPath.toFile());
        } catch (IOException e) {
            UCOV_LOGGER.info("An error occurred while reading the report.");
            UCOV_LOGGER.info(e.getMessage());
            throw e;
        }

        return mainProjectApiModel;
    }

    /**
     * Our main program entry point (CLI)
     *
     * @param args The arguments to our program
     * @throws Exception upon failure
     */
    public static void main(String[] args) throws Exception {
        // Version, Name, Copyright info
        printBanner();

        // We require an api output location, a sum location, a suf location, and a main project at the very least.
        if (args.length < 4) {
            printUsage();
            return;
        }

        // Parse all possible arguments
        String apiReportOutputLocation = args[0];
        String usageModelReportOutputLocation = args[1];
        String usageFootprintReportOutputLocation = args[2];
        String[] projectArguments = Arrays.stream(args).skip(3).toArray(String[]::new);

        // Transform command line arguments into something usable
        Path apiReportOutputPath = Path.of(apiReportOutputLocation);
        Path sumLocationPath = Path.of(usageModelReportOutputLocation);
        Path usageReportOutputPath = Path.of(usageFootprintReportOutputLocation);
        ArrayList<Project> projects = new ArrayList<>(Arrays.stream(projectArguments).map(Main::parseProjectCommandLineOption).toList());

        // Retrieve the main project, if not provided, fail.
        Optional<Project> optionalMainProject = projects.stream().filter(t -> t.type() == ProjectType.MAIN).findFirst();
        if (optionalMainProject.isEmpty()) {
            throw new Exception("Main project was not specified!");
        }

        Project mainProject = optionalMainProject.get();
        UCOV_LOGGER.info("Processing " + mainProject);

        UCovLibraryProject libraryProject = new UCovLibraryProject();
        libraryProject.addExternalReference(mainProject.location(), EnumSet.of(CodeType.MAIN));

        API mainProjectApiModel = null;

        // Attempt to deserialize the existing API model first if found
        if (Files.exists(apiReportOutputPath)) {
            mainProjectApiModel = readApiModelReport(apiReportOutputPath);
        }

        // Either no existing API model json existed, or it failed to parse, recreate it
        // This mainly only saves execution time for generating multiple SUFs if needed
        // to run multiple times the tool due to command line argument size limitations on some
        // operating systems.
        if (mainProjectApiModel == null) {
            mainProjectApiModel = libraryProject.getAPIModels();
            CSVGenerator.writeApiModelReport(apiReportOutputPath, mainProjectApiModel);
        }

        // If the SUM doesn't already exist on disk, create it
        if (!Files.exists(sumLocationPath)) {
            CSVGenerator.InitializeUsageReport(sumLocationPath);
            ArrayList<Usage> sum = SUMGenerator.getSUM(mainProjectApiModel, mainProject);
            CSVGenerator.writeApiUsageReport(sumLocationPath, sum);
        }

        // If projects other than main were specified, generate a SUF.
        if (projects.stream().anyMatch(t -> t.type() != ProjectType.MAIN)) {
            CSVGenerator.InitializeUsageReport(usageReportOutputPath);
            ArrayList<Usage> usageModels = SUFGenerator.getSUF(mainProjectApiModel, libraryProject, projects);
            CSVGenerator.writeApiUsageReport(usageReportOutputPath, usageModels);
        }
    }
}
