package test.support.com.pyxis.petstore.web.server;

public class LastingServer implements ServerLifeCycle {

    private final WebServer shared;

    public LastingServer(String host, int port, String contextPath, String webappPath) {
        this.shared = new WebServer(host, port, contextPath, webappPath);
    }

    public void start() {
        shared.start();
    }

    public void stop()  {
        shared.stopOnShutdown();
    }
}
