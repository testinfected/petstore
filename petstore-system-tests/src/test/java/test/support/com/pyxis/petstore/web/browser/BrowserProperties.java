package test.support.com.pyxis.petstore.web.browser;

import org.testinfected.hamcrest.ExceptionImposter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class BrowserProperties {
    
    public static final String LIFECYCLE = "browser.lifecycle";
    public static final String REMOTE_URL = "browser.remote.url";
    public static final String CAPABILITY = "browser.remote.capability.";

    private final Properties properties;

    public BrowserProperties(Properties properties) {
        this.properties = properties;
    }

    public String lifeCycle() {
        return valueOf(LIFECYCLE);
    }

    public URL remoteUrl() {
        try {
            return new URL(valueOf(REMOTE_URL));
        } catch (MalformedURLException e) {
            throw ExceptionImposter.imposterize(e);
        }
    }

    public Map<String, String> capabilities() {
        Map<String, String> capabilities = new HashMap<String, String>();
        for (String property : properties.stringPropertyNames()) {
            if (isCapability(property)) {
                capabilities.put(capabilityName(property), valueOf(property));
            }
        }
        return capabilities;
    }

    private String valueOf(String propertyName) {
        return properties.getProperty(propertyName);
    }

    private String capabilityName(String property) {
        return property.substring(CAPABILITY.length());
    }

    private boolean isCapability(String property) {
        return property.startsWith(CAPABILITY);
    }
}
