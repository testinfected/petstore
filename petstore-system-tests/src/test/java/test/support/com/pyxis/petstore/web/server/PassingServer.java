package test.support.com.pyxis.petstore.web.server;

public class PassingServer implements ServerLifeCycle {

    private final ServerProperties properties;
    private WebServer server;

    public PassingServer(ServerProperties properties) {
        this.properties = properties;
    }

    public void start() {
        server = new WebServer(properties);
        server.start();
    }

    public void stop() {
        server.stop();
    }
}
