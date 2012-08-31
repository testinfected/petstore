package test.system.com.pyxis.petstore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

public class BrowseCatalogFeature {

    ApplicationDriver application = new ApplicationDriver(TestEnvironment.load());

    @Test public void
    consultsAProductCurrentlyOutOfStock() throws Exception {
        application.addProduct("LIZ-0001", "Iguana");
        application.consultInventoryOf("Iguana");
        application.showsNoItemAvailable();
    }

    @Test public void
    consultsAProductAvailableItems() throws Exception {
        application.addProduct("LIZ-0001", "Iguana");
        application.addItem("LIZ-0001", "12345678", "Green Adult", "18.50");

        application.consultInventoryOf("Iguana");
        application.displaysItem("12345678", "Green Adult", "18.50");
        application.continueShopping();
    }

    @Before public void
    startApplication() throws Exception {
        application.start();
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}
