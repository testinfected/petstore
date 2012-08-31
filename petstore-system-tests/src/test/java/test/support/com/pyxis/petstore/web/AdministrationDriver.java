package test.support.com.pyxis.petstore.web;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AdministrationDriver {

    public static final int CREATED = 201;

    private final WebClient client;
    private final Routing routes;

    public AdministrationDriver(WebClient client, Routing routes) {
        this.routes = routes;
        this.client = client;
    }

    public void addProduct(String number, String name) throws IOException {
        WebRequest request = new WebRequest(routes.toProducts(), HttpMethod.POST);

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new NameValuePair("number", number));
        requestParameters.add(new NameValuePair("name", name));
        request.setRequestParameters(requestParameters);

        WebResponse response = client.loadWebResponse(request);
        assertThat("http status code", response.getStatusCode(), equalTo(CREATED));
    }

    public void addItem(String productNumber, String itemNumber, String description, String price) throws IOException {
        WebRequest request = new WebRequest(routes.toItems(), HttpMethod.POST);

        List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
        requestParameters.add(new NameValuePair("product", productNumber));
        requestParameters.add(new NameValuePair("number", itemNumber));
        requestParameters.add(new NameValuePair("price",price));
        requestParameters.add(new NameValuePair("description", description));
        request.setRequestParameters(requestParameters);

        WebResponse response = client.loadWebResponse(request);
        assertThat("http status code", response.getStatusCode(), equalTo(CREATED));
    }

}
