package test.system.com.pyxis.petstore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.web.ApplicationDriver;
import test.support.com.pyxis.petstore.web.TestEnvironment;

import java.io.IOException;

public class SearchFeature {

    ApplicationDriver application = new ApplicationDriver(TestEnvironment.load());

    @Test public void
    searchesForAProductNotAvailableInStore() throws Exception {
        application.addProduct("DOG-0001", "Labrador Retriever");
        application.searchFor("Dalmatian");
        application.showsNoResult();
    }

    @Test public void
    searchesAndFindsProductsInCatalog() throws IOException {
        application.addProduct("DOG-0001", "Labrador Retriever");
        application.addProduct("DOG-0002", "Chesapeake", "Chesapeake bay retriever");
        application.addProduct("DOG-0003", "Dalmatian");

        application.searchFor("retriever");
        application.displaysNumberOfResults(2);
        application.displaysProduct("DOG-0001", "Labrador Retriever");
        application.displaysProduct("DOG-0002", "Chesapeake");
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
