package test.support.com.pyxis.petstore.views;

public final class Routes {

    public static Routes root() {
        return to("");
    }

    public static Routes to(String contextPath) {
        return new Routes(contextPath);
    }

    private final String contextPath;

    public Routes(String contextPath) {
        this.contextPath = contextPath;
    }

    public String contextPath() {
        return contextPath;
    }

    public String pathFor(String relativePath) {
        return contextPath + relativePath;
    }

    public String homePath() {
        return pathFor("/");
    }

    public String itemsPath(String productNumber) {
        return pathFor("/products/" + productNumber + "/items");
    }

    public String cartItemsPath() {
        return pathFor("/cartitems");
    }

    public String cartPath() {
        return pathFor("/cart");
    }

    public String checkoutPath() {
        return pathFor("/checkout");
    }

    public String purchasesPath() {
        return pathFor("/purchases");
    }

    public String logoutPath() {
        return pathFor("/logout");
    }

    public String productsPath() {
        return pathFor("/products");
    }
}
