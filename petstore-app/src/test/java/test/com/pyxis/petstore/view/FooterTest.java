package test.com.pyxis.petstore.view;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import test.support.com.pyxis.petstore.views.Routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testinfected.hamcrest.dom.DomMatchers.anElement;
import static org.testinfected.hamcrest.dom.DomMatchers.hasAttribute;
import static org.testinfected.hamcrest.dom.DomMatchers.hasChildren;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasTag;
import static test.support.com.pyxis.petstore.views.VelocityRendering.render;

public class FooterTest {

    String FOOTER_PARTIAL = "decorators/_footer";
    Routes routes = Routes.to("/petstore");
    Element footer;

    @Before public void
    renderFooter() {
        footer = render(FOOTER_PARTIAL).using(routes).asDom();
    }

    @SuppressWarnings("unchecked") @Test public void
    containsLogoutButtonThatSubmitsADeleteRequest() {
        assertThat("footer", footer, hasLogoutForm(hasAttribute("action", routes.logoutPath()), hasAttribute("method", "post")));
        assertThat("footer", footer, hasLogoutForm(hasChildren(
                anElement(hasTag("input"), hasAttribute("type", "hidden"), hasAttribute("name", "_method"), hasAttribute("value", "delete")),
                anElement(hasTag("button")))));
    }

    private Matcher<Element> hasLogoutForm(Matcher<Element>... formMatchers) {
        return hasSelector("#logout-box form", formMatchers);
    }
}
