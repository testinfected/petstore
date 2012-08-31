package test.system.com.pyxis.petstore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

public class PurchaseFeature {

    ApplicationDriver application = new ApplicationDriver(TestEnvironment.load());

    @Test public void
    purchasesSeveralItemsUsingACreditCard() {
        application.buy("Labrador Retriever", "11111111");
        application.buy("Golden Retriever", "22222222");
        application.checkout();
        application.showsTotalToPay("1248.00");

        application.pay("John", "Leclair", "jleclair@gmail.com", "Visa", "9999 9999 9999 9999", "12/12");
        application.showsTotalPaid("1248.00");
        application.showsLineItem("11111111", "Male Adult", "599.00");
        application.showsLineItem("22222222", "Female Adult", "649.00");
        application.showsBillingInformation("John", "Leclair", "jleclair@gmail.com");
        application.showsCreditCardDetails("Visa", "9999 9999 9999 9999", "12/12");

        application.continueShopping();
        application.showsCartIsEmpty();
    }

    @Before public void
    startApplication() throws Exception {
        application.start();
        retrieversAreForSale();
    }

    private void retrieversAreForSale() throws Exception {
        application.addProduct("DOG-0001", "Labrador Retriever");
        application.addProduct("DOG-0002", "Golden Retriever");
        application.addItem("DOG-0001", "11111111", "Male Adult", "599.00");
        application.addItem("DOG-0002", "22222222", "Female Adult", "649.00");
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}
