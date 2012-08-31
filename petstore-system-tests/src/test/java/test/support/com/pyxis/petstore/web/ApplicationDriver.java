package test.support.com.pyxis.petstore.web;

import com.objogate.wl.web.AsyncWebDriver;
import test.support.com.pyxis.petstore.web.page.*;

import java.io.IOException;
import java.math.BigDecimal;

public class ApplicationDriver {

    private final TestEnvironment environment;

    private AdministrationDriver admin;
    private AsyncWebDriver browser;
    private HomePage homePage;
    private ProductsPage productsPage;
    private ItemsPage itemsPage;
    private CartPage cartPage;
    private PurchasePage purchasePage;
    private ReceiptPage receiptPage;
    private Menu menu;

    public ApplicationDriver(TestEnvironment environment) {
        this.environment = environment;
    }

    public void start() throws Exception {
        startWebServer();
        startBrowser();
        makeDrivers();
    }

    private void startWebServer() {
        environment.startServer();
    }

    private void startBrowser() throws Exception {
        this.browser = environment.startBrowser();
    }

    private void makeDrivers() {
        menu = new Menu(browser);
        homePage = new HomePage(browser);
        productsPage = new ProductsPage(browser);
        itemsPage = new ItemsPage(browser);
        cartPage = new CartPage(browser);
        purchasePage = new PurchasePage(browser);
        receiptPage = new ReceiptPage(browser);
        admin = new AdministrationDriver(environment.makeWebClient(), environment.routes());
    }

    public void stop() {
        logout();
        stopServer();
        stopBrowser();
        cleanupEnvironment();
    }

    private void cleanupEnvironment() {
        environment.wipe();
    }

    private void stopBrowser() {
        browser.quit();
    }

    private void stopServer() {
        environment.stopServer();
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

    public void addProduct(String number, String name) throws IOException {
        addProduct(number, name, "");
    }

    public void addProduct(String number, String name, String description) throws IOException {
        admin.addProduct(number, name, description);
    }

    public void addItem(String productNumber, String itemNumber, String description, String price) throws IOException {
        admin.addItem(productNumber, itemNumber, description, price);
    }
}
