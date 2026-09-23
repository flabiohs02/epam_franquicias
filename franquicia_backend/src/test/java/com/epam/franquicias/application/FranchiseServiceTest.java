package com.epam.franquicias.application;

import com.epam.franquicias.application.usecase.service.*;
import com.epam.franquicias.domain.exception.DuplicateEntityException;
import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.BranchTopProduct;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.domain.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    private FranchiseRepository repository;

    private CreateFranchiseServiceImpl createFranchiseService;
    private AddBranchServiceImpl addBranchService;
    private AddProductServiceImpl addProductService;
    private DeleteProductServiceImpl deleteProductService;
    private UpdateStockServiceImpl updateStockService;
    private GetTopStockProductsServiceImpl getTopStockProductsService;

    @BeforeEach
    void setUp() {
        createFranchiseService = new CreateFranchiseServiceImpl(repository);
        addBranchService = new AddBranchServiceImpl(repository);
        addProductService = new AddProductServiceImpl(repository);
        deleteProductService = new DeleteProductServiceImpl(repository);
        updateStockService = new UpdateStockServiceImpl(repository);
        getTopStockProductsService = new GetTopStockProductsServiceImpl(repository);
    }

    @Test
    @DisplayName("Caso de uso: Crear franquicia con éxito")
    void shouldCreateFranchise() {
        when(repository.existsByName("Franquicia ABC")).thenReturn(false);
        when(repository.save(any(Franchise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Franchise result = createFranchiseService.execute("Franquicia ABC");

        assertNotNull(result);
        assertEquals("Franquicia ABC", result.getName());
        verify(repository, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Caso de uso: Falla al crear franquicia con nombre duplicado")
    void shouldThrowWhenCreatingDuplicateFranchise() {
        when(repository.existsByName("Franquicia Existente")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () ->
                createFranchiseService.execute("Franquicia Existente")
        );
        verify(repository, never()).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Caso de uso: Agregar sucursal a franquicia")
    void shouldAddBranchToFranchise() {
        Franchise franchise = Franchise.create("Franquicia ABC");
        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));
        when(repository.save(any(Franchise.class))).thenReturn(franchise);

        Branch branch = addBranchService.execute(franchise.getId(), "Sucursal Norte");

        assertNotNull(branch);
        assertEquals("Sucursal Norte", branch.getName());
        assertEquals(1, franchise.getBranches().size());
        verify(repository, times(1)).save(franchise);
    }

    @Test
    @DisplayName("Caso de uso: Agregar producto a sucursal")
    void shouldAddProductToBranch() {
        Franchise franchise = Franchise.create("Franquicia ABC");
        Branch branch = Branch.create("Sucursal Centro");
        franchise.addBranch(branch);

        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));
        when(repository.save(any(Franchise.class))).thenReturn(franchise);

        Product product = addProductService.execute(franchise.getId(), branch.getId(), "Capuchino", 20);

        assertNotNull(product);
        assertEquals("Capuchino", product.getName());
        assertEquals(20, product.getStock());
        verify(repository, times(1)).save(franchise);
    }

    @Test
    @DisplayName("Caso de uso: Eliminar producto de una sucursal")
    void shouldDeleteProductFromBranch() {
        Franchise franchise = Franchise.create("Franquicia ABC");
        Branch branch = Branch.create("Sucursal Centro");
        Product product = Product.create("Té", 15);
        branch.addProduct(product);
        franchise.addBranch(branch);

        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));
        when(repository.save(any(Franchise.class))).thenReturn(franchise);

        deleteProductService.execute(franchise.getId(), branch.getId(), product.getId());

        assertEquals(0, branch.getProducts().size());
        verify(repository, times(1)).save(franchise);
    }

    @Test
    @DisplayName("Caso de uso: Modificar stock de un producto")
    void shouldUpdateProductStock() {
        Franchise franchise = Franchise.create("Franquicia ABC");
        Branch branch = Branch.create("Sucursal Centro");
        Product product = Product.create("Té", 15);
        branch.addProduct(product);
        franchise.addBranch(branch);

        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));
        when(repository.save(any(Franchise.class))).thenReturn(franchise);

        Product updatedProduct = updateStockService.execute(franchise.getId(), branch.getId(), product.getId(), 50);

        assertEquals(50, updatedProduct.getStock());
        verify(repository, times(1)).save(franchise);
    }

    @Test
    @DisplayName("Caso de uso: Requerimiento 7 - Obtener productos con mayor stock")
    void shouldGetTopStockProducts() {
        Franchise franchise = Franchise.create("Franquicia ABC");
        Branch branch = Branch.create("Sucursal 1");
        branch.addProduct(Product.create("P1", 10));
        branch.addProduct(Product.create("P2 (Mayor)", 100));
        franchise.addBranch(branch);

        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));

        List<BranchTopProduct> topProducts = getTopStockProductsService.execute(franchise.getId());

        assertEquals(1, topProducts.size());
        assertEquals("P2 (Mayor)", topProducts.get(0).topProduct().getName());
    }

    @Test
    @DisplayName("Lanzar excepción cuando la franquicia no existe")
    void shouldThrowWhenFranchiseNotFound() {
        when(repository.findById("unknown-id")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                addBranchService.execute("unknown-id", "Sucursal")
        );
    }
}
