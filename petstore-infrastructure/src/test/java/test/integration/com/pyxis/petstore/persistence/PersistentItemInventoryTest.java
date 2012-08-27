package test.integration.com.pyxis.petstore.persistence;

import com.pyxis.petstore.domain.product.Item;
import com.pyxis.petstore.domain.product.ItemInventory;
import com.pyxis.petstore.domain.product.ItemNumber;
import com.pyxis.petstore.domain.product.Product;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.builders.ItemBuilder;
import test.support.com.pyxis.petstore.db.Database;
import test.support.com.pyxis.petstore.db.TestEnvironment;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.testinfected.hamcrest.jpa.HasFieldWithValue.hasField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

public class PersistentItemInventoryTest {

    TestEnvironment environment = TestEnvironment.load();

    Database database = Database.in(environment);
    ItemInventory itemInventory = environment.get(ItemInventory.class);

    Product product = aProduct().build();

    @Before public void
    cleanDatabase() {
        database.clean();
    }
    
    @After public void
    closeDatabase() {
        database.close();
    }

    @Test public void
    findsItemsByNumber() {
        database.given(product);
        database.given(anItem().of(product).withNumber("12345678"));

        Item found = itemInventory.find(new ItemNumber("12345678"));
        assertThat("inventory", found, itemWithNumber("12345678"));
    }

    @Test public void
    findsItemsByProductNumber() {
        Product product = aProduct().withNumber("LAB-1234").build();
        database.given(product);
        database.given(anItem().of(product), anItem().of(product));

        List<Item> availableItems = itemInventory.findByProductNumber("LAB-1234");
        assertThat("available items", availableItems, everyItem(anItemOfProduct(productWithNumber("LAB-1234"))));
    }

    @Test public void
    findsNothingIfProductHasNoItemInInventory() {
        database.given(aProduct().withNumber("DAL-5432"));

        List<Item> availableItems = itemInventory.findByProductNumber("DAL-5432");
        assertThat("available items", availableItems, Matchers.<Item>empty());
    }

    @Test public void
    itemIsInvalidWithoutAnItemNumber() {
        Product product = aProduct().build();
        database.persist(product);
        assertFailsPersisting(anItemWithoutAReferenceNumber(product));
    }

    @Test public void
    referenceNumberShouldBeUnique() {
        database.given(product);
        ItemBuilder item = anItem().of(product).withNumber("LAB-1234");
        database.given(item.build());

        assertViolatesUniqueness(item.build());
    }

    @Test(expected = ConstraintViolationException.class) public void
    itemIsInvalidWithoutAnAssociatedProduct() {
        database.persist(anItemWithoutAnAssociatedProduct());
    }

    @Test(expected = ConstraintViolationException.class) public void
    itemIsInvalidWithoutAPrice() {
        database.persist(anItemWithoutAPrice());
    }

    @Test public void
    canRoundTripItems() {
        Product product = aProduct().build();
        final Collection<Item> sampleItems = Arrays.asList(
                anItem().of(product).withNumber("12345678").describedAs("Chocolate male").priced("58.00").build(),
                anItem().of(product).withNumber("87654321").build());

        database.persist(product);
        for (Item item : sampleItems) {
            database.persist(item);
            database.assertCanBeReloadedWithSameState(item);
        }
    }

    private Matcher<Item> itemWithNumber(final String number) {
        return new FeatureMatcher<Item, String>(equalTo(number), "an item with number", "item number") {
            @Override protected String featureValueOf(Item actual) {
                return actual.getNumber();
            }
        };
    }

    private Matcher<Item> anItemOfProduct(Matcher<? super Product> productMatcher) {
        return hasField("product", productMatcher);
    }

    private Matcher<Product> productWithNumber(final String number) {
        return new FeatureMatcher<Product, String>(equalTo(number), "a product with number", "product number") {
            @Override protected String featureValueOf(Product actual) {
                return actual.getNumber();
            }
        };
    }

    private ItemBuilder anItemWithoutAReferenceNumber(Product product) {
        return anItem().of(product).withNumber(null);
    }

    private ItemBuilder anItemWithoutAnAssociatedProduct() {
        return anItem().of((Product) null);
    }

    private ItemBuilder anItemWithoutAPrice() {
        return anItem().priced((BigDecimal) null);
    }

    private void assertViolatesUniqueness(Item item) {
        try {
            database.persist(item);
            fail("No constraint violation");
        } catch (org.hibernate.exception.ConstraintViolationException expected) {
            assertTrue(true);
        }
    }

    private void assertFailsPersisting(ItemBuilder item) {
        try {
            database.persist(item);
            fail("No validation violation");
        } catch (ConstraintViolationException expected) {
            assertTrue(true);
        }
    }
}
