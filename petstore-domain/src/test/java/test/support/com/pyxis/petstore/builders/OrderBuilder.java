package test.support.com.pyxis.petstore.builders;

import com.pyxis.petstore.domain.billing.PaymentMethod;
import com.pyxis.petstore.domain.order.Cart;
import com.pyxis.petstore.domain.order.Order;
import com.pyxis.petstore.domain.order.OrderNumber;

import static test.support.com.pyxis.petstore.builders.CartBuilder.aCart;
import static test.support.com.pyxis.petstore.builders.OrderNumberFaker.aNumber;

public class OrderBuilder implements Builder<Order> {

    private OrderNumber orderNumber = aNumber();
    private Cart cart = aCart().build();
    private PaymentMethod paymentMethod;

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    public OrderBuilder from(CartBuilder cart) {
        return from(cart.build());
    }

    public OrderBuilder from(Cart cart) {
        this.cart = cart;
        return this;
    }

    public Order build() {
        Order order = new Order(orderNumber);
        order.addItemsFrom(cart);
        order.pay(paymentMethod);
        return order;
    }

    public OrderBuilder withNumber(String orderNumber) {
        return with(new OrderNumber(orderNumber));
    }

    public OrderBuilder with(final OrderNumber orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public OrderBuilder paidWith(Builder<? extends PaymentMethod> paymentMethodBuilder) {
        this.paymentMethod = paymentMethodBuilder.build();
        return this;
    }
}