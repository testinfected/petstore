package test.support.com.pyxis.petstore.web.server;

public class PassingServer implements ServerLifeCycle {

    private final ServerProperties properties;

    public PassingServer(ServerProperties properties) {
        this.properties = properties;
    }

    public ServerDriver start() {
        ServerDriver server = new WebServer(properties);
        server.start();
        return server;
    }

    public void stop(ServerDriver server) {
        server.stop();
    }
}
