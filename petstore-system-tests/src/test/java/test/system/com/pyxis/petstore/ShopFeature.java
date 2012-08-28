package test.system.com.pyxis.petstore;

import com.pyxis.petstore.domain.product.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

import static test.support.com.pyxis.petstore.builders.ItemBuilder.an;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

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
    startApplication() {
        application.start();
        iguanaAreForSale();
    }

    private void iguanaAreForSale() {
        Product iguana = aProduct().named("Iguana").build();
        application.addProducts(iguana);
        application.addItems(
                an(iguana).withNumber("12345678").describedAs("Green Adult").priced("18.50"),
                an(iguana).withNumber("87654321").describedAs("Blue Female").priced("58.97"));
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}