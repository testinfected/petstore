package test.support.com.pyxis.petstore.web;

import test.support.com.pyxis.petstore.web.server.ServerProperties;

import java.net.URL;

public final class Routing {

    private final ServerProperties server;

    public Routing(ServerProperties server) {
        this.server = server;
    }

    public URL toHome() {
        return server.urlFor("/");
    }

    public URL toProducts() {
        return server.urlFor("/products");
    }

    public URL toItems() {
        return server.urlFor("/items");
    }
}
