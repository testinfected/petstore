package test.integration.com.pyxis.petstore.persistence;

import com.pyxis.petstore.Maybe;
import com.pyxis.petstore.domain.order.CartItem;
import com.pyxis.petstore.domain.order.LineItem;
import com.pyxis.petstore.domain.order.Order;
import com.pyxis.petstore.domain.order.OrderBook;
import com.pyxis.petstore.domain.order.OrderNumber;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.builders.OrderBuilder;
import test.support.com.pyxis.petstore.db.Database;
import test.support.com.pyxis.petstore.db.DatabaseCleaner;
import test.support.com.pyxis.petstore.db.IntegrationTestContext;
import test.support.com.pyxis.petstore.db.UnitOfWork;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.testinfected.hamcrest.jpa.PersistenceMatchers.samePersistentFieldsAs;
import static test.support.com.pyxis.petstore.builders.CartBuilder.aCart;
import static test.support.com.pyxis.petstore.builders.CreditCardBuilder.validVisaDetails;
import static test.support.com.pyxis.petstore.builders.ItemBuilder.anItem;
import static test.support.com.pyxis.petstore.builders.OrderBuilder.anOrder;
import static test.support.com.pyxis.petstore.db.Database.idOf;
import static test.support.com.pyxis.petstore.db.IntegrationTestContext.integrationTesting;

public class PersistentOrderBookTest {

    IntegrationTestContext context = integrationTesting();

    Database database = new Database(context.openConnection());
    OrderBook orderBook = context.getComponent(OrderBook.class);

    @Before public void
    cleanDatabase() {
        new DatabaseCleaner(database).clean();
    }

    @After public void
    closeDatabase() {
        database.close();
    }

    @Test public void
    orderNumberShouldBeUnique() {
        OrderBuilder order = anOrder().withNumber("00000100");
        database.given(order);

        assertViolatesUniqueness(order.build());
    }

    @Test public void
    findsOrdersByNumber() {
        database.given(anOrder().withNumber("00000100"));

        Maybe<Order> entry = orderBook.find(new OrderNumber("00000100"));
        assertThat("no match", entry.exists());
        assertThat("match", entry.bare(), orderWithNumber("00000100"));
    }

    @Test(expected = ConstraintViolationException.class) public void
    lineItemsCannotBePersistedInIsolation() {
        LineItem shouldFail = LineItem.from(new CartItem(anItem().build()));
        database.persist(shouldFail);
    }

    @Test public void
    canRoundTripOrders() {
        OrderBuilder aPendingOrder = anOrder();
        assertCanPersistAndReload("pending order", aPendingOrder.build());

        OrderBuilder aPaidOrder = anOrder().paidWith(validVisaDetails());
        assertCanPersistAndReload("paid order", aPaidOrder.build());
    }

    private void assertCanPersistAndReload(String orderName, Order order) {
        database.persist(order);
        database.assertCanBeReloadedWithSameState(orderName, order);
        if (order.isPaid()) database.assertCanBeReloadedWithSameState(order.getPaymentMethod());
    }

    @Test public void
    lineItemsAreLoggedWithOrderInCorrectOrder() {
        final Order order = anOrder().from(aCart().containing(
                anItem().withNumber("00000100").priced("100.00"),
                anItem().withNumber("00000100").priced("100.00"),
                anItem().withNumber("00000111").describedAs("White lizard"))).build();
        database.persist(order);

        database.perform(new UnitOfWork() {
            public void work(Session session) {
                Order reloaded = (Order) session.get(Order.class, idOf(order));
                assertThat("loaded", reloaded, sameItemCountAs(order));
                assertThat("loaded line items", reloaded.getLineItems(), contains(linesWithSameStateAs(order)));
            }
        });
    }

    private Matcher<? super Order> sameItemCountAs(Order order) {
        return new FeatureMatcher<Order, Integer>(equalTo(order.getLineItemCount()), "an order with line item count", "line item count") {
            @Override protected Integer featureValueOf(Order actual) {
                return actual.getLineItemCount();
            }
        };
    }

    private List<Matcher<? super LineItem>> linesWithSameStateAs(Order order) {
        List<Matcher<? super LineItem>> all = new ArrayList<Matcher<? super LineItem>>();
        for (LineItem lineItem : order.getLineItems()) {
            all.add(samePersistentFieldsAs(lineItem));
        }
        return all;
    }

    private Matcher<? super Order> orderWithNumber(String orderNumber) {
        return new FeatureMatcher<Order, String>(equalTo(orderNumber), "an order with number", "order number") {
            @Override protected String featureValueOf(Order order) {
                return order.getNumber();
            }
        };
    }

    private void assertViolatesUniqueness(Order order) {
        try {
            orderBook.record(order);
            fail("No constraint violation");
        } catch (ConstraintViolationException expected) {
        }
    }
}