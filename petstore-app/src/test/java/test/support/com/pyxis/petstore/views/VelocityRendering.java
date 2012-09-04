package test.support.com.pyxis.petstore.views;

import org.springframework.core.io.FileSystemResourceLoader;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static test.support.com.pyxis.petstore.views.HTMLDocument.toElement;

public class VelocityRendering {

	private static final String UTF_8 = "UTF-8";
    private static final String VELOCITY_CONFIG_FILE_URL_KEY = "velocity.config.url";
	private static final String TEMPLATES_BASE_URL_KEY = "templates.base.url";
	private static final String VIEWS_PROPERTIES_FILENAME = "/views.properties";
	private static final String VELOCITY_EXTENSION = ".vm";
    private static final String PETSTORE_MACRO_LIBRARY = "com/pyxis/petstore/helpers/petstore.vm";

    public static VelocityRendering render(String template) {
        return new VelocityRendering(template);
    }

    private final String template;
    private VelocityEngine velocityEngine;
    private Routes routes = Routes.root();
    private ResourceLoader resourceLoader = new FileSystemResourceLoader();
    private String renderedView;
    private MockRequestContext mockRequestContext = new MockRequestContext();

    private Map<String, Object> model = new ExtendedModelMap();

    private VelocityRendering(String template) {
		this.template = template;
	}

    public VelocityRendering using(Routes routes) {
        this.routes = routes;
        return this;
    }

    public VelocityRendering bind(BindingResult result) {
        mockRequestContext.bind(result);
        return this;
    }

    public VelocityRendering using(ModelBuilder modelBuilder) {
        return using(modelBuilder.asMap());
    }

	public VelocityRendering using(Map<String, Object> model) {
        this.model.putAll(model);
        return this;
	}

    public String asString() throws Exception {
        render();
        return renderedView;
    }

    public Element asDom() throws Exception {
        return toElement(asString());
    }

    private void render() throws IOException {
        loadVelocityEngine();
        setupTools();
        exposeRequestContext();
        renderTemplate();
    }

    private void loadVelocityEngine() throws IOException {
        VelocityConfigurer velocityConfigurer = new VelocityConfigurer() {
            @Override protected void postProcessVelocityEngine(VelocityEngine velocityEngine) {
                super.postProcessVelocityEngine(velocityEngine);
                velocityEngine.addProperty(
                        VelocityEngine.VM_LIBRARY, PETSTORE_MACRO_LIBRARY);
            }
        };
        Properties properties = loadViewProperties();
        velocityConfigurer.setConfigLocation(getResource(velocityConfigFileUrl(properties)));
        velocityConfigurer.setResourceLoader(resourceLoader);
        velocityConfigurer.setResourceLoaderPath(templatesBaseUrl(properties));
        velocityConfigurer.setOverrideLogging(false);
        velocityConfigurer.afterPropertiesSet();
        velocityEngine = velocityConfigurer.getVelocityEngine();
    }

    private Properties loadViewProperties() throws IOException {
        Properties properties = new Properties();
        InputStream resource = VelocityRendering.class.getResourceAsStream(VIEWS_PROPERTIES_FILENAME);
        if (resource == null) throw new IllegalArgumentException("Property file not found: " + VIEWS_PROPERTIES_FILENAME);
        properties.load(resource);
        return properties;
    }

    private Resource getResource(final String location) throws IOException {
        return resourceLoader.getResource(location);
    }

    private String velocityConfigFileUrl(Properties properties) throws IOException {
        return properties.getProperty(VELOCITY_CONFIG_FILE_URL_KEY);
    }

    private String templatesBaseUrl(Properties properties) {
        return properties.getProperty(TEMPLATES_BASE_URL_KEY);
    }

    private void setupTools() {
        model.put("base", routes.contextPath());
        model.put("display", new DisplayTool());
        model.put("date", dateTool());
    }

    private DateTool dateTool() {
        DateTool dateTool = new DateTool();
        Map<String, String> dateToolParams = new HashMap<String, String>();
        dateToolParams.put("format", "yyyy-MM-dd");
        dateTool.configure(dateToolParams);
        return dateTool;
    }

    private void exposeRequestContext() {
        model.put(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, mockRequestContext);
    }

    private void renderTemplate() {
        renderedView = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateFileName(), UTF_8, model);
    }

    private String templateFileName()  {
		return template + VELOCITY_EXTENSION;
	}
}
