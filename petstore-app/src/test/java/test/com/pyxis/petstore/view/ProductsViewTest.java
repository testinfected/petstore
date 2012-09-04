package test.com.pyxis.petstore.view;

import com.pyxis.petstore.domain.product.AttachmentStorage;
import com.pyxis.petstore.domain.product.Product;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;
import test.support.com.pyxis.petstore.views.ModelBuilder;
import test.support.com.pyxis.petstore.views.Routes;
import test.support.com.pyxis.petstore.views.VelocityRendering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.nullValue;
import static org.testinfected.hamcrest.dom.DomMatchers.hasAttribute;
import static org.testinfected.hamcrest.dom.DomMatchers.hasBlankText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasChild;
import static org.testinfected.hamcrest.dom.DomMatchers.hasNoSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSelector;
import static org.testinfected.hamcrest.dom.DomMatchers.hasSize;
import static org.testinfected.hamcrest.dom.DomMatchers.hasText;
import static org.testinfected.hamcrest.dom.DomMatchers.hasUniqueSelector;
import static test.support.com.pyxis.petstore.builders.ProductBuilder.aProduct;
import static test.support.com.pyxis.petstore.views.VelocityRendering.render;

@RunWith(JMock.class)
public class ProductsViewTest {
    Routes routes = Routes.toPetstore();
    String PRODUCTS_VIEW_TEMPLATE = "products";
    Object DEFAULT_PHOTO_URL = "url/of/" + Product.MISSING_PHOTO;
    String keyword = "Iguana";

    Mockery context = new JUnit4Mockery();
    AttachmentStorage attachmentStorage = context.mock(AttachmentStorage.class);
    ModelBuilder model = new ModelBuilder();
    Element productsView;

    @Before public void
    setupModel() {
        model.with("keyword", keyword);
        model.with("attachments", attachmentStorage);
    }

    @Before public void
    setUpDefaultPhoto() {
        context.checking(new Expectations() {{
            allowing(attachmentStorage).getLocation(Product.MISSING_PHOTO); will(returnValue(DEFAULT_PHOTO_URL));
        }});
    }

    @Test public void
    displaysAllProductsFound() {
        productsView = renderProductsView().using(model.listing(aProduct(), aProduct())).asDom();
        assertThat("view", productsView, hasUniqueSelector("#match-count", hasText("2")));
        assertThat("view", productsView, hasSelector("#catalog li[id^='product']", hasSize(2)));
    }

    @Test public void
    displaysProductDetails() throws Exception {
        model.listing(aProduct().
                withNumber("LAB-1234").
                named("Labrador").
                describedAs("Friendly").
                withPhoto("labrador.png"));
        final String photoUrl = "/path/to/attachment/labrador.png";
        context.checking(new Expectations() {{
            allowing(attachmentStorage).getLocation(with("labrador.png")); will(returnValue(photoUrl));
        }});

        productsView = renderProductsView().using(model).asDom();
        assertThat("view", productsView,
                hasSelector(".product-link", hasImage(routes.pathFor(photoUrl))));
        assertThat("view", productsView,
                hasSelector(".product-name", hasText("Labrador")));
        assertThat("view", productsView,
                hasSelector(".product-description", hasText("Friendly")));
    }

    @Test public void
    handlesProductWithNoDescription() {
        productsView = renderProductsView().using(model.listing(aProduct().withNoDescription())).asDom();
        assertThat("view", productsView,
                hasSelector(".product-description", hasBlankText()));
    }

    @Test public void
    doesNotDisplayProductListWhenNoProductIsFound() {
        productsView = renderProductsView().using(model).asDom();
        assertThat("view", productsView, hasUniqueSelector("#no-match"));
        assertThat("view", productsView, hasNoSelector("#catalog li"));
    }

    @Test public void
    productNameAndPhotoLinkToProductInventory() {
        productsView = renderProductsView().using(model.listing(aProduct().named("Labrador").withNumber("LAB-1234"))).asDom();
        assertThat("view", productsView,
                hasSelector("li a", everyItem(
                        hasAttribute("href", equalTo(routes.itemsPath("LAB-1234"))))));
    }

    private Matcher<Product> aProductWithPhoto(Matcher<? super String> photoMatcher) {
        return new FeatureMatcher<Product, String>(photoMatcher, "a product with photo filename", "photo filename") {
            @Override protected String featureValueOf(Product actual) {
                return actual.getPhotoFileName();
            }
        };
    }

    private Matcher<Element> hasImage(String imageUrl) {
        return hasChild(hasAttribute("src", equalTo(imageUrl)));
    }

    private VelocityRendering renderProductsView() {
        return render(PRODUCTS_VIEW_TEMPLATE).using(routes);
    }
}
