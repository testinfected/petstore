package test.system.com.pyxis.petstore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

public class NavigateSiteFeature {

    ApplicationDriver application = new ApplicationDriver(TestEnvironment.load());

    @Test public void
    stopsBrowsingCatalog() {
        application.consultInventoryOf("Iguana");
        application.continueShopping();
        application.returnHome();
    }

    @Test public void
    reviewsCartContentWhileShopping() {
        application.consultInventoryOf("Iguana");
        application.buy("12345678");
        application.continueShopping();

        application.consultInventoryOf("Salamander");
        application.reviewCart();
    }

    @Before public void
    startApplication() throws Exception {
        application.start();
        populateInventory();
    }

    private void populateInventory() throws Exception {
        application.addProduct("LIZ-0001", "Iguana");
        application.addProduct("LIZ-0002", "Salamander");
        application.addItem("LIZ-0001", "12345678", "Blue skin", "50.00");
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}