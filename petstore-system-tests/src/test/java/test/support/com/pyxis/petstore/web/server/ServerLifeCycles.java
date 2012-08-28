package test.support.com.pyxis.petstore.web.server;

import java.util.HashMap;
import java.util.Map;

public class ServerLifeCycles {

    public static enum Option {
        passing, lasting, external
    }

    private final Map<Option, ServerLifeCycle> available = new HashMap<Option, ServerLifeCycle>();

    public ServerLifeCycles(ServerProperties properties) {
        available.put(Option.external, new ExternalServer());
        available.put(Option.lasting, new LastingServer(properties));
        available.put(Option.passing, new PassingServer(properties));
    }

    public ServerLifeCycle select(String lifeCycle) {
        return select(Option.valueOf(lifeCycle));
    }

    public ServerLifeCycle select(Option lifeCycle) {
        return available.get(lifeCycle);
    }
}
