package test.com.pyxis.petstore.domain.product;

import com.pyxis.petstore.domain.product.Product;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import test.support.com.pyxis.petstore.builders.ProductBuilder;

import static org.testinfected.hamcrest.validation.ViolationMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;
import static test.support.com.pyxis.petstore.validation.ValidationOf.validationOf;

public class ProductTest {

    String SHOULD_NOT_BE_NULL = "{javax.validation.constraints.NotNull.message}";

    @SuppressWarnings("unchecked")
    @Test public void
    isInvalidWithoutAName() {
        assertThat("constraint violations", validationOf(aProductWithoutAName()), violates(on("name"), withError(SHOULD_NOT_BE_NULL)));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    isInvalidWithoutANumber() {
        assertThat("constraint violations", validationOf(aProductWithoutANumber()), violates(on("number"), withError(SHOULD_NOT_BE_NULL)));
    }

    @Test public void
    isValidWithANameAndANumber() {
        assertThat("constraint violations", validationOf(aValidProduct()), succeeds());
    }

    @Test public void
    hasADefaultPhoto() {
        assertThat("default photo", aProductWithoutAPhoto(), productWithPhoto("missing.png"));
    }

    @Test public void
    productIsUniquelyIdentifiedByItsNumber() {
        Product product = aProduct().withNumber("AAA-123").build();
        Product shouldMatch = aProduct().withNumber("AAA-123").build();
        Product shouldNotMatch = aProduct().withNumber("BBB-456").build();
        assertThat("product", product, equalTo(shouldMatch));
        assertThat("hash code", product.hashCode(), equalTo(shouldMatch.hashCode()));
        assertThat("product", product, not(equalTo(shouldNotMatch)));
    }

    private ProductBuilder aProductWithoutAName() {
        return aProduct().named(null);
    }

    private ProductBuilder aProductWithoutANumber() {
        return aProduct().withNumber(null);
    }

    private ProductBuilder aValidProduct() {
        return aProduct();
    }

    private Product aProductWithoutAPhoto() {
        return aProduct().withoutAPhoto().build();
    }

    private Matcher<? super Product> productWithPhoto(String fileName) {
        return new FeatureMatcher<Product, String>(equalTo(fileName), "a product with photo", "photo") {
            protected String featureValueOf(Product actual) {
                return actual.getPhotoFileName();
            }
        };
    }
}
