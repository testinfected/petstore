package test.support.com.pyxis.petstore;

import org.testinfected.hamcrest.ExceptionImposter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFile {

    public static Properties load(String resource) {
        return load(resource, Thread.currentThread().getContextClassLoader());
    }

    private static Properties load(String resource, ClassLoader classLoader) {
        InputStream config = classLoader.getResourceAsStream(resource);
        if (config == null) throw new IllegalArgumentException("Property file not found: " + resource);

        return load(config);
    }

    private static Properties load(InputStream stream) {
        Properties props = new Properties();
        try {
            props.load(stream);
        } catch (IOException e) {
            throw ExceptionImposter.imposterize(e);
        }
        return props;
    }

    private PropertyFile() {
    }
}