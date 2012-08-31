package test.support.com.pyxis.petstore.web.server;

public class PassingServer implements ServerLifeCycle {

    private final String host;
    private final int port;
    private final String contextPath;
    private final String webappPath;

    private WebServer server;

    public PassingServer(String host, int port, String contextPath, String webappPath) {
        this.host = host;
        this.port = port;
        this.contextPath = contextPath;
        this.webappPath = webappPath;
    }

    public void start() {
        server = new WebServer(host, port, contextPath, webappPath);
        server.start();
    }

    public void stop() {
        server.stop();
    }
}
