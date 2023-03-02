package com.github.ucov.reports.html;

import com.github.ucov.reports.html.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseManager {
    private final List<API> apiModel = new ArrayList<>();
    private final List<Usage2> usageModel = new ArrayList<>();
    private final List<Compatibility> compatibility = new ArrayList<>();
    private final List<Client> client = new ArrayList<>();
    private final List<StudiedClient> studiedClient = new ArrayList<>();

    public void addAPIElement(String packageName, String className, String name, String sourcePath, String fileName, int beginLine, int endLine, int beginColumn, int endColumn, String symbol_type, String visibility, String modifier, String compatibilityTest, String compatibilityClient, String compatibilityExample) {
        apiModel.add(new API(apiModel.size(), packageName, className, name, sourcePath, fileName, beginLine, endLine, beginColumn, endColumn, symbol_type, visibility, modifier, compatibilityTest, compatibilityClient, compatibilityExample));
    }

    public void addUsage(int idElement, int idClient, int beginLine, int endLine, int beginColumn, int endColumn, String usageType, String role, String context) {
        usageModel.add(new Usage2(usageModel.size(), idElement, idClient, beginLine, endLine, beginColumn, endColumn, usageType, role, context));
    }

    public void addCompatibility(String packageName, String fileName, int nbFieldTested, int nbFieldHalfTested, int nbFieldNoTested, int nbFieldTotal, int nbMethodTested, int nbMethodHalfTested, int nbMethodNoTested, int nbMethodTotal, int nbTypeTested, int nbTypeHalfTested, int nbTypeNoTested, int nbTypeTotal, int nbConstructorTested, int nbConstructorHalfTested, int nbConstructorNoTested, int nbConstructorTotal) {
        compatibility.add(new Compatibility(compatibility.size(), packageName, fileName, nbFieldTested, nbFieldHalfTested, nbFieldNoTested, nbFieldTotal, nbMethodTested, nbMethodHalfTested, nbMethodNoTested, nbMethodTotal, nbTypeTested, nbTypeHalfTested, nbTypeNoTested, nbTypeTotal, nbConstructorTested, nbConstructorHalfTested, nbConstructorNoTested, nbConstructorTotal));
    }

    public void addClient(String clientType, String clientName, String clientPackage, String clientClass, String clientFunction, String fileName, int beginLine, int endLine, int beginColumn, int endColumn) {
        client.add(new Client(client.size(), clientType, clientName, clientPackage, clientClass, clientFunction, fileName, beginLine, endLine, beginColumn, endColumn));
    }

    public void addStudiedClient(String studiedClientType, String studiedClientName, int nbElement) {
        studiedClient.add(new StudiedClient(studiedClient.size(), studiedClientType, studiedClientName, nbElement));
    }

    public int getElementId(String packageName, String className, String name, int line) {
        for (API api : apiModel) {
            if (api.package_name().equals(packageName) && api.class_name().equals(className) && api.name().equals(name) && api.begin_line() == line) {
                return api.element_id();
            }
        }

        return -1;
    }

    public List<ApiElement> getFiles(String packageName) {
        List<ApiElement> listApiElement = new ArrayList<>();

        for (API api : apiModel) {
            if (api.package_name().equals(packageName)) {
                if (listApiElement.stream().noneMatch(t -> t.packageName.equals(packageName) && t.filePath.equals(api.file_path()))) {
                    listApiElement.add(new ApiElement(api.element_id(),
                            api.package_name(),
                            api.class_name(),
                            api.name(),
                            api.file_path(),
                            api.file_name(),
                            api.begin_line(),
                            api.end_line(),
                            api.begin_column(),
                            api.end_column(),
                            SymbolType.valueOf(api.symbol_type()),
                            Visibility.valueOf(api.visibility()),
                            Modifier.valueOf(api.modifier()),
                            CompatibilityStatus.valueOf(api.compatibilityTest()),
                            CompatibilityStatus.valueOf(api.compatibilityClient()),
                            CompatibilityStatus.valueOf(api.compatibilityExample())));
                }
            }
        }

        return listApiElement;
    }

    public List<String> getPackages() {
        List<String> listPackage = new ArrayList<>();

        for (API api : apiModel) {
            if (listPackage.stream().noneMatch(t -> t.equals(api.package_name()))) {
                listPackage.add(api.package_name());
            }
        }

        return listPackage;
    }

    public List<ApiElement> getElements(String sourcePath) {

        List<ApiElement> listApiElement = new ArrayList<>();
        for (API api : apiModel) {
            if (api.file_path().equals(sourcePath)) {
                listApiElement.add(new ApiElement(api.element_id(),
                        api.package_name(),
                        api.class_name(),
                        api.name(),
                        api.file_path(),
                        api.file_name(),
                        api.begin_line(),
                        api.end_line(),
                        api.begin_column(),
                        api.end_column(),
                        SymbolType.valueOf(api.symbol_type()),
                        Visibility.valueOf(api.visibility()),
                        Modifier.valueOf(api.modifier()),
                        CompatibilityStatus.valueOf(api.compatibilityTest()),
                        CompatibilityStatus.valueOf(api.compatibilityClient()),
                        CompatibilityStatus.valueOf(api.compatibilityExample())));
            }
        }

        return listApiElement;
    }

    public List<CompatibilityElement> getCompatibilities(String packageName) {
        List<CompatibilityElement> listCompatibilityElement = new ArrayList<>();

        for (Compatibility compat : compatibility) {
            if (compat.package_name().equals(packageName)) {
                listCompatibilityElement.add(new CompatibilityElement(compat.compatibility_id(),
                        compat.package_name(),
                        compat.file_name(),
                        compat.nb_field_tested(),
                        compat.nb_field_halfTested(),
                        compat.nb_field_noTested(),
                        compat.nb_field_total(),
                        compat.nb_method_tested(),
                        compat.nb_method_halfTested(),
                        compat.nb_method_noTested(),
                        compat.nb_method_total(),
                        compat.nb_type_tested(),
                        compat.nb_type_halfTested(),
                        compat.nb_type_noTested(),
                        compat.nb_type_total(),
                        compat.nb_constructor_tested(),
                        compat.nb_constructor_halfTested(),
                        compat.nb_constructor_noTested(),
                        compat.nb_constructor_total()));
            }
        }

        return listCompatibilityElement;
    }

    public List<CompatibilityElement> getPackagesCompatibilities() {
        List<CompatibilityElement> listCompatibilityElement = new ArrayList<>();

        for (List<Compatibility> compats : compatibility.stream().collect(Collectors.groupingBy(Compatibility::package_name)).values()) {
            int nb_field_tested = 0;
            int nb_field_halfTested = 0;
            int nb_field_noTested = 0;
            int nb_field_total = 0;
            int nb_method_tested = 0;
            int nb_method_halfTested = 0;
            int nb_method_noTested = 0;
            int nb_method_total = 0;
            int nb_type_tested = 0;
            int nb_type_halfTested = 0;
            int nb_type_noTested = 0;
            int nb_type_total = 0;
            int nb_constructor_tested = 0;
            int nb_constructor_halfTested = 0;
            int nb_constructor_noTested = 0;
            int nb_constructor_total = 0;

            int compatibility_id = -1;
            String package_name = "";
            String file_name = "";

            for (Compatibility compat : compats) {
                compatibility_id = compat.compatibility_id();
                package_name = compat.package_name();
                file_name = compat.package_name();

                nb_field_tested += compat.nb_field_tested();
                nb_field_halfTested += compat.nb_field_halfTested();
                nb_field_noTested += compat.nb_field_noTested();
                nb_field_total += compat.nb_field_total();
                nb_method_tested += compat.nb_method_tested();
                nb_method_halfTested += compat.nb_method_halfTested();
                nb_method_noTested += compat.nb_method_noTested();
                nb_method_total += compat.nb_method_total();
                nb_type_tested += compat.nb_type_tested();
                nb_type_halfTested += compat.nb_type_halfTested();
                nb_type_noTested += compat.nb_type_noTested();
                nb_type_total += compat.nb_type_total();
                nb_constructor_tested += compat.nb_constructor_tested();
                nb_constructor_halfTested += compat.nb_constructor_halfTested();
                nb_constructor_noTested += compat.nb_constructor_noTested();
                nb_constructor_total += compat.nb_constructor_total();
            }

            listCompatibilityElement.add(new CompatibilityElement(compatibility_id,
                    package_name,
                    file_name,
                    nb_field_tested,
                    nb_field_halfTested,
                    nb_field_noTested,
                    nb_field_total,
                    nb_method_tested,
                    nb_method_halfTested,
                    nb_method_noTested,
                    nb_method_total,
                    nb_type_tested,
                    nb_type_halfTested,
                    nb_type_noTested,
                    nb_type_total,
                    nb_constructor_tested,
                    nb_constructor_halfTested,
                    nb_constructor_noTested,
                    nb_constructor_total));
        }

        return listCompatibilityElement;
    }

    public boolean checkUsage(int elementId, String usageType, ApiType apiType, ClientType clientType) {
        for (Usage2 usage : usageModel) {
            if (usage.usage_type().equals(usageType) && usage.element_id() == elementId) {
                if (apiType == ApiType.TEST && usage.context().equals("CLASS") ||
                        apiType == ApiType.CLIENT && usage.context().equals("EXTERNAL") ||
                        apiType == ApiType.SOURCE_CODE) {
                    for (Client cli : client) {
                        if (cli.client_id() == usage.client_id()) {
                            if (cli.client_type().equals(clientType.name())) {
                                return true;
                            }
                            break;
                        }
                    }
                }
            }
        }

        return false;
    }

    public int countNbUsage(int elementId, String usageType, ApiType apiType, ClientType clientType) {
        int nbUse = 0;

        for (Usage2 usage : usageModel) {
            if (usage.usage_type().equals(usageType) && usage.element_id() == elementId) {
                if (apiType == ApiType.TEST && usage.context().equals("CLASS") ||
                        apiType == ApiType.CLIENT && usage.context().equals("EXTERNAL") ||
                        apiType == ApiType.SOURCE_CODE) {
                    for (Client cli : client) {
                        if (cli.client_id() == usage.client_id()) {
                            if (cli.client_type().equals(clientType.name())) {
                                nbUse++;
                            }
                            break;
                        }
                    }
                }
            }
        }

        return nbUse;
    }

    public void updateElementCompatibility(ApiElement element) {
        API api = null;

        for (API _api : apiModel) {
            if (_api.element_id() == element.elementId) {
                api = _api;
                break;
            }
        }

        if (api != null) {
            apiModel.remove(element.elementId);
            apiModel.add(new API(
                    api.element_id(),
                    api.package_name(),
                    api.class_name(),
                    api.name(),
                    api.file_path(),
                    api.file_name(),
                    api.begin_line(),
                    api.end_line(),
                    api.begin_column(),
                    api.end_column(),
                    api.symbol_type(),
                    api.visibility(),
                    api.modifier(),
                    element.compatibilityTest.name(),
                    element.compatibilityClient.name(),
                    element.compatibilityExample.name()));
        }
    }

    public int getClientId(String clientType, String clientName, String clientPackage, String clientClass, int beginLine, int endLine, int beginColumn, int endColumn, String sourceName, String clientFunction) {
        int idClient = -1;

        for (Client cli : client) {
            if (cli.client_name().equals(clientName) &&
                    cli.client_package().equals(clientPackage) &&
                    cli.client_class().equals(clientClass) &&
                    cli.client_function().equals(clientFunction) &&
                    cli.begin_line() == beginLine) {
                idClient = cli.client_id();
                break;
            }
        }

        if (idClient == -1) {
            addClient(clientType, clientName, clientPackage, clientClass, clientFunction, sourceName, beginLine, endLine, beginColumn, endColumn);

            for (Client cli : client) {
                if (cli.client_name().equals(clientName) &&
                        cli.client_package().equals(clientPackage) &&
                        cli.client_class().equals(clientClass) &&
                        cli.client_function().equals(clientFunction) &&
                        cli.begin_line() == beginLine) {
                    idClient = cli.client_id();
                    break;
                }
            }
        }

        return idClient;
    }
}
