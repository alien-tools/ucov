package com.github.ucov.reports.html;


import com.github.ucov.Main;
import com.github.ucov.reports.html.types.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateReport {

    private final String reportFolder;
    private final DatabaseManager database;
    private final ApiType apiType;

    private String colorTested;
    private String colorHalfTested;
    private String colorNoTested;

    public GenerateReport(String reportFolder, DatabaseManager database, ApiType apiType, Boolean colorblind) {
        this.reportFolder = reportFolder;
        this.database = database;
        this.apiType = apiType;
        this.colorChoice(colorblind);
    }

    private void colorChoice(boolean colorblind) {
        if (colorblind) {
            this.colorTested = "blue";
            this.colorHalfTested = "yellow";
        } else {
            this.colorTested = "green";
            this.colorHalfTested = "orange";
        }
        this.colorNoTested = "red";
    }

    public void generateReport() {
        String src;
        String name;
        File dossier = new File(reportFolder);
        dossier.mkdir();
        generateCss();
        generateJS();
        List<String> packages = database.getPackages();
        for (String packageName : packages) {
            dossier = new File(STR."\{reportFolder}/\{packageName}");
            dossier.mkdir();

            List<ApiElement> files = database.getFiles(packageName);
            for (ApiElement file : files) {
                src = file.filePath;
                name = file.fileName;
                generateFile(packageName, name, src);
            }
            generatePackageIndex(packageName);
        }
        generateGeneralIndex();
    }

    private void generatePackageIndex(String packageName) {

        try {
            FileWriter fw = new FileWriter(STR."\{reportFolder}/\{packageName}/index.html");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println("""
                    <!DOCTYPE html> <html> <head><link rel='stylesheet' href='../report.css' type='text/css'/><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM' crossorigin='anonymous'>
                    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js' integrity='sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz' crossorigin='anonymous'></script>
                    <script src='../report.js' async></script></head><body>""");
            pw.println("<table><thead><tr>");
            pw.println("<th class='title'>File</th>");
            pw.println("<th class='title'>Percentage Total</th>");
            pw.println("<th class='title'>Percentage Field</th>");
            pw.println("<th class='title'>Percentage Method</th>");
            pw.println("<th class='title'>Percentage Type</th>");
            pw.println("<th class='title'>Percentage Constructor</th>");
            pw.println("</tr></thead>");
            pw.println("<tbody>");

            List<CompatibilityElement> compatibilities = database.getCompatibilities(packageName);
            Map<String, Integer> percentage;
            int i = 0;
            for (CompatibilityElement compatibility : compatibilities) {
                percentage = computePercentage(compatibility);

                pw.println("<tr>");
                pw.println(STR."<td class='link'><a href=\{compatibility.fileName()}.html>\{compatibility.fileName()}</a></td>");
                pw.println(getTdStringForOneELement(i, percentage.get("Tested"), compatibility.nbFieldTested() + compatibility.nbMethodTested() + compatibility.nbTypeTested() + compatibility.nbConstructorTested(), percentage.get("HalfTested"), compatibility.nbFieldHalfTested() + compatibility.nbMethodHalfTested() + compatibility.nbTypeHalfTested() + compatibility.nbConstructorHalfTested(), percentage.get("NoTested"), compatibility.nbFieldNoTested() + compatibility.nbMethodNoTested() + compatibility.nbTypeNoTested() + compatibility.nbConstructorNoTested()));
                i++;
                pw.println(getTdStringForOneELement(i, percentage.get("Field_Tested"), compatibility.nbFieldTested(), percentage.get("Field_HalfTested"), compatibility.nbFieldHalfTested(), percentage.get("Field_NoTested"), compatibility.nbFieldNoTested()));
                i++;
                pw.println(getTdStringForOneELement(i, percentage.get("Method_Tested"), compatibility.nbMethodTested(), percentage.get("Method_HalfTested"), compatibility.nbMethodHalfTested(), percentage.get("Method_NoTested"), compatibility.nbMethodNoTested()));
                i++;
                pw.println(getTdStringForOneELement(i, percentage.get("Type_Tested"), compatibility.nbTypeTested(), percentage.get("Type_HalfTested"), compatibility.nbTypeHalfTested(), percentage.get("Type_NoTested"), compatibility.nbTypeNoTested()));
                i++;
                pw.println(getTdStringForOneELement(i, percentage.get("Constructor_Tested"), compatibility.nbConstructorTested(), percentage.get("Constructor_HalfTested"), compatibility.nbConstructorHalfTested(), percentage.get("Constructor_NoTested"), compatibility.nbConstructorNoTested()));
                pw.println("</tr>");

                i++;
            }

            pw.println("</tbody><tfoot><tr>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("</tr></tfoot>");

            pw.println("""
                    </table><script>
                        const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
                        const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))
                    </script></body></html>""");
            pw.close();
        } catch (IOException e) {
            Main.UCOV_LOGGER.info(STR."Message \{e}");
        }
    }

    private void generateGeneralIndex() {

        try {

            FileWriter fw = new FileWriter(STR."\{reportFolder}/index.html");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println("""
                    <!DOCTYPE html> <html> <head><link rel='stylesheet' href='./report.css' type='text/css'/><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM' crossorigin='anonymous'>
                            <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js' integrity='sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz' crossorigin='anonymous'></script>
                            <script src='./report.js' async></script></head><body>""");
            pw.println("<table><thead><tr>");
            pw.println("<th class='title'>File</th>");
            pw.println("<th class='title'>Percentage Total</th>");
            pw.println("<th class='title'>Percentage Field</th>");
            pw.println("<th class='title'>Percentage Method</th>");
            pw.println("<th class='title'>Percentage Type</th>");
            pw.println("<th class='title'>Percentage Constructor</th>");
            pw.println("</tr></thead>");
            pw.println("<tbody>");

            Map<String, Integer> percentage;
            List<CompatibilityElement> compatibilities = database.getPackagesCompatibilities();
            int i = 0;
            for (CompatibilityElement compatibility : compatibilities) {
                percentage = computePercentage(compatibility);

                pw.println("<tr>");
                pw.println(STR."<td class='link'><a href=./\{compatibility.packageName()}/index.html>\{compatibility.packageName()}</a></td>");
                pw.println(getTdStringForOneELement(i, percentage.get("Tested"), compatibility.nbFieldTested() + compatibility.nbMethodTested() + compatibility.nbTypeTested() + compatibility.nbConstructorTested(), percentage.get("HalfTested"), compatibility.nbFieldHalfTested() + compatibility.nbMethodHalfTested() + compatibility.nbTypeHalfTested() + compatibility.nbConstructorHalfTested(), percentage.get("NoTested"), compatibility.nbFieldNoTested() + compatibility.nbMethodNoTested() + compatibility.nbTypeNoTested() + compatibility.nbConstructorNoTested()));
                i++;
                pw.println(getTdStringForOneELement(i, percentage.get("Field_Tested"), compatibility.nbFieldTested(), percentage.get("Field_HalfTested"), compatibility.nbFieldHalfTested(), percentage.get("Field_NoTested"), compatibility.nbFieldNoTested()));
                i++;
                pw.println(getTdStringForOneELement(i, percentage.get("Method_Tested"), compatibility.nbMethodTested(), percentage.get("Method_HalfTested"), compatibility.nbMethodHalfTested(), percentage.get("Method_NoTested"), compatibility.nbMethodNoTested()));
                i++;
                pw.println(getTdStringForOneELement(i, percentage.get("Type_Tested"), compatibility.nbTypeTested(), percentage.get("Type_HalfTested"), compatibility.nbTypeHalfTested(), percentage.get("Type_NoTested"), compatibility.nbTypeNoTested()));
                i++;
                pw.println(getTdStringForOneELement(i, percentage.get("Constructor_Tested"), compatibility.nbConstructorTested(), percentage.get("Constructor_HalfTested"), compatibility.nbConstructorHalfTested(), percentage.get("Constructor_NoTested"), compatibility.nbConstructorNoTested()));
                pw.println("</tr>");

                i++;
            }

            pw.println("</tbody><tfoot><tr>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("</tr></tfoot>");

            pw.println("""
                    </table><script>
                        const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
                        const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))
                    </script></body></html>""");
            pw.close();
        } catch (IOException e) {
            Main.UCOV_LOGGER.info(STR."Message \{e}");
        }
    }

    private void generateFile(String packageName, String fileName, String fileSource) {
        try {
            List<ApiElement> elements = database.getElements(fileSource);
            FileWriter fw = new FileWriter(STR."\{reportFolder}/\{packageName}/\{fileName}.html");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println("""
                    <!DOCTYPE html> <html> <head><link rel='stylesheet' href='../report.css' type='text/css'/><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet' integrity='sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM' crossorigin='anonymous'>
                    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js' integrity='sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz' crossorigin='anonymous'></script>
                    <script src='../report.js' async></script></head><body>""");
            pw.println("<table><thead><tr>");
            pw.println("<th class='title'>Type</th>");
            pw.println("<th class='title'>Name</th>");
            pw.println("<th class='title'>Test</th>");
            pw.println("<th class='title'>Client</th>");
            pw.println("<th class='title'>Example</th>");
            pw.println("</tr></thead>");
            pw.println("<tbody>");

            Map<CompatibilityStatus, Integer> index = new HashMap<>();
            index.put(CompatibilityStatus.TESTED, 0);
            index.put(CompatibilityStatus.HALF_TESTED, 0);
            index.put(CompatibilityStatus.NO_TESTED, 0);
            for (ApiElement element : elements) {

                pw.println("<tr>");
                pw.println(STR."<td class='column'>\{element.symbolType.name()}</td>");
                pw.println(STR."<td class='column'>\{element.name}</td>");
                pw.println(getStringForOneElement(element.compatibilityTest, index.get(element.compatibilityTest), element, ClientType.TEST));
                index.put(element.compatibilityTest, index.get(element.compatibilityTest) + 1);
                pw.println(getStringForOneElement(element.compatibilityClient, index.get(element.compatibilityClient), element, ClientType.CLIENT));
                index.put(element.compatibilityClient, index.get(element.compatibilityClient) + 1);
                pw.println(getStringForOneElement(element.compatibilityExample, index.get(element.compatibilityExample), element, ClientType.EXAMPLE));
                index.put(element.compatibilityExample, index.get(element.compatibilityExample) + 1);
                pw.println("</tr>");
            }

            pw.println("</tbody><tfoot><tr>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("<td class='bottom'></td>");
            pw.println("</tr></tfoot>");

            pw.println("""
                    </table><script>
                        const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
                        const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))
                    </script></body></html>""");
            pw.close();
        } catch (IOException e) {
            Main.UCOV_LOGGER.info(STR."Message \{e}");
        }
    }

    private void generateCss() {
        try {

            FileWriter fw = new FileWriter(STR."\{reportFolder}/report.css");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println("table {\n    border-collapse : collapse; \n}");
            pw.println("td{\n    padding: 2px 5px 2px 5px; \n}");
            pw.println("td.column {\n\tborder-left: 5px solid grey;\n\tborder-right: 5px solid grey;\n\tborder-bottom: 1px solid grey;\n\ttext-align: center;\n\theight: 30px;\n\twidth: 130px; \n}");
            pw.println("td.link {\n\tborder-left: 5px solid grey;\n\tborder-right: 5px solid grey;\n\tborder-bottom: 1px solid grey;\n\theight: 30px;\n\twidth: 130px; \n}");
            pw.println("td.bottom {\n\tborder-top: 5px solid grey;\n}");
            pw.println("th.title {\n\tborder: 5px solid grey;\n\ttext-align: center;\n\theight: 30px;\n\twidth: 130px; \n}");
            pw.println("button.bouton {\n    background: transparent;\n    border: transparent;\n   white-space: normal;\n}");

            pw.close();
        } catch (IOException e) {
            Main.UCOV_LOGGER.info(STR."Message \{e}");
        }
    }

    private void generateJS() {
        try {

            FileWriter fw = new FileWriter(STR."\{reportFolder}/report.js");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println("var width;\nvar height;");
            pw.println();
            pw.println(STR."var i = 0;\nvar cTested = document.getElementById('TestedCanvas_' + i);\nvar ctxTested;\nwhile(cTested != null){\n\twidth = cTested.width;\n\theight = cTested.height;\n\tctxTested = cTested.getContext('2d');\n\tctxTested.fillStyle = '\{this.colorTested}';\n\tctxTested.fillRect(0,0,width,height);\n\ti++;\n\tcTested = document.getElementById('TestedCanvas_' + i);\n}\n");
            pw.println();
            pw.println(STR."i = 0;\nvar cHalfTested = document.getElementById('HalfTestedCanvas_' + i);\nvar ctxHalfTested;\nwhile(cHalfTested != null){\n\twidth = cHalfTested.width;\n\theight = cHalfTested.height;\n\tctxHalfTested = cHalfTested.getContext('2d');\n\tctxHalfTested.fillStyle = '\{this.colorHalfTested}';\n\tctxHalfTested.fillRect(0,0,width,height);\n\ti++;\n\tcHalfTested = document.getElementById('HalfTestedCanvas_' + i);\n}\n");
            pw.println();
            pw.println(STR."i = 0;\nvar cNoTested = document.getElementById('NoTestedCanvas_' + i);\nvar ctxNoTested;\nwhile(cNoTested != null){\n\twidth = cNoTested.width;\n\theight = cNoTested.height;\n\tctxNoTested = cNoTested.getContext('2d');\n\tctxNoTested.fillStyle = '\{this.colorNoTested}';\n\tctxNoTested.fillRect(0,0,width,height);\n\ti++;\n\tcNoTested = document.getElementById('NoTestedCanvas_' + i);\n}\n");

            pw.println();
            pw.println(STR."i = 0;\nvar cTested = document.getElementById('TestedCanvasCircle_' + i);\nvar ctxTested;\nwhile(cTested != null){\n\tctxTested = cTested.getContext('2d');\n\tctxTested.fillStyle = '\{this.colorTested}';\n\tctxTested.beginPath(); \n\tctxTested.ellipse(10, 10, 9, 9, 0, 0, 2 * Math.PI); \n\tctxTested.fill();\n\tctxTested.stroke();\n\ti++;\n\tcTested = document.getElementById('TestedCanvasCircle_' + i);\n}\n");
            pw.println();
            pw.println(STR."i = 0;\nvar cHalfTested = document.getElementById('HalfTestedCanvasCircle_' + i);\nvar ctxHalfTested;\nwhile(cHalfTested != null){\n\tctxHalfTested = cHalfTested.getContext('2d');\n\tctxHalfTested.fillStyle = '\{this.colorHalfTested}'; \n\tctxHalfTested.beginPath(); \n\tctxHalfTested.ellipse(10, 10, 9, 9, 0, 0, 2 * Math.PI); \n\tctxHalfTested.fill();\n\tctxHalfTested.stroke(); \n\ti++;\n\tcHalfTested = document.getElementById('HalfTestedCanvasCircle_' + i);\n}\n");
            pw.println();
            pw.println(STR."i = 0;\nvar cNoTested = document.getElementById('NoTestedCanvasCircle_' + i);\nvar ctxNoTested;\nwhile(cNoTested != null){\n\tctxNoTested = cNoTested.getContext('2d');\n\tctxNoTested.fillStyle = '\{this.colorNoTested}'; \n\tctxNoTested.beginPath(); \n\tctxNoTested.ellipse(10, 10, 9, 9, 0, 0, 2 * Math.PI); \n\tctxNoTested.fill();\n\tctxNoTested.stroke(); \n\ti++;\n\tcNoTested = document.getElementById('NoTestedCanvasCircle_' + i);\n}\n");

            pw.close();
        } catch (IOException e) {
            Main.UCOV_LOGGER.info(STR."Message \{e}");
        }
    }

    private Map<String, Integer> computePercentage(CompatibilityElement compatibility) {
        Map<String, Integer> percentage = new HashMap<>();
        int totalElement = compatibility.nbFieldTotal() + compatibility.nbMethodTotal() + compatibility.nbTypeTotal() + compatibility.nbConstructorTotal();
        if (totalElement != 0) {
            percentage.put("Tested", 100 * (compatibility.nbFieldTested() + compatibility.nbMethodTested() + compatibility.nbTypeTested() + compatibility.nbConstructorTested()) / totalElement);
            percentage.put("HalfTested", 100 * (compatibility.nbFieldHalfTested() + compatibility.nbMethodHalfTested() + compatibility.nbTypeHalfTested() + compatibility.nbConstructorHalfTested()) / totalElement);
            percentage.put("NoTested", 100 * (compatibility.nbFieldNoTested() + compatibility.nbMethodNoTested() + compatibility.nbTypeNoTested() + compatibility.nbConstructorNoTested()) / totalElement);
        } else {
            percentage.put("Tested", 0);
            percentage.put("HalfTested", 0);
            percentage.put("NoTested", 0);
        }

        if (compatibility.nbFieldTotal() != 0) {
            percentage.put("Field_Tested", 100 * compatibility.nbFieldTested() / compatibility.nbFieldTotal());
            percentage.put("Field_HalfTested", 100 * compatibility.nbFieldHalfTested() / compatibility.nbFieldTotal());
            percentage.put("Field_NoTested", 100 * compatibility.nbFieldNoTested() / compatibility.nbFieldTotal());
        } else {
            percentage.put("Field_Tested", 0);
            percentage.put("Field_HalfTested", 0);
            percentage.put("Field_NoTested", 0);
        }

        if (compatibility.nbMethodTotal() != 0) {
            percentage.put("Method_Tested", 100 * compatibility.nbMethodTested() / compatibility.nbMethodTotal());
            percentage.put("Method_HalfTested", 100 * compatibility.nbMethodHalfTested() / compatibility.nbMethodTotal());
            percentage.put("Method_NoTested", 100 * compatibility.nbMethodNoTested() / compatibility.nbMethodTotal());
        } else {
            percentage.put("Method_Tested", 0);
            percentage.put("Method_HalfTested", 0);
            percentage.put("Method_NoTested", 0);
        }

        if (compatibility.nbTypeTotal() != 0) {
            percentage.put("Type_Tested", 100 * compatibility.nbTypeTested() / compatibility.nbTypeTotal());
            percentage.put("Type_HalfTested", 100 * compatibility.nbTypeHalfTested() / compatibility.nbTypeTotal());
            percentage.put("Type_NoTested", 100 * compatibility.nbTypeNoTested() / compatibility.nbTypeTotal());
        } else {
            percentage.put("Type_Tested", 0);
            percentage.put("Type_HalfTested", 0);
            percentage.put("Type_NoTested", 0);
        }

        if (compatibility.nbConstructorTotal() != 0) {
            percentage.put("Constructor_Tested", 100 * compatibility.nbConstructorTested() / compatibility.nbConstructorTotal());
            percentage.put("Constructor_HalfTested", 100 * compatibility.nbConstructorHalfTested() / compatibility.nbConstructorTotal());
            percentage.put("Constructor_NoTested", 100 * compatibility.nbConstructorNoTested() / compatibility.nbConstructorTotal());
        } else {
            percentage.put("Constructor_Tested", 0);
            percentage.put("Constructor_HalfTested", 0);
            percentage.put("Constructor_NoTested", 0);
        }

        return percentage;
    }


    private String getTdStringForOneELement(int i, int percentageTested, int nbTested, int percentageHalfTested, int nbHalfTested, int percentageNoTested, int nbNoTested) {
        int height = 6;
        return STR."<td class='column'><button type='button' class='bouton' data-bs-toggle='tooltip' data-bs-html='true' data-bs-title='<div>Tested : \{percentageTested}% | \{nbTested}</div><div>Half Tested : \{percentageHalfTested}% | \{nbHalfTested}</div><div>No Tested : \{percentageNoTested}% | \{nbNoTested}</div>'><canvas id = 'TestedCanvas_\{i}' width=\{percentageTested} height=\{height}></canvas><canvas id = 'HalfTestedCanvas_\{i}' width=\{percentageHalfTested} height=\{height}></canvas><canvas id = 'NoTestedCanvas_\{i}' width=\{percentageNoTested} height=\{height}></canvas></button></td>";
    }

    private String getStringForOneElement(CompatibilityStatus compatibilityStatus, int i, ApiElement element, ClientType clientType) {
        int height = 20;
        int length = 20;
        String result = STR."<td class='column'><button type='button' class='bouton' data-bs-toggle='tooltip' data-bs-html='true' data-bs-title='\{getTooltip(element, clientType)}'>";
        switch (compatibilityStatus) {
            case TESTED ->
                    result += STR."<canvas id = 'TestedCanvasCircle_\{i}' width=\{length} height=\{height}></canvas>";
            case HALF_TESTED ->
                    result += STR."<canvas id = 'HalfTestedCanvasCircle_\{i}' width=\{length} height=\{height}></canvas>";
            case NO_TESTED ->
                    result += STR."<canvas id = 'NoTestedCanvasCircle_\{i}' width=\{length} height=\{height}></canvas>";
        }
        result += "</button></td>";

        return result;
    }

    private String getTooltip(ApiElement element, ClientType clientType) {
        String result = "";
        if (element.symbolType == SymbolType.FIELD) {
            result += STR."<div> Field Read : \{database.countNbUsage(element.elementId, UsageType.FIELD_READ.name(), apiType, clientType)}</div>";
            if (!(element.modifier == Modifier.FINAL)) {
                result += STR."<div> Field Write : \{database.countNbUsage(element.elementId, UsageType.FIELD_WRITE.name(), apiType, clientType)}</div>";
            }
        } else if (element.symbolType == SymbolType.CLASS) {
            if (!(element.modifier == Modifier.FINAL)) {
                result += STR."<div> Type Extend : \{database.countNbUsage(element.elementId, UsageType.TYPE_EXTEND.name(), apiType, clientType)}</div>";
            }
            result += STR."<div> Type Reference : \{database.countNbUsage(element.elementId, UsageType.TYPE_REFERENCE.name(), apiType, clientType)}</div>";
        } else if (element.symbolType == SymbolType.INTERFACE) {
            result += STR."<div> Type Implement : \{database.countNbUsage(element.elementId, UsageType.TYPE_IMPLEMENT.name(), apiType, clientType)}</div>";
            result += STR."<div> Type Reference : \{database.countNbUsage(element.elementId, UsageType.TYPE_REFERENCE.name(), apiType, clientType)}</div>";
        } else if (element.symbolType == SymbolType.ENUM) {
            result += STR."<div> Type Reference : \{database.countNbUsage(element.elementId, UsageType.TYPE_REFERENCE.name(), apiType, clientType)}</div>";
        } else if (element.symbolType == SymbolType.METHOD) {
            if (!(element.modifier == Modifier.FINAL)) {
                result += STR."<div> Method Override : \{database.countNbUsage(element.elementId, UsageType.METHOD_OVERRIDE.name(), apiType, clientType)}</div>";
            }
            if (!(element.modifier == Modifier.ABSTRACT)) {
                result += STR."<div> Method Call : \{database.countNbUsage(element.elementId, UsageType.METHOD_CALL.name(), apiType, clientType)}</div>";
            }
        } else {
            result += STR."<div> Constructor Call : \{database.countNbUsage(element.elementId, UsageType.CONSTRUCTOR_CALL.name(), apiType, clientType)}</div>";
        }
        return result;
    }
}
