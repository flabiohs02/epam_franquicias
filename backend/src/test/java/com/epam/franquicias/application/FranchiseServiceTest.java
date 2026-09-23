package com.epam.franquicias.application;

import com.epam.franquicias.domain.exception.DuplicateEntityException;
import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.BranchTopProduct;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.domain.repository.FranchiseRepository;
import com.epam.franquicias.usecase.impl.*;
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
    @DisplayName("Caso de uso: Error al crear franquicia con nombre duplicado")
    void shouldThrowDuplicateWhenCreatingWithExistingName() {
        when(repository.existsByName("Franquicia ABC")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> createFranchiseService.execute("Franquicia ABC"));
        verify(repository, never()).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Caso de uso: Agregar sucursal a franquicia existente")
    void shouldAddBranchToFranchise() {
        Franchise franchise = Franchise.create("Franquicia XYZ");
        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));
        when(repository.save(any(Franchise.class))).thenAnswer(i -> i.getArgument(0));

        Branch branch = addBranchService.execute(franchise.getId(), "Sucursal Oeste");

        assertNotNull(branch);
        assertEquals("Sucursal Oeste", branch.getName());
        assertEquals(1, franchise.getBranches().size());
        verify(repository).save(franchise);
    }

    @Test
    @DisplayName("Caso de uso: Agregar producto a sucursal")
    void shouldAddProductToBranch() {
        Franchise franchise = Franchise.create("Franquicia XYZ");
        Branch branch = Branch.create("Sucursal 1");
        franchise.addBranch(branch);

        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));
        when(repository.save(any(Franchise.class))).thenAnswer(i -> i.getArgument(0));

        Product product = addProductService.execute(franchise.getId(), branch.getId(), "Producto A", 50);

        assertNotNull(product);
        assertEquals("Producto A", product.getName());
        assertEquals(50, product.getStock());
        verify(repository).save(franchise);
    }

    @Test
    @DisplayName("Caso de uso: Actualizar stock de producto")
    void shouldUpdateProductStock() {
        Franchise franchise = Franchise.create("Franquicia XYZ");
        Branch branch = Branch.create("Sucursal 1");
        Product product = Product.create("Producto A", 20);
        branch.addProduct(product);
        franchise.addBranch(branch);

        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));
        when(repository.save(any(Franchise.class))).thenAnswer(i -> i.getArgument(0));

        Product updated = updateStockService.execute(franchise.getId(), branch.getId(), product.getId(), 85);

        assertEquals(85, updated.getStock());
        verify(repository).save(franchise);
    }

    @Test
    @DisplayName("Caso de uso: Eliminar producto de sucursal")
    void shouldDeleteProduct() {
        Franchise franchise = Franchise.create("Franquicia XYZ");
        Branch branch = Branch.create("Sucursal 1");
        Product product = Product.create("Producto A", 20);
        branch.addProduct(product);
        franchise.addBranch(branch);

        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));
        when(repository.save(any(Franchise.class))).thenAnswer(i -> i.getArgument(0));

        deleteProductService.execute(franchise.getId(), branch.getId(), product.getId());

        assertTrue(franchise.getBranchOrThrow(branch.getId()).getProducts().isEmpty());
        verify(repository).save(franchise);
    }

    @Test
    @DisplayName("Caso de uso: Consulta analítica de producto con mayor stock por sucursal")
    void shouldGetTopStockProductsByFranchise() {
        Franchise franchise = Franchise.create("Franquicia Top");
        Branch branch = Branch.create("Sucursal Alpha");
        branch.addProduct(new Product("p1", "Producto Barato", 10));
        branch.addProduct(new Product("p2", "Producto Estrella", 999));
        franchise.addBranch(branch);

        when(repository.findById(franchise.getId())).thenReturn(Optional.of(franchise));

        List<BranchTopProduct> result = getTopStockProductsService.execute(franchise.getId());

        assertEquals(1, result.size());
        assertEquals("Producto Estrella", result.get(0).topProduct().getName());
        assertEquals(999, result.get(0).topProduct().getStock());
    }

    @Test
    @DisplayName("Debe fallar al buscar franquicia inexistente")
    void shouldThrowWhenFranchiseNotFound() {
        when(repository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                addBranchService.execute("invalid-id", "Sucursal")
        );
    }
}
