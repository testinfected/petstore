package test.system.com.pyxis.petstore;

import com.pyxis.petstore.domain.product.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

import static test.support.com.pyxis.petstore.builders.ItemBuilder.an;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

public class BrowseCatalogFeature {

    ApplicationDriver application = new ApplicationDriver(TestEnvironment.load());
    Product iguana;

    @Test public void
    consultsAProductCurrentlyOutOfStock() {
        application.consultInventoryOf("Iguana");
        application.showsNoItemAvailable();
    }

    @Test public void
    consultsAProductAvailableItems() {
        application.addItems(an(iguana).withNumber("12345678").describedAs("Green Adult").priced("18.50"));

        application.consultInventoryOf("Iguana");
        application.displaysItem("12345678", "Green Adult", "18.50");
        application.continueShopping();
    }

    @Before public void
    startApplication() {
        application.start();
        iguanaAreForSale();
    }

    private void iguanaAreForSale() {
        iguana = aProduct().named("Iguana").build();
        application.addProducts(iguana);
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}
