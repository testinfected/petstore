package test.support.com.pyxis.petstore.web.server;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.testinfected.hamcrest.ExceptionImposter;

public class WebServer implements ServerDriver {

    private static final int SHUTDOWN_TIMEOUT = 1000;
    private static final int MAX_THREADS = 5;

    private final Server server = new Server();

    private String host;
    private int port;
    private String contextPath;
    private String webAppPath;

    public WebServer(ServerProperties properties) {
        this(properties.host(), properties.port(), properties.contextPath(), properties.webAppPath());
    }

    public WebServer(String host, int port, String contextPath, String webAppPath) {
        this.host = host;
        this.port = port;
        this.contextPath = contextPath;
        this.webAppPath = webAppPath;

        configureThreadPool();
        configureConnector();
        bindDefaultHandler();
        configureApplication();
        configureExtraOptions();
    }

    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw ExceptionImposter.imposterize(e);
        }
    }

    public void stop()  {
        try {
            server.stop();
        } catch (Exception e) {
            throw ExceptionImposter.imposterize(e);
        }
    }

    public void stopOnShutdown() {
        server.setStopAtShutdown(true);
    }
    private void configureThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool(MAX_THREADS);
        server.setThreadPool(threadPool);
    }

    private void configureConnector() {
        Connector connector = new SelectChannelConnector();
        connector.setHost(host);
        connector.setPort(port);
        server.addConnector(connector);
    }

    private void bindDefaultHandler() {
        server.setHandler(new HandlerList());
        server.addHandler(new DefaultHandler());
    }

    private void configureApplication() {
        WebAppContext appContext = new WebAppContext();
        appContext.setContextPath(contextPath);
        appContext.setWar(webAppPath);
        server.setHandler(appContext);
    }

    private void configureExtraOptions() {
        server.setGracefulShutdown(SHUTDOWN_TIMEOUT);
    }
}