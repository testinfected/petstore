package test.com.pyxis.petstore.controller;

import com.pyxis.petstore.controller.ProductsController;
import com.pyxis.petstore.domain.product.AttachmentStorage;
import com.pyxis.petstore.domain.product.Product;
import com.pyxis.petstore.domain.product.ProductCatalog;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.testinfected.hamcrest.spring.SpringMatchers.containsAttribute;
import static org.testinfected.hamcrest.spring.SpringMatchers.hasAttribute;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;

@RunWith(JMock.class)
public class ProductsControllerTest {

    String ANY_PRODUCT = "a product";
    int CREATED = 201;

    Mockery context = new JUnit4Mockery();
    ProductCatalog productCatalog = context.mock(ProductCatalog.class);
    AttachmentStorage attachmentStorage = context.mock(AttachmentStorage.class);
    ProductsController productsController = new ProductsController(productCatalog, attachmentStorage);

    Model model = new ExtendedModelMap();

    @Before public void
    searchWillNotYieldAnyResult() {
        context.checking(new Expectations() {{
            allowing(productCatalog).findByKeyword(ANY_PRODUCT); will(returnValue(emptyList()));
        }});
    }

    @Test public void
    retrievesProductsMatchingKeywordAndMakesThemAvailableToView() {
        final Object matchingProducts = Arrays.asList(aProduct().build());
        context.checking(new Expectations() {{
            oneOf(productCatalog).findByKeyword("Dog"); will(returnValue(matchingProducts));
        }});

        productsController.index("Dog", model);
        assertThat("model", model, hasAttribute("productList", matchingProducts));
    }

	@Test public void
    doesNotAddProductListToModelIfNoMatchIsFound() {
        productsController.index(ANY_PRODUCT, model);
        assertThat("model", model, not(containsAttribute("productList")));
    }

    @Test public void
    makesStorageAvailableToView() {
        AttachmentStorage storage = productsController.getAttachmentStorage();
        assertThat("attachment storage", storage, sameInstance(attachmentStorage));
    }
    
    @Test public void
    makesSearchKeywordAvailableToView() {
        productsController.index(ANY_PRODUCT, model);
        assertThat("model", model, hasAttribute("keyword", ANY_PRODUCT));
    }

    @Test public void
    addsProductToCatalog() {
        final Product product = aProduct().build();
        context.checking(new Expectations() {{
            oneOf(productCatalog).add(with(same(product)));
        }});
        MockHttpServletResponse response = new MockHttpServletResponse();

        productsController.create(product, response);

        assertThat("status code", response.getStatus(), equalTo(CREATED));
    }

}
