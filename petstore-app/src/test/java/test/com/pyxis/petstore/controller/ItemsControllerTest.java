package test.com.pyxis.petstore.controller;

import com.pyxis.petstore.controller.ItemsController;
import com.pyxis.petstore.domain.product.Item;
import com.pyxis.petstore.domain.product.ItemInventory;
import com.pyxis.petstore.domain.product.Product;
import com.pyxis.petstore.domain.product.ProductCatalog;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.DataBinder;

import java.util.Arrays;
import java.util.List;

import static com.pyxis.petstore.Maybe.some;
import static org.hamcrest.Matchers.sameInstance;
import static org.testinfected.hamcrest.spring.SpringMatchers.hasAttribute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

@RunWith(JMock.class)
public class ItemsControllerTest {

    Mockery context = new JUnit4Mockery();
    ProductCatalog productCatalog = context.mock(ProductCatalog.class);
    ItemInventory itemInventory = context.mock(ItemInventory.class);
    ItemsController itemsController = new ItemsController(productCatalog, itemInventory);
    Model model = new ExtendedModelMap();

    int CREATED = 201;

    @Test public void
    retrievesItemsByProductNumberAndMakeThemAvailableToView() {
    	final List<Item> anItemList = Arrays.asList(anItem().build());
    	context.checking(new Expectations(){{
    		oneOf(itemInventory).findByProductNumber("LAB-1234"); will(returnValue(anItemList));
    	}});
        String view = itemsController.index("LAB-1234", model);
        assertThat("view", view, equalTo("items"));
        assertThat("model", model, hasAttribute("itemList", anItemList));
    }

    @Test public void
    addsItemToInventory() {
        final Item item = anItem().build();
        context.checking(new Expectations() {{
            oneOf(itemInventory).add(with(same(item)));
        }});
        MockHttpServletResponse response = new MockHttpServletResponse();

        itemsController.create(item, response);

        assertThat("status code", response.getStatus(), equalTo(CREATED));
    }

    @Test public void
    automaticallyConvertsProductNumbersToProductsWhenAddingItemsToInventory() {
        final Product product = aProduct("PRD-0001").build();
        context.checking(new Expectations() {{
            oneOf(productCatalog).findByNumber("PRD-0001"); will(returnValue(some(product)));
        }});

        final Item item = anItem().withoutAProduct().build();
        DataBinder binder = new DataBinder(item);
        itemsController.initBinder(binder);

        binder.bind(new MutablePropertyValues() {{
            addPropertyValue("product", "PRD-0001");
        }});
        assertThat("item", item, itemOfProduct(sameInstance(product)));
    }

    private Matcher<Item> itemOfProduct(Matcher<Product> product) {
        return new FeatureMatcher<Item, Product>(product, "item of product", "product") {
            protected Product featureValueOf(Item actual) {
                return actual.getProduct();
            }
        };
    }
}
