package com.epam.franquicias.domain.model;

import com.epam.franquicias.domain.exception.DomainException;
import com.epam.franquicias.domain.exception.DuplicateEntityException;
import com.epam.franquicias.domain.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Franchise {
    private String id;
    private String name;
    private List<Branch> branches = new ArrayList<>();

    public Franchise() {
    }

    public Franchise(String id, String name, List<Branch> branches) {
        validateName(name);
        this.id = (id == null || id.isBlank()) ? UUID.randomUUID().toString() : id;
        this.name = name.trim();
        if (branches != null) {
            this.branches = new ArrayList<>(branches);
        }
    }

    public static Franchise create(String name) {
        return new Franchise(UUID.randomUUID().toString(), name, new ArrayList<>());
    }

    public void addBranch(Branch branch) {
        if (branch == null) {
            throw new DomainException("La sucursal no puede ser nula");
        }
        boolean existsByName = branches.stream()
                .anyMatch(b -> b.getName().equalsIgnoreCase(branch.getName()));
        if (existsByName) {
            throw new DuplicateEntityException("Ya existe una sucursal con el nombre '" + branch.getName() + "' en la franquicia");
        }
        boolean existsById = branches.stream()
                .anyMatch(b -> b.getId().equals(branch.getId()));
        if (existsById) {
            throw new DuplicateEntityException("Ya existe una sucursal con el ID '" + branch.getId() + "' en la franquicia");
        }
        branches.add(branch);
    }

    public Branch getBranchOrThrow(String branchId) {
        return findBranchById(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal con ID '" + branchId + "' no encontrada en la franquicia"));
    }

    public Optional<Branch> findBranchById(String branchId) {
        if (branchId == null) return Optional.empty();
        return branches.stream()
                .filter(b -> b.getId().equals(branchId))
                .findFirst();
    }

    public void addProductToBranch(String branchId, Product product) {
        Branch branch = getBranchOrThrow(branchId);
        branch.addProduct(product);
    }

    public void removeProductFromBranch(String branchId, String productId) {
        Branch branch = getBranchOrThrow(branchId);
        branch.removeProduct(productId);
    }

    public void updateProductStock(String branchId, String productId, int newStock) {
        Branch branch = getBranchOrThrow(branchId);
        branch.updateProductStock(productId, newStock);
    }

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName.trim();
    }

    public void updateBranchName(String branchId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new DomainException("El nuevo nombre de la sucursal no puede estar vacío");
        }
        boolean duplicate = branches.stream()
                .anyMatch(b -> !b.getId().equals(branchId) && b.getName().equalsIgnoreCase(newName.trim()));
        if (duplicate) {
            throw new DuplicateEntityException("Ya existe otra sucursal con el nombre '" + newName + "' en la franquicia");
        }
        Branch branch = getBranchOrThrow(branchId);
        branch.updateName(newName);
    }

    public void updateProductName(String branchId, String productId, String newName) {
        Branch branch = getBranchOrThrow(branchId);
        branch.updateProductName(productId, newName);
    }

    /**
     * Requerimiento 7: Obtener el producto con mayor stock por cada sucursal para esta franquicia.
     * Retorna un listado estructurado de cada sucursal y su producto líder.
     */
    public List<BranchTopProduct> getTopStockProductsByBranch() {
        return branches.stream()
                .map(branch -> new BranchTopProduct(
                        branch.getId(),
                        branch.getName(),
                        branch.findTopStockProduct().orElse(null)
                ))
                .toList();
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("El nombre de la franquicia no puede estar vacío");
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

    public List<Branch> getBranches() {
        return Collections.unmodifiableList(branches);
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches != null ? new ArrayList<>(branches) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Franchise franchise = (Franchise) o;
        return Objects.equals(id, franchise.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Franchise{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", branchesCount=" + branches.size() +
                '}';
    }
}
