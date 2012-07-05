package test.support.com.pyxis.petstore.web.browser;

import java.util.HashMap;
import java.util.Map;

public class BrowserControls {

    public static enum Option { passing, lasting, remote }

    private final Map<Option, BrowserControl> available = new HashMap<Option, BrowserControl>();

    public BrowserControls(BrowserProperties properties) {
        available.put(Option.passing, new PassingBrowser());
        available.put(Option.lasting, new LastingBrowser());
        available.put(Option.remote, new RemoteBrowser(properties));
    }

    public BrowserControl select(String lifeCycle) {
        return select(Option.valueOf(lifeCycle));
    }

    public BrowserControl select(Option lifeCycle) {
        return available.get(lifeCycle);
    }
}
