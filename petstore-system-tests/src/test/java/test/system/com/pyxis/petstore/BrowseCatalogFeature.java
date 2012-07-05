package test.system.com.pyxis.petstore;

import com.pyxis.petstore.domain.product.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.SystemTestContext;

import static test.support.com.pyxis.petstore.builders.ItemBuilder.an;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;
import static test.support.com.pyxis.petstore.web.SystemTestContext.systemTesting;

public class BrowseCatalogFeature {

    SystemTestContext context = systemTesting();
    ApplicationDriver petstore;

    Product iguana;

    @Before public void
    iguanaAreForSale() {
        iguana = aProduct().named("Iguana").build();
        context.given(iguana);
    }

    @Test public void
    consultsAProductCurrentlyOutOfStock() {
        petstore.consultInventoryOf("Iguana");
        petstore.showsNoItemAvailable();
    }

    @Test public void
    consultsAProductAvailableItems() {
        context.given(an(iguana).withNumber("12345678").describedAs("Green Adult").priced("18.50"));

        petstore.consultInventoryOf("Iguana");
        petstore.displaysItem("12345678", "Green Adult", "18.50");
        petstore.continueShopping();
    }

    @Before public void
    startApplication() {
        petstore = context.startApplication();
    }

    @After public void
    stopApplication() {
        context.stopApplication(petstore);
    }
}
