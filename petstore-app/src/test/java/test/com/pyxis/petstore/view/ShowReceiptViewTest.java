package test.com.pyxis.petstore.view;

import com.pyxis.petstore.domain.product.Item;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import test.support.com.pyxis.petstore.views.ModelBuilder;
import test.support.com.pyxis.petstore.views.Routes;
import test.support.com.pyxis.petstore.views.VelocityRendering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.testinfected.hamcrest.dom.DomMatchers.hasAttribute;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSize;
import static org.testinfected.hamcrest.dom.DomMatchers.hasText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasUniqueSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.matches;
import static org.testinfected.hamcrest.dom.DomMatchers.matchesInAnyOrder;
import static test.support.com.pyxis.petstore.builders.AddressBuilder.anAddress;
import static test.support.com.pyxis.petstore.builders.CartBuilder.aCart;
import static test.support.com.pyxis.petstore.builders.CreditCardBuilder.aVisa;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.builders.OrderBuilder.anOrder;
import static test.support.com.pyxis.petstore.views.ModelBuilder.aModel;
import static test.support.com.pyxis.petstore.views.VelocityRendering.render;

public class ShowReceiptViewTest {

    Routes routes = Routes.to("/petstore");
    String SHOW_RECEIPT_VIEW_TEMPLATE = "receipts/show";
    Element showReceiptView;
    ModelBuilder model;
    String orderTotal = "458.97";

    @Before public void
    renderView() throws Exception {
        Item anItemOrderedSeveralTimes = anItem().
                withNumber("12345678").
                describedAs("Green Adult").
                priced("100.00").build();
        Item anItemOrderedOnce = anItem().priced("258.97").build();
        model = aModel().with(
                anOrder().
                        withNumber("00000100").
                        from(aCart().containing(
                                anItemOrderedSeveralTimes,
                                anItemOrderedSeveralTimes,
                                anItemOrderedOnce)).
                        paidWith(aVisa().
                                withNumber("9999 9999 9999").
                                withExpiryDate("12/12").
                                billedTo(anAddress().
                                        withFirstName("John").
                                        withLastName("Doe").
                                        withEmail("jdoe@gmail.com")
                                )
                        )
        );

        showReceiptView = renderShowReceiptView().using(model).asDom();
    }

    @Test public void
    displaysOrderSummary() {
        assertThat("view", showReceiptView, hasOrderNumber("00000100"));
        assertThat("view", showReceiptView, hasOrderTotal(orderTotal));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    setsUpOrderDetailsColumnHeadings() {
        assertThat("view", showReceiptView,
                hasSelector("#order-details th",
                        matches(hasText("Quantity"),
                                hasText("Item"),
                                hasText("Price"),
                                hasText("Total"))));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    displaysOrderLineItemsInColumns() {
        assertThat("view", showReceiptView, hasSelector("#order-details tr#line-item-12345678 td",
                matches(hasText("2"),
                        hasText(containsString("Green Adult")),
                        hasText("100.00"),
                        hasText("200.00"))));
    }

    @Test public void
    displaysOneOrderLineItemPerLine() {
        assertThat("view", showReceiptView, hasSelector("#order-details tr[id^='line-item']", hasSize(2)));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    displaysPaymentDetails() {
        assertThat("view", showReceiptView, hasSelector("#payment-details span", matchesInAnyOrder(
                hasText("Visa"),
                hasText("9999 9999 9999"),
                hasText("12/12"))));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    displaysBillingInformation() {
        assertThat("view", showReceiptView, hasSelector("#billing-address span", matchesInAnyOrder(
                hasText("John"),
                hasText("Doe"),
                hasText("jdoe@gmail.com"))));
    }

    @Test public void
    returnsToHomePageToContinueShopping() {
        assertThat("view", showReceiptView, hasUniqueSelector("a#continue-shopping", hasAttribute("href", routes.homePath())));
    }

    private Matcher<Element> hasOrderNumber(final String orderNumber) {
        return hasUniqueSelector("#order-number", hasText(orderNumber));
    }

    private Matcher<Element> hasOrderTotal(final String orderTotal) {
        return hasUniqueSelector("#order-total", hasText(orderTotal));
    }

    private VelocityRendering renderShowReceiptView() {
        return render(SHOW_RECEIPT_VIEW_TEMPLATE).using(routes);
    }
}