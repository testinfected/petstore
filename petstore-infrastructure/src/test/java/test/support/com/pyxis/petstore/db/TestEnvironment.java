package test.support.com.pyxis.petstore.db;

import test.support.com.pyxis.petstore.PropertyFile;

import java.util.Properties;

public class TestEnvironment {

    private static final String INTEGRATION_TEST_PROPERTIES = "integration/test.properties";
    private static TestEnvironment environment;

    private Spring spring;

    public static TestEnvironment load() {
        if (environment == null) {
            environment = load(INTEGRATION_TEST_PROPERTIES);
        }
        return environment;
    }

    public static TestEnvironment load(final String name) {
        return new TestEnvironment(PropertyFile.load(name));
    }

    public TestEnvironment(Properties properties) {
        loadSpringContext(properties);
        migrateDatabase(properties);
    }

    private void loadSpringContext(Properties properties) {
        this.spring = new Spring(properties);
    }

    private void migrateDatabase(Properties properties) {
        new DatabaseMigrator(properties).migrate(spring.getDataSource());
    }

    public <T> T get(Class<T> type) {
        return spring.getBean(type);
    }
}
