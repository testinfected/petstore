package test.support.com.pyxis.petstore.web.server;

import org.testinfected.hamcrest.ExceptionImposter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import static java.lang.Integer.parseInt;

public class ServerProperties {

    public static final String LIFECYCLE = "server.lifecycle";
    public static final String SCHEME = "server.scheme";
    public static final String HOST = "server.host";
    public static final String PORT = "server.port";
    public static final String CONTEXT_PATH = "server.context.path";
    public static final String WEBAPP_PATH = "server.webapp.path";

    private final Properties properties;

    public ServerProperties(Properties properties) {
        this.properties = properties;
    }

    public String scheme() {
        return properties.getProperty(SCHEME);
    }

    public String host() {
        return properties.getProperty(HOST);
    }

    public int port() {
        return parseInt(properties.getProperty(PORT));
    }

    public String contextPath() {
        return properties.getProperty(CONTEXT_PATH);
    }

    public String webAppPath() {
        return properties.getProperty(WEBAPP_PATH);
    }

    public String lifeCycle() {
        return properties.getProperty(LIFECYCLE);
    }

    public URL urlFor(String path) {
        try {
            return new URL(String.format("%s://%s:%s%s%s", scheme(), host(), port(), contextPath(), path));
        } catch (MalformedURLException e) {
            throw ExceptionImposter.imposterize(e);
        }
    }
}
