package test.support.com.pyxis.petstore.web.browser;

import org.testinfected.hamcrest.ExceptionImposter;
import test.support.com.pyxis.petstore.Properties;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
        for (String property : properties.names()) {
            if (isCapability(property)) {
                capabilities.put(capabilityName(property), valueOf(property));
            }
        }
        return capabilities;
    }

    private String valueOf(String propertyName) {
        return properties.getString(propertyName);
    }

    private String capabilityName(String property) {
        return property.substring(CAPABILITY.length());
    }

    private boolean isCapability(String property) {
        return property.startsWith(CAPABILITY);
    }
}
