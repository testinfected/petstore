package com.pyxis.petstore.domain.product;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity @Access(AccessType.FIELD) @Table(name = "items")
public class Item implements Serializable {

	@SuppressWarnings("unused")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private @Id Long id;

    private @NotNull @Valid ItemNumber number;

    @ManyToOne @JoinColumn(name = "product_id")
	private @NotNull Product product;
    private @NotNull BigDecimal price;

    private String description;

    Item() {}

    public Item(ItemNumber number, Product product, BigDecimal price) {
        this.number = number;
        this.product = product;
        this.price = price;
    }

    public void setNumber(String number) {
        this.number = new ItemNumber(number);
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getNumber() {
        return number.getNumber();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public String getProductNumber() {
        return product.getNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (number != null ? !number.equals(item.number) : item.number != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return number != null ? number.hashCode() : 0;
    }

    public String toString() {
        return number + " (" + product.getNumber() + ")";
    }
}
