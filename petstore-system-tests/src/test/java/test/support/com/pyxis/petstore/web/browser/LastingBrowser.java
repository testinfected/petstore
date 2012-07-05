package test.support.com.pyxis.petstore.web.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LastingBrowser implements BrowserControl {

    private WebDriver browser;

    public LastingBrowser() {
    }

    public WebDriver launch() {
        if (!started()) {
            browser = launchBrowser();
        }
        return browser;
    }

    private boolean started() {
        return browser != null;
    }

    protected WebDriver launchBrowser() {
        FirefoxDriver browser = new FirefoxDriver() {
            public void close() {
            }
        };
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(browser));
        return browser;
    }

    private class ShutdownHook extends Thread {
        private ShutdownHook(final WebDriver browser) {
            super(new Runnable() {
                public void run() {
                    browser.quit();
                }
            });
        }
    }
}
