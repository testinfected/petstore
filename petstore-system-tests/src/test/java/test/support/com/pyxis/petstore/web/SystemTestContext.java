package test.support.com.pyxis.petstore.web;

import org.openqa.selenium.WebDriver;
import test.support.com.pyxis.petstore.Properties;
import test.support.com.pyxis.petstore.builders.Builder;
import test.support.com.pyxis.petstore.db.Database;
import test.support.com.pyxis.petstore.db.DatabaseCleaner;
import test.support.com.pyxis.petstore.db.DatabaseMigrator;
import test.support.com.pyxis.petstore.db.PersistenceContext;
import test.support.com.pyxis.petstore.web.browser.BrowserControl;
import test.support.com.pyxis.petstore.web.browser.BrowserControls;
import test.support.com.pyxis.petstore.web.browser.BrowserProperties;
import test.support.com.pyxis.petstore.web.server.ServerLifeCycle;
import test.support.com.pyxis.petstore.web.server.ServerLifeCycles;
import test.support.com.pyxis.petstore.web.server.ServerProperties;

public final class SystemTestContext {

    private static final String SYSTEM_TEST_PROPERTIES = "system/test.properties";

    private static SystemTestContext context;

    private PersistenceContext spring;
    private ServerLifeCycle serverLifeCycle;
    private BrowserControl browserControl;
    private Routing routing;

    public static SystemTestContext systemTesting() {
        if (context == null) {
            context = new SystemTestContext(Properties.load(SYSTEM_TEST_PROPERTIES));
        }
        return context;
    }

    public SystemTestContext(Properties properties) {
        systemPropertiesOverride(properties);
        loadSpringContext(properties);
        migrateDatabase(properties);
        selectServer(new ServerProperties(properties));
        selectBrowser(new BrowserProperties(properties));
        createRoutes(new ServerProperties(properties));
    }

    private void systemPropertiesOverride(Properties properties) {
        properties.override(Properties.system());
        Properties.system().merge(properties);
    }

    private void createRoutes(ServerProperties properties) {
        this.routing = new Routing(properties);
    }

    private void loadSpringContext(Properties properties) {
        this.spring = new PersistenceContext(properties.toJavaProperties());
    }

    private void migrateDatabase(Properties properties) {
        new DatabaseMigrator(properties).migrate(spring.getDataSource());
    }

    private void selectServer(ServerProperties properties) {
        serverLifeCycle = new ServerLifeCycles(properties).select(properties.lifeCycle());
    }

    private void selectBrowser(BrowserProperties properties) {
        browserControl = new BrowserControls(properties).select(properties.lifeCycle());
    }

    public ApplicationDriver startApplication() {
        startServer();
        return launchApplication();
    }

    public void stopApplication(ApplicationDriver application) {
        application.close();
        stopServer();
        cleanUp();
    }

    public void given(Builder<?>... builders) {
        for (final Builder<?> builder : builders) {
            given(builder.build());
        }
    }

    public void given(Object... fixtures) {
        Database database = new Database(spring.openSession());
        database.persist(fixtures);
        database.close();
    }

    private ApplicationDriver launchApplication() {
        ApplicationDriver application = new ApplicationDriver(launchBrowser());
        application.open(routing);
        return application;
    }

    private void startServer() {
        serverLifeCycle.start();
    }

    private void stopServer() {
        serverLifeCycle.stop();
    }

    private WebDriver launchBrowser() {
        return browserControl.launch();
    }

    private void cleanUp() {
        Database database = new Database(spring.openSession());
        new DatabaseCleaner(database).clean();
        database.close();
    }
}
