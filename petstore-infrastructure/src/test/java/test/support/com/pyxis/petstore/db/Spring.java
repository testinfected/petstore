package test.support.com.pyxis.petstore.db;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import javax.sql.DataSource;
import java.util.Properties;

import static java.util.Arrays.asList;

public class Spring {

    private static final String PERSISTENCE_CONFIGURATION = "classpath:persistence.xml";

    private final ApplicationContext context;

    public Spring(Properties properties) {
        context = loadFrom(properties);
    }

    public <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }

    public DataSource getDataSource() {
        return getBean(DataSource.class);
    }

    private ApplicationContext loadFrom(Properties properties) {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.registerBeanDefinition("property-configurer", definePropertyConfigurer(properties));
        context.load(PERSISTENCE_CONFIGURATION);
        context.refresh();
        return context;
    }

    private GenericBeanDefinition definePropertyConfigurer(Properties properties) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(PropertyPlaceholderConfigurer.class);
        beanDefinition.setPropertyValues(new MutablePropertyValues(
                asList(new PropertyValue("properties", properties))
        ));
        return beanDefinition;
    }
}
