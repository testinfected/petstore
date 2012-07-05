package test.system.com.pyxis.petstore;

import com.pyxis.petstore.domain.product.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.SystemTestContext;

import static test.support.com.pyxis.petstore.builders.ItemBuilder.a;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.an;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;
import static test.support.com.pyxis.petstore.web.SystemTestContext.systemTesting;

public class NavigateSiteFeature {

    SystemTestContext context = systemTesting();
    ApplicationDriver petstore;

    @Before public void
    inventoryIsNotEmpty() {
        Product iguana = aProduct().named("Iguana").build();
        context.given(iguana);
        Product salamander = aProduct().named("Salamander").build();
        context.given(salamander);
        context.given(an(iguana).withNumber("12345678").priced("50.00"), a(salamander));
    }

    @Test public void
    stopsBrowsingCatalog() {
        petstore.consultInventoryOf("Iguana");
        petstore.continueShopping();
        petstore.returnHome();
    }

    @Test public void
    reviewsCartContentWhileShopping() {
        petstore.consultInventoryOf("Iguana");
        petstore.buy("12345678");
        petstore.continueShopping();

        petstore.consultInventoryOf("Salamander");
        petstore.reviewCart();
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