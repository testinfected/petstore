package test.support.com.pyxis.petstore.web.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteBrowser implements BrowserControl {

    private final BrowserProperties properties;

    public RemoteBrowser(BrowserProperties properties) {
        this.properties = properties;
    }

    public WebDriver launch() {
        DesiredCapabilities capabilities = new DesiredCapabilities(properties.capabilities());
        return new RemoteWebDriver(properties.remoteUrl(), capabilities);
    }
}