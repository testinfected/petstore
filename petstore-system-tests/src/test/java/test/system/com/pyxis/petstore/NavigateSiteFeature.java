package test.system.com.pyxis.petstore;

import com.pyxis.petstore.domain.product.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

import static test.support.com.pyxis.petstore.builders.ItemBuilder.a;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.an;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

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
    startApplication() {
        application.start();
        inventoryIsNotEmpty();
    }

    private void inventoryIsNotEmpty() {
        Product iguana = aProduct().named("Iguana").build();
        application.addProducts(iguana);
        Product salamander = aProduct().named("Salamander").build();
        application.addProducts(salamander);
        application.addItems(an(iguana).withNumber("12345678").priced("50.00"), a(salamander));
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}