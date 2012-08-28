package test.support.com.pyxis.petstore.web;

import com.objogate.wl.UnsynchronizedProber;
import com.objogate.wl.web.AsyncWebDriver;
import com.pyxis.petstore.domain.product.Product;
import org.hibernate.SessionFactory;
import org.openqa.selenium.WebDriver;
import test.support.com.pyxis.petstore.builders.ItemBuilder;
import test.support.com.pyxis.petstore.builders.ProductBuilder;
import test.support.com.pyxis.petstore.web.browser.BrowserControl;
import test.support.com.pyxis.petstore.web.page.*;
import test.support.com.pyxis.petstore.web.server.ServerLifeCycle;

import java.math.BigDecimal;

public class ApplicationDriver {

    private final ServerLifeCycle server;
    private final BrowserControl browserControl;
    private final DatabaseDriver database;
    private final Routing routes;

    private WebDriver webDriver;
    private HomePage homePage;
    private ProductsPage productsPage;
    private ItemsPage itemsPage;
    private CartPage cartPage;
    private PurchasePage purchasePage;
    private ReceiptPage receiptPage;
    private Menu menu;

    public ApplicationDriver(TestEnvironment environment) {
        this.server = environment.serverLifeCycle;
        this.database = new DatabaseDriver(environment.getComponent(SessionFactory.class));
        this.browserControl = environment.browserControl;
        this.routes = environment.routes;
    }

    public void start() {
        startDatabase();
        startWebServer();
        startBrowser();
        openHomePage();
    }

    private void startDatabase() {
        database.start();
    }

    private void startWebServer() {
        server.start();
    }

    private void startBrowser() {
        this.webDriver = browserControl.launch();
        AsyncWebDriver browser = new AsyncWebDriver(new UnsynchronizedProber(), webDriver);
        menu = new Menu(browser);
        homePage = new HomePage(browser);
        productsPage = new ProductsPage(browser);
        itemsPage = new ItemsPage(browser);
        cartPage = new CartPage(browser);
        purchasePage = new PurchasePage(browser);
        receiptPage = new ReceiptPage(browser);
    }

    public void openHomePage() {
        webDriver.navigate().to(routes.urlFor(HomePage.class));
    }

    public void stop() {
        logout();
        stopWebServer();
        stopBrowser();
        stopDatabase();
    }

    private void stopDatabase() {
        database.stop();
    }

    private void stopBrowser() {
        webDriver.close();
    }

    private void stopWebServer() {
        server.stop();
    }

    public void logout() {
        menu.logout();
        homePage.displays();
    }

    public void searchFor(String keyword) {
        menu.search(keyword);
        productsPage.displays();
    }

    public void showsNoResult() {
        productsPage.showsNoResult();
    }

    public void displaysNumberOfResults(int matchCount) {
        productsPage.displaysNumberOfResults(matchCount);
    }

    public void displaysProduct(String number, String name) {
        productsPage.displaysProduct(number, name);
    }

    public void consultInventoryOf(String product) {
        searchFor(product);
        browseInventory(product);
    }

    public void browseInventory(String product) {
        productsPage.browseItemsOf(product);
        itemsPage.displays();
    }

    public void showsNoItemAvailable() {
        itemsPage.showsNoItemAvailable();
    }

    public void displaysItem(String number, String description, String price) {
        itemsPage.displaysItem(number, description, price);
    }

    public void buy(String product, String itemNumber) {
        consultInventoryOf(product);
        buy(itemNumber);
    }

    public void buy(String itemNumber) {
        itemsPage.addToCart(itemNumber);
        cartPage.displays();
    }

    public void checkout() {
        cartPage.checkout();
        purchasePage.displays();
    }

    public void showsCartIsEmpty() {
        menu.showsCartIsEmpty();
    }

    public void showsCartTotalQuantity(int quantity) {
        menu.showsCartTotalQuantity(quantity);
    }

    public void showsItemInCart(String itemNumber, String itemDescription, String totalPrice) {
        cartPage.showsItemInCart(itemNumber, itemDescription, totalPrice);
    }

    public void showsItemQuantity(String itemNumber, int quantity) {
        cartPage.showsItemQuantity(itemNumber, quantity);
    }

    public void showsGrandTotal(String price) {
        cartPage.showsGrandTotal(price);
    }

    public void showsTotalToPay(String total) {
        purchasePage.showsTotalToPay(new BigDecimal(total));
    }

    public void pay(String firstName, String lastName, String email, String cardType, String cardNumber, String cardExpiryDate) {
        purchasePage.willBillTo(firstName, lastName, email);
        purchasePage.willPayUsingCreditCard(cardType, cardNumber, cardExpiryDate);
        purchasePage.confirmOrder();
        receiptPage.displays();
    }

    public void showsTotalPaid(String total) {
        receiptPage.showsTotalPaid(new BigDecimal(total));
    }

    public void showsLineItem(String itemNumber, String itemDescription, String totalPrice) {
        receiptPage.showsLineItem(itemNumber, itemDescription, totalPrice);
    }

    public void showsCreditCardDetails(String cardType, String cardNumber, String cardExpiryDate) {
        receiptPage.showsCreditCardDetails(cardType, cardNumber, cardExpiryDate);
    }

    public void showsBillingInformation(String firstName, String lastName, String emailAddress) {
        receiptPage.showsBillingInformation(firstName, lastName, emailAddress);
    }

    public void returnHome() {
        menu.home();
        homePage.displays();
    }

    public void continueShopping() {
        cartPage.continueShopping();
        homePage.displays();
    }

    public void reviewCart() {
        menu.cart();
        cartPage.displays();
    }

    public void addProducts(ProductBuilder... products) {
        database.add(products);
    }

    public void addProducts(Product... products) {
        database.add(products);
    }

    public void addItems(ItemBuilder... items) {
        database.add(items);
    }
}
