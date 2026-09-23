package com.epam.franquicias.domain;

import com.epam.franquicias.domain.exception.DuplicateEntityException;
import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.exception.InvalidStockException;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.BranchTopProduct;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {

    @Test
    @DisplayName("Crear franquicia válida")
    void shouldCreateFranchiseSuccessfully() {
        Franchise franchise = Franchise.create("Franquicia Starbucks");
        assertNotNull(franchise.getId());
        assertEquals("Franquicia Starbucks", franchise.getName());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Agregar sucursal a la franquicia")
    void shouldAddBranchToFranchise() {
        Franchise franchise = Franchise.create("Franquicia Starbucks");
        Branch branch = Branch.create("Sucursal Chapinero");

        franchise.addBranch(branch);

        assertEquals(1, franchise.getBranches().size());
        assertEquals("Sucursal Chapinero", franchise.getBranches().get(0).getName());
    }

    @Test
    @DisplayName("No permitir sucursales con nombres duplicados")
    void shouldThrowWhenDuplicateBranchName() {
        Franchise franchise = Franchise.create("Franquicia Starbucks");
        franchise.addBranch(Branch.create("Sucursal Norte"));

        assertThrows(DuplicateEntityException.class, () ->
                franchise.addBranch(Branch.create("Sucursal Norte"))
        );
    }

    @Test
    @DisplayName("Agregar producto a una sucursal")
    void shouldAddProductToBranch() {
        Franchise franchise = Franchise.create("Franquicia Starbucks");
        Branch branch = Branch.create("Sucursal Chapinero");
        franchise.addBranch(branch);

        Product product = Product.create("Café Latte", 50);
        franchise.addProductToBranch(branch.getId(), product);

        Branch updatedBranch = franchise.getBranchOrThrow(branch.getId());
        assertEquals(1, updatedBranch.getProducts().size());
        assertEquals(50, updatedBranch.getProducts().get(0).getStock());
    }

    @Test
    @DisplayName("No permitir producto con stock negativo")
    void shouldThrowWhenProductStockIsNegative() {
        assertThrows(InvalidStockException.class, () ->
                Product.create("Café Mocha", -5)
        );
    }

    @Test
    @DisplayName("Eliminar producto de una sucursal")
    void shouldRemoveProductFromBranch() {
        Franchise franchise = Franchise.create("Franquicia Starbucks");
        Branch branch = Branch.create("Sucursal Chapinero");
        franchise.addBranch(branch);

        Product product = Product.create("Té Verde", 30);
        franchise.addProductToBranch(branch.getId(), product);
        assertEquals(1, franchise.getBranchOrThrow(branch.getId()).getProducts().size());

        franchise.removeProductFromBranch(branch.getId(), product.getId());
        assertEquals(0, franchise.getBranchOrThrow(branch.getId()).getProducts().size());
    }

    @Test
    @DisplayName("Modificar el stock de un producto")
    void shouldUpdateProductStock() {
        Franchise franchise = Franchise.create("Franquicia Starbucks");
        Branch branch = Branch.create("Sucursal Chapinero");
        franchise.addBranch(branch);

        Product product = Product.create("Espresso", 10);
        franchise.addProductToBranch(branch.getId(), product);

        franchise.updateProductStock(branch.getId(), product.getId(), 45);

        Branch updatedBranch = franchise.getBranchOrThrow(branch.getId());
        assertEquals(45, updatedBranch.getProductOrThrow(product.getId()).getStock());
    }

    @Test
    @DisplayName("Requerimiento 7: Determinar producto con mayor stock por cada sucursal")
    void shouldReturnTopStockProductByBranch() {
        Franchise franchise = Franchise.create("Franquicia Starbucks");

        Branch branchA = Branch.create("Sucursal Centro");
        branchA.addProduct(Product.create("Café A1", 20));
        branchA.addProduct(Product.create("Café A2 (Top)", 150));
        branchA.addProduct(Product.create("Café A3", 80));

        Branch branchB = Branch.create("Sucursal Norte");
        branchB.addProduct(Product.create("Café B1", 90));
        branchB.addProduct(Product.create("Café B2 (Top)", 300));

        Branch branchC = Branch.create("Sucursal Sur (Vacía)");

        franchise.addBranch(branchA);
        franchise.addBranch(branchB);
        franchise.addBranch(branchC);

        List<BranchTopProduct> topProducts = franchise.getTopStockProductsByBranch();

        assertEquals(3, topProducts.size());

        BranchTopProduct topA = topProducts.stream().filter(t -> t.branchId().equals(branchA.getId())).findFirst().orElseThrow();
        assertEquals("Café A2 (Top)", topA.topProduct().getName());
        assertEquals(150, topA.topProduct().getStock());

        BranchTopProduct topB = topProducts.stream().filter(t -> t.branchId().equals(branchB.getId())).findFirst().orElseThrow();
        assertEquals("Café B2 (Top)", topB.topProduct().getName());
        assertEquals(300, topB.topProduct().getStock());

        BranchTopProduct topC = topProducts.stream().filter(t -> t.branchId().equals(branchC.getId())).findFirst().orElseThrow();
        assertNull(topC.topProduct());
    }

    @Test
    @DisplayName("Lanzar excepción cuando la sucursal no existe")
    void shouldThrowWhenBranchNotFound() {
        Franchise franchise = Franchise.create("Franquicia Starbucks");
        assertThrows(EntityNotFoundException.class, () ->
                franchise.getBranchOrThrow("non-existent-id")
        );
    }
}
