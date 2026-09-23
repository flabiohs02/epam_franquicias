package com.epam.franquicias.domain.model;

import com.epam.franquicias.domain.exception.DomainException;
import com.epam.franquicias.domain.exception.InvalidStockException;

import java.util.Objects;
import java.util.UUID;

public class Product {
    private String id;
    private String name;
    private int stock;

    public Product() {
    }

    public Product(String id, String name, int stock) {
        validateName(name);
        validateStock(stock);
        this.id = (id == null || id.isBlank()) ? UUID.randomUUID().toString() : id;
        this.name = name.trim();
        this.stock = stock;
    }

    public static Product create(String name, int stock) {
        return new Product(UUID.randomUUID().toString(), name, stock);
    }

    public void updateStock(int newStock) {
        validateStock(newStock);
        this.stock = newStock;
    }

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName.trim();
    }

    private void validateStock(int stock) {
        if (stock < 0) {
            throw new InvalidStockException("El stock de un producto no puede ser negativo: " + stock);
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("El nombre del producto no puede estar vacío");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                '}';
    }
}
