package test.support.com.pyxis.petstore.web.browser;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class RemoteBrowser implements BrowserControl {

    private final URL url;
    private final DesiredCapabilities capabilities;

    public RemoteBrowser(URL url, Capabilities capabilities) {
        this.url = url;
        this.capabilities = new DesiredCapabilities(capabilities);
    }

    public WebDriver launch() {
        return new RemoteWebDriver(url, capabilities);
    }
}