package com.epam.franquicias.domain;

import com.epam.franquicias.domain.exception.DomainException;
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
    @DisplayName("Debe crear una franquicia con nombre válido")
    void shouldCreateFranchiseSuccessfully() {
        Franchise franchise = Franchise.create("Franquicia Gourmet");
        assertNotNull(franchise.getId());
        assertEquals("Franquicia Gourmet", franchise.getName());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Debe fallar al crear franquicia con nombre vacío")
    void shouldFailWhenNameIsBlank() {
        assertThrows(DomainException.class, () -> Franchise.create("   "));
        assertThrows(DomainException.class, () -> Franchise.create(null));
    }

    @Test
    @DisplayName("Debe agregar una sucursal correctamente a la franquicia")
    void shouldAddBranchSuccessfully() {
        Franchise franchise = Franchise.create("Franquicia Gourmet");
        Branch branch = Branch.create("Sucursal Norte");

        franchise.addBranch(branch);

        assertEquals(1, franchise.getBranches().size());
        assertEquals("Sucursal Norte", franchise.getBranches().get(0).getName());
    }

    @Test
    @DisplayName("Debe lanzar excepción al agregar sucursal con nombre duplicado")
    void shouldThrowDuplicateEntityWhenBranchNameAlreadyExists() {
        Franchise franchise = Franchise.create("Franquicia Gourmet");
        franchise.addBranch(Branch.create("Sucursal Norte"));

        assertThrows(DuplicateEntityException.class, () ->
                franchise.addBranch(Branch.create("sucursal norte"))
        );
    }

    @Test
    @DisplayName("Debe agregar un producto a una sucursal existente")
    void shouldAddProductToBranch() {
        Franchise franchise = Franchise.create("Franquicia Gourmet");
        Branch branch = Branch.create("Sucursal Norte");
        franchise.addBranch(branch);

        Product product = Product.create("Hamburguesa Clásica", 25);
        franchise.addProductToBranch(branch.getId(), product);

        Branch foundBranch = franchise.getBranchOrThrow(branch.getId());
        assertEquals(1, foundBranch.getProducts().size());
        assertEquals("Hamburguesa Clásica", foundBranch.getProducts().get(0).getName());
        assertEquals(25, foundBranch.getProducts().get(0).getStock());
    }

    @Test
    @DisplayName("Debe fallar al crear un producto con stock negativo")
    void shouldFailWhenProductStockIsNegative() {
        assertThrows(InvalidStockException.class, () -> Product.create("Producto Test", -5));
    }

    @Test
    @DisplayName("Debe actualizar el stock de un producto")
    void shouldUpdateProductStock() {
        Franchise franchise = Franchise.create("Franquicia Gourmet");
        Branch branch = Branch.create("Sucursal Norte");
        franchise.addBranch(branch);
        Product product = Product.create("Papas Fritas", 10);
        franchise.addProductToBranch(branch.getId(), product);

        franchise.updateProductStock(branch.getId(), product.getId(), 40);

        Branch foundBranch = franchise.getBranchOrThrow(branch.getId());
        Product foundProduct = foundBranch.getProductOrThrow(product.getId());
        assertEquals(40, foundProduct.getStock());
    }

    @Test
    @DisplayName("Debe eliminar un producto de una sucursal")
    void shouldDeleteProduct() {
        Franchise franchise = Franchise.create("Franquicia Gourmet");
        Branch branch = Branch.create("Sucursal Norte");
        franchise.addBranch(branch);
        Product product = Product.create("Refresco", 15);
        franchise.addProductToBranch(branch.getId(), product);

        franchise.removeProductFromBranch(branch.getId(), product.getId());

        Branch foundBranch = franchise.getBranchOrThrow(branch.getId());
        assertTrue(foundBranch.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Requerimiento 7: Debe obtener el producto con mayor stock por cada sucursal")
    void shouldReturnTopStockProductsByBranchCorrectly() {
        Franchise franchise = Franchise.create("Franquicia EPAM");

        // Sucursal 1
        Branch branch1 = Branch.create("Sucursal Centro");
        branch1.addProduct(new Product("p1", "Café Latte", 15));
        branch1.addProduct(new Product("p2", "Capuchino", 50));
        branch1.addProduct(new Product("p3", "Espresso", 30));
        franchise.addBranch(branch1);

        // Sucursal 2
        Branch branch2 = Branch.create("Sucursal Sur");
        branch2.addProduct(new Product("p4", "Té Chai", 100));
        branch2.addProduct(new Product("p5", "Croissant", 180));
        branch2.addProduct(new Product("p6", "Muffin", 45));
        franchise.addBranch(branch2);

        // Sucursal 3 (Sin productos)
        Branch branch3 = Branch.create("Sucursal Este");
        franchise.addBranch(branch3);

        List<BranchTopProduct> result = franchise.getTopStockProductsByBranch();

        assertEquals(3, result.size());

        // Verificar sucursal 1
        BranchTopProduct top1 = result.stream()
                .filter(r -> r.branchId().equals(branch1.getId()))
                .findFirst().orElseThrow();
        assertEquals("Capuchino", top1.topProduct().getName());
        assertEquals(50, top1.topProduct().getStock());

        // Verificar sucursal 2
        BranchTopProduct top2 = result.stream()
                .filter(r -> r.branchId().equals(branch2.getId()))
                .findFirst().orElseThrow();
        assertEquals("Croissant", top2.topProduct().getName());
        assertEquals(180, top2.topProduct().getStock());

        // Verificar sucursal 3 (sin productos retorna null)
        BranchTopProduct top3 = result.stream()
                .filter(r -> r.branchId().equals(branch3.getId()))
                .findFirst().orElseThrow();
        assertNull(top3.topProduct());
    }
}
