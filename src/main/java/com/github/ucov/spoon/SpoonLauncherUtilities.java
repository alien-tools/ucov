package com.github.ucov.spoon;

import com.github.ucov.CodeType;
import com.github.ucov.Main;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.SpoonException;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.SpoonPom;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.Pattern;

public class SpoonLauncherUtilities {
    public static Launcher getCommonLauncherInstance() {
        Launcher launcher = new Launcher();

        // Ignore missing types/classpath related errors
        launcher.getEnvironment().setNoClasspath(true);
        // Proceed even if we find the same type twice; affects the precision of the result
        launcher.getEnvironment().setIgnoreDuplicateDeclarations(true);
        // Ignore files with syntax/JLS violations and proceed
        launcher.getEnvironment().setIgnoreSyntaxErrors(true);

        return launcher;
    }

    private static ArrayList<Path> getPomProjectPaths(String mavenProject, spoon.MavenLauncher.SOURCE_TYPE sourceType) throws SpoonException {
        SpoonPom model;
        ArrayList<Path> paths = new ArrayList<>();

        File mavenProjectFile = new File(mavenProject);
        if (!mavenProjectFile.exists()) {
            throw new SpoonException(mavenProject + " does not exist.");
        }

        Pattern profileFilter = Pattern.compile("^$");

        try {
            model = new SpoonPom(mavenProject, sourceType, new StandardEnvironment(), profileFilter);
        } catch (Exception e) {
            throw new SpoonException("Unable to read the pom", e);
        }

        // app source
        if (spoon.MavenLauncher.SOURCE_TYPE.APP_SOURCE == sourceType || spoon.MavenLauncher.SOURCE_TYPE.ALL_SOURCE == sourceType) {
            List<File> sourceDirectories = model.getSourceDirectories();
            for (File sourceDirectory : sourceDirectories) {
                Main.UCOV_LOGGER.info("Detected Project MAIN Source Directory at: " + sourceDirectory);
                paths.add(sourceDirectory.toPath());
            }
        }

        // test source
        if (spoon.MavenLauncher.SOURCE_TYPE.TEST_SOURCE == sourceType || spoon.MavenLauncher.SOURCE_TYPE.ALL_SOURCE == sourceType) {
            List<File> testSourceDirectories = model.getTestDirectories();
            for (File sourceDirectory : testSourceDirectories) {
                Main.UCOV_LOGGER.info("Detected Project TEST Source Directory at: " + sourceDirectory);
                paths.add(sourceDirectory.toPath());
            }
        }

        return paths;
    }

    private static int getPomProjectSourceComplianceLevel(String mavenProject) throws SpoonException {
        SpoonPom model;

        File mavenProjectFile = new File(mavenProject);
        if (!mavenProjectFile.exists()) {
            throw new SpoonException(mavenProject + " does not exist.");
        }

        Pattern profileFilter = Pattern.compile("^$");

        try {
            model = new SpoonPom(mavenProject, MavenLauncher.SOURCE_TYPE.ALL_SOURCE, new StandardEnvironment(), profileFilter);
        } catch (Exception e) {
            throw new SpoonException("Unable to read the pom", e);
        }

        return model.getSourceVersion();
    }

    public static int getProjectSourceComplianceLevel(Path location) {
        try {
            return getPomProjectSourceComplianceLevel(location.toAbsolutePath().toString());
        } catch (Exception ignore) {

        }

        return 11;
    }

    private static Path getPossibleSamplePath(Path location) {
        Path examplesPath = location.resolve("examples");
        Path samplesPath = location.resolve("samples");
        Path srcPath = location.resolve("src");

        if (Files.exists(examplesPath)) {
            return examplesPath;
        }

        if (Files.exists(samplesPath)) {
            return samplesPath;
        }

        if (Files.exists(srcPath)) {
            return getPossibleSamplePath(srcPath);
        }

        return null;
    }

    private static Path getPossibleMainPath(Path location) {
        Path mainPath = location.resolve("main");
        Path srcPath = location.resolve("src");

        if (Files.exists(mainPath)) {
            return mainPath;
        }

        if (Files.exists(srcPath)) {
            return getPossibleMainPath(srcPath);
        }

        return null;
    }

