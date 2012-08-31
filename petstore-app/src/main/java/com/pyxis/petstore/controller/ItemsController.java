package com.pyxis.petstore.controller;

import com.pyxis.petstore.Maybe;
import com.pyxis.petstore.domain.product.Item;
import com.pyxis.petstore.domain.product.ItemInventory;
import com.pyxis.petstore.domain.product.Product;
import com.pyxis.petstore.domain.product.ProductCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.util.List;

@Controller
public class ItemsController {

    private final ProductCatalog productCatalog;
    private final ItemInventory itemInventory;

    @Autowired
    public ItemsController(ProductCatalog productCatalog, ItemInventory itemInventory) {
        this.productCatalog = productCatalog;
        this.itemInventory = itemInventory;
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        binder.registerCustomEditor(Product.class, "product", new PropertyEditorSupport() {
            public void setAsText(String text) throws IllegalArgumentException {
                Maybe<Product> product = productCatalog.findByNumber(text);
                if (product.exists()) setValue(product.bare());
            }
        });
    }

    @RequestMapping(value = "/products/{productNumber}/items", method = RequestMethod.GET)
    public String index(@PathVariable("productNumber") String productNumber, Model model) {
        List<Item> items = itemInventory.findByProductNumber(productNumber);
        model.addAttribute(items);
        return "items";
    }

    @RequestMapping(value = "/items", method = RequestMethod.POST)
    public void create(@Valid Item item, HttpServletResponse response) {
        itemInventory.add(item);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
}
