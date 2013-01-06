package test.com.pyxis.petstore.view;

import org.junit.Test;
import org.w3c.dom.Element;
import test.support.com.pyxis.petstore.views.Routes;
import test.support.com.pyxis.petstore.views.VelocityRendering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testinfected.hamcrest.dom.DomMatchers.hasAttribute;
import static org.testinfected.hamcrest.dom.DomMatchers.hasBlankText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasChild;
import static org.testinfected.hamcrest.dom.DomMatchers.hasId;
import static org.testinfected.hamcrest.dom.DomMatchers.hasNoSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSize;
import static org.testinfected.hamcrest.dom.DomMatchers.hasTag;
import static org.testinfected.hamcrest.dom.DomMatchers.hasText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasUniqueSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.matches;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.views.ModelBuilder.aModel;
import static test.support.com.pyxis.petstore.views.ModelBuilder.anEmptyModel;
import static test.support.com.pyxis.petstore.views.VelocityRendering.render;

public class ItemsViewTest {

    Routes routes = Routes.to("/petstore");
    String ITEMS_VIEW_TEMPLATE = "items";
    Element itemsView;

    @Test public void
    indicatesWhenInventoryIsEmpty() throws Exception {
        itemsView = renderItemsView().using(anEmptyModel()).asDom();
        assertThat("view", itemsView, hasUniqueSelector("#out-of-stock"));
        assertThat("view", itemsView, hasNoSelector("#inventory"));
    }

    @Test public void
    displaysNumberOfItemsAvailable() throws Exception {
        itemsView = renderItemsView().using(aModel().listing(anItem(), anItem())).asDom();
        assertThat("view", itemsView, hasUniqueSelector("#inventory-count", hasText("2")));
        assertThat("view", itemsView, hasSelector("#inventory tr[id^='item']", hasSize(2)));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    displaysColumnHeadingsOnItemsTable() throws Exception {
        itemsView = renderItemsView().using(aModel().listing(anItem())).asDom();
        assertThat("view", itemsView,
                hasSelector("#items th",
                        matches(hasText("Reference number"),
                                hasText("Description"),
                                hasText("Price"),
                                hasBlankText())));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    displaysItemDetailsInColumns() throws Exception {
        itemsView = renderItemsView().using(aModel().listing(anItem().
                withNumber("12345678").
                describedAs("Green Adult").
                priced("18.50"))).asDom();
        assertThat("view", itemsView,
                hasSelector("tr#item-12345678 td",
                        matches(hasText("12345678"),
                                hasText("Green Adult"),
                                hasText("18.50"),
                                hasChild(hasTag("form")))));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    addToCartButtonAddsItemToShoppingCart() throws Exception {
        itemsView = renderItemsView().using(aModel().listing(anItem().withNumber("12345678"))).asDom();
        assertThat("view", itemsView,
                hasUniqueSelector("form",
                        hasAttribute("action", routes.cartItemsPath()),
                        hasAttribute("method", "post"),
                        hasUniqueSelector("button", hasId("add-to-cart-12345678"))));
        assertThat("view", itemsView,
                hasUniqueSelector("form input[type='hidden']",
                        hasAttribute("name", "item_number"),
                        hasAttribute("value", "12345678")));
    }

    @Test public void
    returnsToHomePageToContinueShopping() throws Exception {
        itemsView = renderItemsView().asDom();
        assertThat("view", itemsView, hasUniqueSelector("a#continue-shopping", hasAttribute("href", routes.homePath())));
    }

    private VelocityRendering renderItemsView() {
        return render(ITEMS_VIEW_TEMPLATE).using(routes);
    }
}