package com.epam.franquicias.domain.model;

import com.epam.franquicias.domain.exception.DomainException;
import com.epam.franquicias.domain.exception.DuplicateEntityException;
import com.epam.franquicias.domain.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Branch {
    private String id;
    private String name;
    private List<Product> products = new ArrayList<>();

    public Branch() {
    }

    public Branch(String id, String name, List<Product> products) {
        validateName(name);
        this.id = (id == null || id.isBlank()) ? UUID.randomUUID().toString() : id;
        this.name = name.trim();
        if (products != null) {
            this.products = new ArrayList<>(products);
        }
    }

    public static Branch create(String name) {
        return new Branch(UUID.randomUUID().toString(), name, new ArrayList<>());
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new DomainException("El producto no puede ser nulo");
        }
        boolean existsByName = products.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()));
        if (existsByName) {
            throw new DuplicateEntityException("Ya existe un producto con el nombre '" + product.getName() + "' en la sucursal");
        }
        boolean existsById = products.stream()
                .anyMatch(p -> p.getId().equals(product.getId()));
        if (existsById) {
            throw new DuplicateEntityException("Ya existe un producto con el ID '" + product.getId() + "' en la sucursal");
        }
        products.add(product);
    }

    public void removeProduct(String productId) {
        Product product = getProductOrThrow(productId);
        products.remove(product);
    }

    public Product getProductOrThrow(String productId) {
        return findProductById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Producto con ID '" + productId + "' no encontrado en la sucursal"));
    }

    public Optional<Product> findProductById(String productId) {
        if (productId == null) return Optional.empty();
        return products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();
    }

    public void updateProductStock(String productId, int newStock) {
        Product product = getProductOrThrow(productId);
        product.updateStock(newStock);
    }

    public void updateProductName(String productId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new DomainException("El nuevo nombre del producto no puede estar vacío");
        }
        boolean duplicate = products.stream()
                .anyMatch(p -> !p.getId().equals(productId) && p.getName().equalsIgnoreCase(newName.trim()));
        if (duplicate) {
            throw new DuplicateEntityException("Ya existe otro producto con el nombre '" + newName + "' en la sucursal");
        }
        Product product = getProductOrThrow(productId);
        product.updateName(newName);
    }

    public Optional<Product> findTopStockProduct() {
        if (products == null || products.isEmpty()) {
            return Optional.empty();
        }
        return products.stream()
                .max(Comparator.comparingInt(Product::getStock));
    }

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName.trim();
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("El nombre de la sucursal no puede estar vacío");
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

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return Objects.equals(id, branch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Branch{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", products=" + products.size() +
                '}';
    }
}
