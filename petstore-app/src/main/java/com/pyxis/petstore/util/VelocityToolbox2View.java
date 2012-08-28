package com.pyxis.petstore.util;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class VelocityToolbox2View extends VelocityToolboxView {

    // TODO find out how to inject this or inject the resource
    private final DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    protected Context createVelocityContext(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ViewToolContext velocityContext = new ViewToolContext(getVelocityEngine(), request, response, getServletContext());
        velocityContext.putAll(model);
        if (toolboxSpecified()) configureToolbox(velocityContext);
        return velocityContext;
    }

    private boolean toolboxSpecified() {
        return getToolboxConfigLocation() != null;
    }

    private void configureToolbox(ViewToolContext velocityContext) throws IOException {
        ToolboxFactory toolboxFactory = createToolboxFactory();
        velocityContext.addToolbox(requestToolbox(toolboxFactory));
        velocityContext.addToolbox(sessionToolbox(toolboxFactory));
        velocityContext.addToolbox(applicationToolbox(toolboxFactory));
    }

    private ToolboxFactory createToolboxFactory() throws IOException {
        XmlFactoryConfiguration factoryConfiguration = new XmlFactoryConfiguration(true);
        factoryConfiguration.read(resourceLoader.getResource(getToolboxConfigLocation()).getURL());
        ToolboxFactory toolboxFactory = new ToolboxFactory();
        toolboxFactory.configure(factoryConfiguration);
        return toolboxFactory;
    }

    private Toolbox applicationToolbox(ToolboxFactory toolboxFactory) {
        return toolboxFactory.createToolbox(Scope.APPLICATION);
    }

    private Toolbox sessionToolbox(ToolboxFactory toolboxFactory) {
        return toolboxFactory.createToolbox(Scope.SESSION);
    }

    private Toolbox requestToolbox(ToolboxFactory toolboxFactory) {
        return toolboxFactory.createToolbox(Scope.REQUEST);
    }
}
