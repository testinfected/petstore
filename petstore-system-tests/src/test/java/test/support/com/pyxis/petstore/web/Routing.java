package test.support.com.pyxis.petstore.web;

import java.net.MalformedURLException;
import java.net.URL;

public final class Routing {

    private final String baseUrl;

    public Routing(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public URL toHome() throws MalformedURLException {
        return urlFor("/");
    }

    public URL toProducts() throws MalformedURLException {
        return urlFor("/products");
    }

    public URL toItems() throws MalformedURLException {
        return urlFor("/items");
    }

    public URL urlFor(String path) throws MalformedURLException {
        return new URL(baseUrl + path);
    }
}