    private static Path getPossibleTestPath(Path location) {
        Path testPath = location.resolve("test");
        Path srcPath = location.resolve("src");

        if (Files.exists(testPath)) {
            return testPath;
        }

        if (Files.exists(srcPath)) {
            return getPossibleTestPath(srcPath);
        }

        return null;
    }

    private static MavenLauncher.SOURCE_TYPE getSourceTypeValue(EnumSet<CodeType> codeTypes) throws InvalidParameterException {
        if (codeTypes.contains(CodeType.MAIN) && codeTypes.contains(CodeType.TEST)) {
            return MavenLauncher.SOURCE_TYPE.ALL_SOURCE;
        } else if (codeTypes.contains(CodeType.MAIN)) {
            return MavenLauncher.SOURCE_TYPE.APP_SOURCE;
        } else if (codeTypes.contains(CodeType.TEST)) {
            return MavenLauncher.SOURCE_TYPE.TEST_SOURCE;
        } else {
            throw new InvalidParameterException();
        }
    }

    public static Launcher getLauncherForProject(Path projectLocation, EnumSet<CodeType> codeTypes) {
        Launcher launcher = getCommonLauncherInstance();
        applyProjectToLauncher(launcher, projectLocation, codeTypes);
        return launcher;
    }

    public static void applyProjectToLauncher(Launcher launcher, Path projectLocation, EnumSet<CodeType> codeTypes) {
        launcher.getEnvironment().setComplianceLevel(getProjectSourceComplianceLevel(projectLocation));

        ArrayList<Path> paths = getProjectPaths(projectLocation, codeTypes);
        for (Path path : paths) {
            launcher.addInputResource(path.toAbsolutePath().toString());
        }
    }

    public static ArrayList<Path> getProjectPaths(Path projectLocation, EnumSet<CodeType> codeTypes) {
        ArrayList<Path> paths = new ArrayList<>();

        Main.UCOV_LOGGER.info("Trying to detect source directories for project: " + projectLocation + " with types: " + codeTypes);

        if (codeTypes.contains(CodeType.MAIN) || codeTypes.contains(CodeType.TEST)) {
            try {
                MavenLauncher.SOURCE_TYPE sourceType = getSourceTypeValue(codeTypes);
                for (Path p : getPomProjectPaths(projectLocation.toString(), sourceType)) {
                    if (!paths.contains(p)) {
                        paths.add(p);
                    }
                }
            } catch (Exception ignored) {
                Main.UCOV_LOGGER.info("WARNING: Falling back to manual detection of project source paths because no maven pom could be parsed for the passed project");
                if (codeTypes.contains(CodeType.MAIN)) {
                    Path mainPath = getPossibleMainPath(projectLocation);
                    if (mainPath != null) {
                        Main.UCOV_LOGGER.info("Detected Project MAIN Source Directory at: " + mainPath);
                        if (!paths.contains(mainPath)) {
                            paths.add(mainPath);
                        }
                    }
                }

                if (codeTypes.contains(CodeType.TEST)) {
                    Path testPath = getPossibleTestPath(projectLocation);
                    if (testPath != null) {
                        Main.UCOV_LOGGER.info("Detected Project TEST Source Directory at: " + testPath);
                        if (!paths.contains(testPath)) {
                            paths.add(testPath);
                        }
                    }
                }
            }
        }

        if (codeTypes.contains(CodeType.SAMPLE)) {
            Path samplePath = getPossibleSamplePath(projectLocation);
            if (samplePath != null) {
                Main.UCOV_LOGGER.info("Detected Project SAMPLE Source Directory at: " + samplePath);
                if (!paths.contains(samplePath)) {
                    paths.add(samplePath);
                }
            }
        }

        if (paths.isEmpty()) {
            Main.UCOV_LOGGER.info("WARNING: Adding the entire directory because no project got detected and CodeTypes = ALL!");
            if (!paths.contains(projectLocation)) {
                paths.add(projectLocation);
            }
        }

        Main.UCOV_LOGGER.info("Finished detecting source directories for project: " + projectLocation);

        return paths;
    }
}
