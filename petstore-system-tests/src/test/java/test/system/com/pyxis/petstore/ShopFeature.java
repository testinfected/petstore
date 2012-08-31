package test.system.com.pyxis.petstore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

import java.io.IOException;

public class ShopFeature {

    ApplicationDriver application = new ApplicationDriver(TestEnvironment.load());

    @Test public void
    shopsForItemsAndAddsThemToCart() {
        application.showsCartIsEmpty();

        application.buy("Iguana", "12345678");
        application.showsItemInCart("12345678", "Green Adult", "18.50");
        application.showsGrandTotal("18.50");
        application.showsCartTotalQuantity(1);
        application.continueShopping();

        application.buy("Iguana", "87654321");
        application.showsItemInCart("87654321", "Blue Female", "58.97");
        application.showsGrandTotal("77.47");
        application.showsCartTotalQuantity(2);
    }

    @Test public void
    shopsForTheSameItemMultipleTimes() {
        application.buy("Iguana", "12345678");
        application.showsItemQuantity("12345678", 1);
        application.continueShopping();

        application.buy("Iguana", "12345678");
        application.showsItemQuantity("12345678", 2);
        application.showsCartTotalQuantity(2);
    }

    @Before public void
    startApplication() throws Exception {
        application.start();
        iguanaAreForSale();
    }

    private void iguanaAreForSale() throws IOException {
        application.addProduct("LIZ-0001", "Iguana");
        application.addItem("LIZ-0001", "12345678", "Green Adult", "18.50");
        application.addItem("LIZ-0001", "87654321", "Blue Female", "58.97");
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}