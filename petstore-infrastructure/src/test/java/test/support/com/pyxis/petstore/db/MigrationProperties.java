package test.support.com.pyxis.petstore.db;

import java.util.Properties;

public class MigrationProperties {

    public static final String MIGRATIONS_PATH = "migrations.path";

    private final Properties properties;

    public MigrationProperties(Properties properties) {
        this.properties = properties;
    }

    public String migrationsPath() {
        return properties.getProperty(MIGRATIONS_PATH);
    }
}
