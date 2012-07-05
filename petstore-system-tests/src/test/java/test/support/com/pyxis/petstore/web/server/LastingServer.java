package test.support.com.pyxis.petstore.web.server;

public class LastingServer implements ServerLifeCycle {

    private final WebServer shared;

    public LastingServer(ServerProperties properties) {
        this.shared = new WebServer(properties);
    }

    public void start() {
        shared.start();
    }

    public void stop()  {
        shared.stopOnShutdown();
    }
}
