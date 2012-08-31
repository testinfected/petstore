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
        LastingWebDriver browser = new LastingWebDriver();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(browser));
        return browser;
    }

    private static class LastingWebDriver extends FirefoxDriver {
        public void shutdown() {
            super.quit();
        }

        public void quit() {
        }
    }

    private class ShutdownHook extends Thread {
        private ShutdownHook(final LastingWebDriver browser) {
            super(new Runnable() {
                public void run() {
                    browser.shutdown();
                }
            });
        }
    }
}
