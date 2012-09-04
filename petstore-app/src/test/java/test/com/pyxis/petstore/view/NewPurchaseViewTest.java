package test.com.pyxis.petstore.view;

import com.pyxis.petstore.domain.billing.CreditCardDetails;
import com.pyxis.petstore.domain.billing.CreditCardType;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.w3c.dom.Element;
import test.support.com.pyxis.petstore.builders.AddressBuilder;
import test.support.com.pyxis.petstore.views.MockErrors;
import test.support.com.pyxis.petstore.views.ModelBuilder;
import test.support.com.pyxis.petstore.views.Routes;
import test.support.com.pyxis.petstore.views.VelocityRendering;

import java.util.ArrayList;
import java.util.List;

import static com.pyxis.petstore.domain.billing.CreditCardType.mastercard;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testinfected.hamcrest.dom.DomMatchers.anElement;
import static org.testinfected.hamcrest.dom.DomMatchers.hasAttribute;
import static org.testinfected.hamcrest.dom.DomMatchers.hasChild;
import static org.testinfected.hamcrest.dom.DomMatchers.hasName;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasUniqueSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.matchesInAnyOrder;
import static test.support.com.pyxis.petstore.builders.AddressBuilder.anAddress;
import static test.support.com.pyxis.petstore.builders.CartBuilder.aCart;
import static test.support.com.pyxis.petstore.builders.CreditCardBuilder.aMasterCard;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.views.MockErrors.errorsOn;
import static test.support.com.pyxis.petstore.views.ModelBuilder.aModel;
import static test.support.com.pyxis.petstore.views.VelocityRendering.render;

@SuppressWarnings("unchecked")
public class NewPurchaseViewTest {

    Routes routes = Routes.to("/petstore");
    String NEW_PURCHASE_VIEW_TEMPLATE = "purchases/new";
    Element newPurchaseView;
    ModelBuilder model;

    @Before public void
    renderView() throws Exception {
        model = aModel().
                with(aCart().containing(anItem().priced("100.00"))).
                and("cardTypes", CreditCardType.options());
        newPurchaseView = renderNewPurchaseView().using(model).asDom();
    }

    @Test public void
    displaysOrderSummary() {
        assertThat("view", newPurchaseView, hasUniqueSelector("#cart-grand-total", hasText("100.00")));
    }

    @Test public void
    displaysPurchaseForm() {
        assertThat("view", newPurchaseView, hasCheckoutForm(
                hasAttribute("action", routes.purchasesPath()),
                hasAttribute("method", "post")
        ));
        assertThat("view", newPurchaseView, hasCheckoutForm(hasEmptyBillingInformation()));
        assertThat("view", newPurchaseView, hasCheckoutForm(hasEmptyPaymentDetails()));
        assertThat("view", newPurchaseView, hasCheckoutForm(hasSubmitOrderButton()));
    }

    @Test public void
    fillsCardTypeSelectionList() {
        assertThat("view", newPurchaseView, hasSelector("#card-type option", hasCreditCardOptions()));
    }

    @Test public void
    rendersErrorsWhenPaymentDetailsAreInvalid() throws Exception {
        MockErrors errors = errorsOn("paymentDetails");
        errors.reject("invalid");
        errors.rejectValue("cardNumber", "empty");
        newPurchaseView = renderNewPurchaseView().using(model).bind(errors).asDom();

        assertThat("view", newPurchaseView, hasUniqueSelector("#payment-details-errors", hasChild(
                hasText("invalid.paymentDetails")
        )));
        assertThat("view", newPurchaseView, hasUniqueSelector("#card-number-errors", hasChild(
                hasText("empty.paymentDetails.cardNumber")
        )));
    }

