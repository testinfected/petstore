package test.integration.com.pyxis.petstore.persistence;

import com.pyxis.petstore.domain.order.OrderNumber;
import com.pyxis.petstore.domain.order.OrderNumberSequence;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.support.com.pyxis.petstore.db.Database;
import test.support.com.pyxis.petstore.db.TestEnvironment;
import test.support.com.pyxis.petstore.db.UnitOfWork;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PersistentOrderNumberSequenceTest {

    TestEnvironment environment = TestEnvironment.load();

    Database database = Database.in(environment);
    OrderNumberSequence orderNumberSequence = environment.get(OrderNumberSequence.class);

    @Before public void
    cleanDatabase() {
        database.clean();
    }

    @After public void
    closeDatabase() {
        database.close();
    }

    @Test public void
    incrementsSequenceNumber() throws Exception {
        seedSequenceNumberWith(100);

        assertThat("next order number", nextOrderNumber(), orderNumber("00000101"));
        assertThat("next order number", nextOrderNumber(), orderNumber("00000102"));
    }

    private void seedSequenceNumberWith(final long seed) throws Exception {
        database.perform(new UnitOfWork() {
            public void work(Session session) {
                SQLQuery query = session.createSQLQuery("insert into order_number_sequence values (:seed)");
                query.setLong("seed", seed);
                assertUpdateExecutes(query);
            }
        });
    }

    private void assertUpdateExecutes(SQLQuery query) {
        assertThat("update count", query.executeUpdate(), is(1));
    }

    private OrderNumber nextOrderNumber() {
        return orderNumberSequence.nextOrderNumber();
    }

    private Matcher<OrderNumber> orderNumber(final String number) {
        return new FeatureMatcher<OrderNumber, String>(equalTo(number), "an order number", "order number") {
            @Override protected String featureValueOf(OrderNumber actual) {
                return actual.getNumber();
            }
        };
    }
}