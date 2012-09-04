package test.com.pyxis.petstore.view;

import com.pyxis.petstore.domain.product.Item;
import org.junit.Test;
import org.w3c.dom.Element;
import test.support.com.pyxis.petstore.builders.ItemBuilder;
import test.support.com.pyxis.petstore.views.Routes;
import test.support.com.pyxis.petstore.views.VelocityRendering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.testinfected.hamcrest.dom.DomMatchers.hasAttribute;
import static org.testinfected.hamcrest.dom.DomMatchers.hasBlankText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSize;
import static org.testinfected.hamcrest.dom.DomMatchers.hasText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasUniqueSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.matches;
import static test.support.com.pyxis.petstore.builders.CartBuilder.aCart;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.views.ModelBuilder.aModel;
import static test.support.com.pyxis.petstore.views.VelocityRendering.render;

public class CartViewTest {

    Routes routes = Routes.to("/petstore");
    String CART_VIEW_TEMPLATE = "cart";
    Element cartView;

    @SuppressWarnings("unchecked")
    @Test public void
    displaysColumnHeadings() throws Exception {
        cartView = renderCartView().using(aModel().with(aCart())).asDom();
        assertThat("view", cartView,
                hasSelector("#cart-content th",
                        matches(hasText("Quantity"),
                                hasText("Item"),
                                hasText("Price"),
                                hasText("Total"))));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    displaysProductDetailsInColumns() throws Exception {
        Item item = anItem().withNumber("12345678").priced("18.50").describedAs("Green Adult").build();

        cartView = renderCartView().using(aModel().with(aCart().containing(item, item))).asDom();
        assertThat("view", cartView,
                hasSelector("tr#cart-item-12345678 td",
                        matches(hasText("2"),
                                hasText(containsString("Green Adult")),
                                hasText("18.50"),
                                hasText("37.00"),
                                hasBlankText())));
    }

    @Test public void
    displaysOneCartItemPerLine() throws Exception {
        ItemBuilder anItem = anItem();
        ItemBuilder anotherItem = anItem();
        cartView = renderCartView().using(aModel().with(aCart().containing(anItem, anItem, anotherItem))).asDom();
        assertThat("view", cartView, hasSelector("#cart-content tr[id^='cart-item']", hasSize(2)));
    }

    @Test public void
    displaysCartGrandTotal() throws Exception {
        cartView = renderCartView().using(aModel().with(aCart().containing(
                anItem().priced("20.00"),
                anItem().priced("12.99"),
                anItem().priced("43.97")))).asDom();
        String grandTotal = "76.96";

        assertThat("view", cartView, hasUniqueSelector("#cart-grand-total", hasText(grandTotal)));
    }

    @Test public void
    returnsToHomePageToContinueShopping() throws Exception {
        cartView = renderCartView().using(aModel().with(aCart().containing(anItem()))).asDom();
        assertThat("view", cartView, hasUniqueSelector("a#continue-shopping", hasAttribute("href", routes.homePath())));
    }

    @Test public void
    checkingOutRendersPaymentForm() throws Exception {
        cartView = renderCartView().using(aModel().with(aCart().containing(anItem()))).asDom();
        assertThat("view", cartView, hasUniqueSelector("#checkout a", hasAttribute("href", routes.checkoutPath())));
    }

    private VelocityRendering renderCartView() {
        return render(CART_VIEW_TEMPLATE).using(routes);
    }
}