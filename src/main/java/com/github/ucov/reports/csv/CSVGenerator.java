package com.github.ucov.reports.csv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.maracas.roseau.api.model.API;
import com.github.ucov.Main;
import com.github.ucov.models.Usage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class CSVGenerator {
    public static void writeApiUsageReport(Path usageReportOutputPath, ArrayList<Usage> usageModels) {
        Main.UCOV_LOGGER.info("Writing usage report CSV...");
        try {
            Files.write(usageReportOutputPath, usageModels.stream().map(Usage::toCSVRowString).sorted().toList(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            Main.UCOV_LOGGER.info("An error occurred while writing the report.");
            Main.UCOV_LOGGER.info(e.getMessage());
        }
    }

    // Initialize usage report CSV file (empty, new file, header)
    public static void InitializeUsageReport(Path usageReportOutputPath) throws IOException {
        Files.deleteIfExists(usageReportOutputPath);
        /*String SEPARATOR = "|";
        String headerStringCsv = "projectId" + SEPARATOR + "projectType" + SEPARATOR + "projectLocation" + SEPARATOR + "usagePosition" + SEPARATOR + "usageEndPosition" + SEPARATOR + "usageFullyQualifiedName" + SEPARATOR + "usageKind" + SEPARATOR + "usageType";
        Files.write(usageReportOutputPath, Arrays.stream(new String[]{headerStringCsv}).toList(), StandardOpenOption.CREATE);*/
    }

    public static void writeApiModelReport(Path apiReportOutputPath, API mainProjectApiModel) {
        Main.UCOV_LOGGER.info("Writing API report JSON...");

        try {
            mainProjectApiModel.writeJson(apiReportOutputPath);
        } catch (IOException e) {
            Main.UCOV_LOGGER.info("An error occurred while writing the report.");
            Main.UCOV_LOGGER.info(e.getMessage());
        }
    }
}
