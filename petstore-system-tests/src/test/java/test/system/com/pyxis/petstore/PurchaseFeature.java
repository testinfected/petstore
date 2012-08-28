package test.system.com.pyxis.petstore;

import com.pyxis.petstore.domain.product.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

import static test.support.com.pyxis.petstore.builders.ItemBuilder.a;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

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
    startApplication() {
        application.start();
        labradorsAreForSale();
    }

    private void labradorsAreForSale() {
        Product labrador = aProduct().named("Labrador Retriever").build();
        Product golden = aProduct().named("Golden Retriever").build();
        application.addProducts(labrador, golden);
        application.addItems(
                a(labrador).withNumber("11111111").describedAs("Male Adult").priced("599.00"),
                a(golden).withNumber("22222222").describedAs("Female Adult").priced("649.00"));
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}
