package com.pyxis.petstore.domain.product;

import com.pyxis.petstore.Maybe;

import java.util.List;

public interface ProductCatalog {

    Maybe<Product> findByNumber(String number);

	List<Product> findByKeyword(String keyword);

	void add(Product product);
}