    @Test public void
    restoresFormValuesWhenAValidationErrorOccurs() throws Exception {
        AddressBuilder billingAddress = anAddress().
                withName("Jack", "Johnson").withEmail("jack@gmail.com");
        CreditCardDetails creditCardDetails = aMasterCard().
                withNumber("1111 2222 3333 4444").
                withExpiryDate("2010-10-10").
                billedTo(billingAddress).build();
        newPurchaseView = renderNewPurchaseView().using(model.with("paymentDetails", creditCardDetails)).bind(validationErrorsOn("paymentDetails", creditCardDetails)).asDom();
        assertThat("view", newPurchaseView, hasCheckoutForm(
                hasBillingInformation("Jack", "Johnson", "jack@gmail.com"),
                hasCreditCardDetails(mastercard, "1111 2222 3333 4444", "2010-10-10")));
    }

    private Matcher<? super Element> hasCheckoutForm(Matcher<Element>... elementMatchers) {
        return hasUniqueSelector("form#order-form", elementMatchers);
    }

    private Matcher<Element> hasEmptyBillingInformation() {
        return hasBillingInformation("", "", "");
    }

    private Matcher<Element> hasBillingInformation(String firstName, String lastName, String email) {
        return hasInputFields(
                anElement(hasName("billingAddress.firstName"), hasAttribute("value", firstName)),
                anElement(hasName("billingAddress.lastName"), hasAttribute("value", lastName)),
                anElement(hasName("billingAddress.emailAddress"), hasAttribute("value", email)));
    }

    private Matcher<Element> hasInputFields(final Matcher<Element>... fieldMatchers) {
        return hasSelector("input[type='text']", fieldMatchers);
    }

    private Matcher<Element> hasEmptyPaymentDetails() {
        return anElement(
                hasSelectionOfCreditCardTypes(),
                hasEmptyCardNumberAndExpiryDate()
        );
    }

    private Matcher<Element> hasSelectionOfCreditCardTypes() {
        return hasSelectionList(hasName("cardType"));
    }

    private Matcher<Element> hasSelectionList(final Matcher<Element>... dropDownMatchers) {
        return hasUniqueSelector("select", dropDownMatchers);
    }

    private Matcher<Element> hasEmptyCardNumberAndExpiryDate() {
        return hasCardNumberAndExpiryDate("", "");
    }

    private Matcher<Element> hasCardNumberAndExpiryDate(String cardNumber, String cardExpiryDate) {
        return hasInputFields(
                anElement(hasName("cardNumber"), hasAttribute("value", cardNumber)),
                anElement(hasName("cardExpiryDate"), hasAttribute("value", cardExpiryDate)));
    }

    private Matcher<Element> hasSubmitOrderButton() {
        return hasUniqueSelector("#order");
    }

    private Matcher<Iterable<Element>> hasCreditCardOptions() {
        List<Matcher<? super Element>> matchers = new ArrayList<Matcher<? super Element>>();
        for (CreditCardType type : CreditCardType.values()) {
            matchers.add(hasOption(type.name(), type.commonName()));
        }
        return matchesInAnyOrder(matchers);
    }

    private Matcher<Element> hasOption(String value, String text) {
        return anElement(hasAttribute("value", value), hasText(text));
    }

    private Matcher<Element> hasCreditCardDetails(CreditCardType cardType, String cardNumber, String cardExpiryDate) {
        return anElement(
                hasSelectedCardType(cardType),
                hasCardNumberAndExpiryDate(cardNumber, cardExpiryDate));
    }

    private Matcher<Element> hasSelectedCardType(CreditCardType cardType) {
        return hasSelectionList(
                hasName("cardType"), hasChild(anElement(hasAttribute("value", cardType.toString()), hasAttribute("selected", "selected"))));
    }

    private BeanPropertyBindingResult validationErrorsOn(String objectName, CreditCardDetails creditCardDetails) {
        return new BeanPropertyBindingResult(creditCardDetails, objectName);
    }

    private VelocityRendering renderNewPurchaseView() {
        return render(NEW_PURCHASE_VIEW_TEMPLATE).using(routes);
    }
}
