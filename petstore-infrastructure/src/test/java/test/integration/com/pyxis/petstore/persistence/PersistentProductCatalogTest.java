package test.integration.com.pyxis.petstore.persistence;

import com.pyxis.petstore.Maybe;
import com.pyxis.petstore.domain.product.Product;
import com.pyxis.petstore.domain.product.ProductCatalog;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.builders.Builder;
import test.support.com.pyxis.petstore.builders.ProductBuilder;
import test.support.com.pyxis.petstore.db.Database;
import test.support.com.pyxis.petstore.db.TestEnvironment;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

public class PersistentProductCatalogTest {

    TestEnvironment environment = TestEnvironment.load();

    Database database = Database.in(environment);
    ProductCatalog productCatalog = environment.get(ProductCatalog.class);

    @Before public void
    cleanDatabase() {
        database.clean();
    }

    @After public void
    closeDatabase() {
        database.close();
    }

    @Test public void
    findsProductsByNumber() {
        database.given(aProduct().withNumber("PRD-0001"));

        Maybe<Product> product = productCatalog.findByNumber("PRD-0001");
        assertThat("no match", product.exists());
        assertThat("match", product.bare(), productWithNumber("PRD-0001"));
    }

    @Test public void
    findsNothingWhenNameAndDescriptionDoNoMatch() throws Exception {
        database.given(aProduct().named("Dalmatian").describedAs("A big dog"));

        Collection<Product> matchingProducts = productCatalog.findByKeyword("bulldog");
        assertThat("matching products", matchingProducts, is(empty()));
    }

    private Matcher<Collection<Product>> empty() {
        return Matchers.empty();
    }

    @SuppressWarnings("unchecked")
    @Test public void
    findsProductsByMatchingName() throws Exception {
        database.given(aProduct().named("English Bulldog"), and(aProduct().named("French Bulldog")), and(aProduct().named("Labrador Retriever")));

        Collection<Product> matches = productCatalog.findByKeyword("bull");
        assertThat("matching products", matches, hasSize(equalTo(2)));
        assertThat("matches", matches, containsInAnyOrder(productNamed("English Bulldog"), productNamed("French Bulldog")));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    findsProductsByMatchingDescription() throws Exception {
        database.given(aProduct().named("Labrador").describedAs("Friendly"), and(aProduct().named("Golden").describedAs("Kids best friend")), and(aProduct().named("Poodle").describedAs("Annoying")));

        List<Product> matches = productCatalog.findByKeyword("friend");
        assertThat("matching products", matches, hasSize(equalTo(2)));
        assertThat("matches", matches, containsInAnyOrder(productNamed("Labrador"), productNamed("Golden")));
    }

    @Test(expected = ConstraintViolationException.class)
    public void cannotPersistAProductWithoutAName() throws Exception {
        productCatalog.add(aProduct().withoutAName().build());
    }

    @Test(expected = ConstraintViolationException.class)
    public void cannotPersistAProductWithoutANumber() throws Exception {
        productCatalog.add(aProduct().withoutANumber().build());
    }

    @Test public void
    canRoundTripProducts() throws Exception {
        final Collection<Product> sampleProducts = Arrays.asList(
                aProduct().named("Labrador").describedAs("Labrador Retriever").withPhoto("labrador.png").build(),
                aProduct().named("Dalmatian").build());

        for (Product product : sampleProducts) {
            productCatalog.add(product);
            database.assertCanBeReloadedWithSameState(product);
        }
    }

    @Test public void
    productNumberShouldBeUnique() throws Exception {
        ProductBuilder someProduct = aProduct().withNumber("LAB-1234");
        database.persist(someProduct);
        try {
            productCatalog.add(someProduct.build());
            fail("No constraint violation");
        } catch (org.hibernate.exception.ConstraintViolationException expected) {
            assertTrue(true);
        }
    }

    private Builder<?> and(Builder<?> builder) {
        return builder;
    }

    private Matcher<Iterable<Product>> hasSize(Matcher<? super Integer> sizeMatcher) {
        return iterableWithSize(sizeMatcher);
    }

    private Matcher<Product> productNamed(String name) {
        return new FeatureMatcher<Product, String>(equalTo(name), "a product named", "product name") {
            @Override protected String featureValueOf(Product actual) {
                return actual.getName();
            }
        };
    }

    private Matcher<Product> productWithNumber(String number) {
        return new FeatureMatcher<Product, String>(equalTo(number), "a product with number", "product number") {
            @Override protected String featureValueOf(Product actual) {
                return actual.getNumber();
            }
        };
    }
}
