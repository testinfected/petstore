package test.system.com.pyxis.petstore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

public class SearchFeature {

    ApplicationDriver application = new ApplicationDriver(TestEnvironment.load());

    @Test public void
    searchesForAProductNotAvailableInStore() {
        application.addProducts(aProduct().named("Labrador Retriever"));

        application.searchFor("Dalmatian");
        application.showsNoResult();
    }

    @Test public void
    searchesAndFindsProductsInCatalog() {
        application.addProducts(aProduct("LAB-1234").named("Labrador Retriever"),
                aProduct("CHE-5678").named("Chesapeake").describedAs("Chesapeake bay retriever"),
                aProduct().named("Dalmatian"));

        application.searchFor("retriever");
        application.displaysNumberOfResults(2);
        application.displaysProduct("LAB-1234", "Labrador Retriever");
        application.displaysProduct("CHE-5678", "Chesapeake");
    }

    @Before public void
    startApplication() {
        application.start();
    }

    @After public void
    stopApplication() {
        application.stop();
    }
}
