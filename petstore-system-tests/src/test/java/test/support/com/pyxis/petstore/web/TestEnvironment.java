package test.support.com.pyxis.petstore.web;

import test.support.com.pyxis.petstore.Properties;
import test.support.com.pyxis.petstore.db.DatabaseMigrator;
import test.support.com.pyxis.petstore.db.Spring;
import test.support.com.pyxis.petstore.web.browser.BrowserControl;
import test.support.com.pyxis.petstore.web.browser.BrowserControls;
import test.support.com.pyxis.petstore.web.browser.BrowserProperties;
import test.support.com.pyxis.petstore.web.server.ServerLifeCycle;
import test.support.com.pyxis.petstore.web.server.ServerLifeCycles;
import test.support.com.pyxis.petstore.web.server.ServerProperties;

public class TestEnvironment {

    private static final String TEST_PROPERTIES = "system/test.properties";

    private static TestEnvironment environment;

    public static TestEnvironment load() {
        if (environment == null) {
            environment = load(TEST_PROPERTIES);
        }
        return environment;
    }

    public static TestEnvironment load(String resource) {
        return new TestEnvironment(Properties.load(resource));
    }

    public final Spring spring;
    public final ServerLifeCycle serverLifeCycle;
    public final BrowserControl browserControl;
    public final Routing routes;

    public TestEnvironment(Properties properties) {
        overrideWithSystemProperties(properties);
        this.spring = loadSpringContext(properties);
        migrateDatabase(properties);
        this.serverLifeCycle = selectServer(new ServerProperties(properties));
        this.browserControl = selectBrowser(new BrowserProperties(properties));
        this.routes = new Routing(new ServerProperties(properties));
    }

    private Spring loadSpringContext(Properties properties) {
        return new Spring(properties.toJavaProperties());
    }

    private void migrateDatabase(Properties properties) {
        new DatabaseMigrator(properties).migrate(spring.getDataSource());
    }

    private void overrideWithSystemProperties(Properties properties) {
        properties.override(Properties.system());
        Properties.system().merge(properties);
    }

    private ServerLifeCycle selectServer(ServerProperties properties) {
        return new ServerLifeCycles(properties).select(properties.lifeCycle());
    }

    private BrowserControl selectBrowser(BrowserProperties properties) {
        return new BrowserControls(properties).select(properties.lifeCycle());
    }

    public <T> T getComponent(Class<T> type) {
        return spring.getBean(type);
    }
}
