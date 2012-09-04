package test.support.com.pyxis.petstore.web;

import com.gargoylesoftware.htmlunit.WebClient;
import com.objogate.wl.UnsynchronizedProber;
import com.objogate.wl.web.AsyncWebDriver;
import org.hibernate.SessionFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import test.support.com.pyxis.petstore.PropertyFile;
import test.support.com.pyxis.petstore.db.Database;
import test.support.com.pyxis.petstore.db.Spring;
import test.support.com.pyxis.petstore.web.browser.BrowserControl;
import test.support.com.pyxis.petstore.web.browser.LastingBrowser;
import test.support.com.pyxis.petstore.web.browser.PassingBrowser;
import test.support.com.pyxis.petstore.web.browser.RemoteBrowser;
import test.support.com.pyxis.petstore.web.server.ExternalServer;
import test.support.com.pyxis.petstore.web.server.LastingServer;
import test.support.com.pyxis.petstore.web.server.ServerLifeCycle;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.Integer.parseInt;

public class TestEnvironment {

    public static final String SERVER_LIFECYCLE = "server.lifecycle";
    public static final String WEBAPP_PATH = "webapp.path";
    public static final String CONTEXT_PATH = "context.path";

    public static final String SERVER_HOST = "server.host";
    public static final String SERVER_PORT = "server.port";

    public static final String APPLICATION_HOST = "application.host";
    public static final String APPLICATION_PORT = "application.port";

    public static final String BROWSER_LIFECYCLE = "browser.lifecycle";
    public static final String BROWSER_REMOTE_URL = "browser.remote.url";
    public static final String BROWSER_REMOTE_CAPABILITY = "browser.remote.capability.";

    public static final String EXTERNAL = "external";
    public static final String LASTING = "lasting";
    public static final String PASSING = "passing";
    public static final String REMOTE = "remote";

    public static final int DEFAULT_TIMEOUT = 5000;

    private static final String TEST_PROPERTIES = "system/test.properties";
    private static TestEnvironment environment;

    public static TestEnvironment load() {
        if (environment == null) {
            environment = load(TEST_PROPERTIES);
        }
        return environment;
    }

    public static TestEnvironment load(String resource) {
        return new TestEnvironment(PropertyFile.load(resource));
    }

    private final Properties props;
    private final Routing applicationRoutes;
    private final Routing adminRoutes;
    private final Spring spring;
    private final ServerLifeCycle serverLifeCycle;
    private final BrowserControl browserControl;

    public TestEnvironment(Properties properties) {
        this.props = configure(properties);
        this.spring = loadSpringContext(properties);
        this.serverLifeCycle = selectServer();
        this.browserControl = selectBrowser();
        this.applicationRoutes = new Routing(applicationUrl());
        this.adminRoutes = new Routing(adminUrl());
    }

    private Properties configure(Properties settings) {
        Properties actual = new Properties();
        actual.putAll(settings);
        actual.putAll(System.getProperties());
        System.getProperties().putAll(actual);
        return actual;
    }

    private Spring loadSpringContext(Properties properties) {
        return new Spring(properties);
    }

    private ServerLifeCycle selectServer() {
        final Map<String, ServerLifeCycle> available = new HashMap<String, ServerLifeCycle>();
        available.put(EXTERNAL, new ExternalServer());
        available.put(LASTING, new LastingServer(asString(SERVER_HOST), asInt(SERVER_PORT), asString(CONTEXT_PATH), asString(WEBAPP_PATH)));
        return available.get(asString(SERVER_LIFECYCLE));
    }

    private BrowserControl selectBrowser() {
        final Map<String, BrowserControl> available = new HashMap<String, BrowserControl>();
        available.put(PASSING, new PassingBrowser());
        available.put(LASTING, new LastingBrowser());
        available.put(REMOTE, new RemoteBrowser(asURL(BROWSER_REMOTE_URL), browserCapabilities()));
        return available.get(asString(BROWSER_LIFECYCLE));
    }

    public WebClient makeWebClient() {
        WebClient webClient = new WebClient();
        webClient.setTimeout(DEFAULT_TIMEOUT);
        return webClient;
    }

    public void startServer() {
        serverLifeCycle.start();
    }

    public void stopServer() {
        serverLifeCycle.stop();
    }

    public AsyncWebDriver launchBrowser() throws Exception {
        AsyncWebDriver browser = new AsyncWebDriver(new UnsynchronizedProber(), browserControl.launch());
        browser.navigate().to(applicationRoutes.toHome());
        return browser;
    }

    public Routing adminRoutes() {
        return adminRoutes;
    }

    public void wipe() {
        Database database = Database.connect(spring.getBean(SessionFactory.class));
        database.clean();
        database.close();
    }

    private String applicationUrl() {
        return String.format("http://%s:%s%s", asString(APPLICATION_HOST), asString(APPLICATION_PORT), asString(CONTEXT_PATH));
    }

    private String adminUrl() {
        return String.format("http://%s:%s%s", asString(SERVER_HOST), asString(SERVER_PORT), asString(CONTEXT_PATH));
    }

    public Capabilities browserCapabilities() {
        Map<String, String> capabilities = new HashMap<String, String>();
        for (String property : props.stringPropertyNames()) {
            if (isCapability(property)) {
                capabilities.put(capabilityName(property), asString(property));
            }
        }
        return new DesiredCapabilities(capabilities);
    }

    private String capabilityName(String property) {
        return property.substring(BROWSER_REMOTE_CAPABILITY.length());
    }

    private boolean isCapability(String property) {
        return property.startsWith(BROWSER_REMOTE_CAPABILITY);
    }

    private String asString(final String key) {
        return props.getProperty(key);
    }

    private int asInt(String key) {
        return parseInt(asString(key));
    }

    private URL asURL(final String key) {
        String url = asString(key);
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(key + " is not a valid url: " + url, e);
        }
    }
}
