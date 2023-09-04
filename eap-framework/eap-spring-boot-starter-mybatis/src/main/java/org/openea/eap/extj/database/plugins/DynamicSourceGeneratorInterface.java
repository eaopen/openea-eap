package org.openea.eap.extj.database.plugins;

import org.openea.eap.extj.database.util.DataSourceUtil;

public interface DynamicSourceGeneratorInterface {
    DataSourceUtil getDataSource();

    default boolean cachedConnection() {
        return true;
    }
}