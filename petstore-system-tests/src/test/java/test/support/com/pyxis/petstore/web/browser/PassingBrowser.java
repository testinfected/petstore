package test.support.com.pyxis.petstore.web.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class PassingBrowser implements BrowserControl {

    public WebDriver launch() {
        return new FirefoxDriver();
    }
}