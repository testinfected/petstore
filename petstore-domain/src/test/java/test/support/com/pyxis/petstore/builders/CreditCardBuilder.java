package test.support.com.pyxis.petstore.builders;

import com.pyxis.petstore.domain.billing.Address;
import com.pyxis.petstore.domain.billing.CreditCardDetails;
import com.pyxis.petstore.domain.billing.CreditCardType;

import static test.support.com.pyxis.petstore.builders.AddressBuilder.aValidAddress;
import static test.support.com.pyxis.petstore.builders.AddressBuilder.anAddress;

public class CreditCardBuilder implements Builder<CreditCardDetails> {

    private CreditCardType cardType;
    private String cardNumber;
    private String cardExpiryDate;
    private Address billingAddress = anAddress().build();

    public static CreditCardBuilder aVisa() {
        return aCreditCard().ofType(CreditCardType.visa);
    }

    public static CreditCardBuilder aMasterCard() {
        return aCreditCard().ofType(CreditCardType.mastercard);
    }

    public static CreditCardBuilder aCreditCard() {
        return new CreditCardBuilder();
    }

    public static CreditCardBuilder validVisaDetails() {
        return aVisa().
                withNumber("4111111111111111").
                withExpiryDate("12/12").
                billedTo(aValidAddress());
    }

    public CreditCardDetails build() {
        return new CreditCardDetails(cardType, cardNumber, cardExpiryDate, billingAddress);
    }

    public CreditCardBuilder ofType(CreditCardType type) {
        this.cardType = type;
        return this;
    }

    public CreditCardBuilder withNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public CreditCardBuilder withExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
        return this;
    }

    public CreditCardBuilder billedTo(AddressBuilder addressBuilder) {
        this.billingAddress = addressBuilder.build();
        return this;
    }
}
