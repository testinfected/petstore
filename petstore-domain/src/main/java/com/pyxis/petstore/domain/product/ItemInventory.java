package com.pyxis.petstore.domain.product;

import java.util.List;

public interface ItemInventory {

	List<Item> findByProductNumber(String productNumber);

    Item find(ItemNumber itemNumber);

    void add(Item item);
}
