package com.github.ucov.reports.html;

import com.github.maracas.roseau.api.model.API;
import com.github.ucov.models.Usage;

import java.util.Collection;

public class DatabaseManagerFactory {
    public static DatabaseManager createDatabaseManager(API mainProjectApiModel, Collection<Usage> usageModels) {
        DatabaseManager databaseManager = new DatabaseManager();

        /*for (var type : )

        databaseManager.addAPIElement();
        databaseManager.addClient();
        databaseManager.addCompatibility();
        databaseManager.addStudiedClient();
        databaseManager.addUsage();*/

        return databaseManager;
    }
}
