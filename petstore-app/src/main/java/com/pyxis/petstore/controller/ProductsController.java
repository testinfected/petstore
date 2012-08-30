package com.pyxis.petstore.controller;

import com.pyxis.petstore.domain.product.AttachmentStorage;
import com.pyxis.petstore.domain.product.Product;
import com.pyxis.petstore.domain.product.ProductCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class ProductsController {

    private final ProductCatalog productCatalog;
    private final AttachmentStorage attachmentStorage;

    @Autowired
    public ProductsController(ProductCatalog productCatalog, AttachmentStorage attachmentStorage) {
        this.productCatalog = productCatalog;
        this.attachmentStorage = attachmentStorage;
    }

    @ModelAttribute("attachments")
    public AttachmentStorage getAttachmentStorage() {
        return attachmentStorage;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void index(@RequestParam("keyword") String keyword, Model model) {
        List<Product> matchingProducts = productCatalog.findByKeyword(keyword);
        model.addAttribute(matchingProducts);
        model.addAttribute("keyword", keyword);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void create(Product product, HttpServletResponse response) {
        productCatalog.add(product);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
}
