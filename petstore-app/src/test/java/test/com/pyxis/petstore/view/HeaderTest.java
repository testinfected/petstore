package test.com.pyxis.petstore.view;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.w3c.dom.Element;
import test.support.com.pyxis.petstore.views.Routes;
import test.support.com.pyxis.petstore.views.VelocityRendering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.testinfected.hamcrest.dom.DomMatchers.anElement;
import static org.testinfected.hamcrest.dom.DomMatchers.hasAttribute;
import static org.testinfected.hamcrest.dom.DomMatchers.hasChildren;
import static org.testinfected.hamcrest.dom.DomMatchers.hasId;
import static org.testinfected.hamcrest.dom.DomMatchers.hasNoSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasTag;
import static org.testinfected.hamcrest.dom.DomMatchers.hasText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasUniqueSelector;
import static test.support.com.pyxis.petstore.builders.CartBuilder.aCart;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.views.ModelBuilder.aModel;
import static test.support.com.pyxis.petstore.views.VelocityRendering.render;

public class HeaderTest {

    String HEADER_TEMPLATE = "decorators/_header";
    Routes routes = Routes.to("/petstore");
    Element header;

    @Test public void
    linkToCartIsInactiveWhenCartIsEmpty() {
        header = renderHeader().using(aModel().with(aCart())).asDom();
        assertThat("header", header, hasNoSelector("#shopping-cart a"));
        assertThat("header", header, hasText(containsString("0")));
    }

    @SuppressWarnings("unchecked") @Test public void
    linkToCartDisplaysCartItemsCount() throws Exception {
        header = renderHeader().using(aModel().with(aCart().containing(anItem(), anItem()))).asDom();
        assertThat("header", header,
                hasUniqueSelector("#shopping-cart a",
                        hasAttribute("href", routes.cartPath()),
                        hasText(containsString("2"))));
    }

    @Test public void
    linkToHomeReturnsToToHomePage() {
        header = renderHeader().asDom();
        assertThat("header", header, hasUniqueSelector("#home a", hasAttribute("href", routes.homePath())));
    }

    @Test public void
    logoLinksToHomePage() {
        header = renderHeader().asDom();
        assertThat("content", header, hasUniqueSelector("#logo a", hasAttribute("href", routes.homePath())));
    }

    @SuppressWarnings("unchecked") @Test public void
    containsASearchBoxToQueryTheProductCatalog() {
        header = renderHeader().asDom();
        assertThat("header", header,
                hasUniqueSelector("#search-box form",
                        hasAttribute("action", routes.productsPath()),
                        hasAttribute("method", equalToIgnoringCase("GET"))));
        assertThat("header", header,
                hasUniqueSelector("#search-box form", hasChildren(keywordInputField(), searchButton())));
    }

    @SuppressWarnings("unchecked")
    private Matcher<Element> keywordInputField() {
        return anElement(hasTag("input"), hasId("keyword"), hasAttribute("type", "text"), hasAttribute("name", "keyword"));
    }

    @SuppressWarnings("unchecked")
    private Matcher<Element> searchButton() {
        return anElement(hasTag("button"), hasId("search"));
    }

    private VelocityRendering renderHeader() {
        return render(HEADER_TEMPLATE).using(routes);
    }
}
