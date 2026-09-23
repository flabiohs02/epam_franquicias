package com.epam.franquicias.application.controller;

import com.epam.franquicias.application.dto.request.AddBranchRequest;
import com.epam.franquicias.application.dto.request.AddProductRequest;
import com.epam.franquicias.application.dto.request.CreateFranchiseRequest;
import com.epam.franquicias.application.dto.request.UpdateNameRequest;
import com.epam.franquicias.application.dto.request.UpdateStockRequest;
import com.epam.franquicias.application.dto.response.BranchResponse;
import com.epam.franquicias.application.dto.response.BranchTopProductResponse;
import com.epam.franquicias.application.dto.response.FranchiseResponse;
import com.epam.franquicias.application.dto.response.ProductResponse;
import com.epam.franquicias.application.mapper.FranchiseWebMapper;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.BranchTopProduct;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/franchises")
@CrossOrigin(origins = "*")
@Tag(name = "Franquicias", description = "Endpoints para la gestión de Franquicias, Sucursales y Productos")
public class FranchiseController {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final GetTopStockProductsUseCase getTopStockProductsUseCase;
    private final GetFranchiseUseCase getFranchiseUseCase;
    private final UpdateNamesUseCase updateNamesUseCase;

    public FranchiseController(CreateFranchiseUseCase createFranchiseUseCase,
                               AddBranchUseCase addBranchUseCase,
                               AddProductUseCase addProductUseCase,
                               DeleteProductUseCase deleteProductUseCase,
                               UpdateStockUseCase updateStockUseCase,
                               GetTopStockProductsUseCase getTopStockProductsUseCase,
                               GetFranchiseUseCase getFranchiseUseCase,
                               UpdateNamesUseCase updateNamesUseCase) {
        this.createFranchiseUseCase = createFranchiseUseCase;
        this.addBranchUseCase = addBranchUseCase;
        this.addProductUseCase = addProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateStockUseCase = updateStockUseCase;
        this.getTopStockProductsUseCase = getTopStockProductsUseCase;
        this.getFranchiseUseCase = getFranchiseUseCase;
        this.updateNamesUseCase = updateNamesUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear nueva franquicia", description = "Crea y registra una franquicia en el sistema")
    public ResponseEntity<FranchiseResponse> createFranchise(@Valid @RequestBody CreateFranchiseRequest request) {
        Franchise franchise = createFranchiseUseCase.execute(request.name());
        FranchiseResponse response = FranchiseWebMapper.toResponse(franchise);
        return ResponseEntity.created(URI.create("/api/v1/franchises/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas las franquicias", description = "Obtiene el listado general de todas las franquicias registradas")
    public ResponseEntity<List<FranchiseResponse>> getAllFranchises() {
        List<FranchiseResponse> list = getFranchiseUseCase.getAll().stream()
                .map(FranchiseWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{franchiseId}")
    @Operation(summary = "Consultar franquicia por ID", description = "Retorna el detalle completo de una franquicia con sus sucursales y productos")
    public ResponseEntity<FranchiseResponse> getFranchiseById(@PathVariable String franchiseId) {
        Franchise franchise = getFranchiseUseCase.getById(franchiseId);
        return ResponseEntity.ok(FranchiseWebMapper.toResponse(franchise));
    }

    @PostMapping("/{franchiseId}/branches")
    @Operation(summary = "Agregar nueva sucursal a una franquicia", description = "Agrega una nueva sucursal a una franquicia existente")
    public ResponseEntity<BranchResponse> addBranch(@PathVariable String franchiseId,
                                                    @Valid @RequestBody AddBranchRequest request) {
        Branch branch = addBranchUseCase.execute(franchiseId, request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(FranchiseWebMapper.toResponse(branch));
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @Operation(summary = "Agregar nuevo producto a una sucursal", description = "Registra un nuevo producto con su stock inicial en la sucursal indicada")
    public ResponseEntity<ProductResponse> addProduct(@PathVariable String franchiseId,
                                                      @PathVariable String branchId,
                                                      @Valid @RequestBody AddProductRequest request) {
        Product product = addProductUseCase.execute(franchiseId, branchId, request.name(), request.stock());
        return ResponseEntity.status(HttpStatus.CREATED).body(FranchiseWebMapper.toResponse(product));
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @Operation(summary = "Eliminar producto de una sucursal", description = "Elimina un producto existente de una sucursal específica")
    public ResponseEntity<Void> deleteProduct(@PathVariable String franchiseId,
                                              @PathVariable String branchId,
                                              @PathVariable String productId) {
        deleteProductUseCase.execute(franchiseId, branchId, productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    @Operation(summary = "Modificar stock de un producto", description = "Actualiza el stock disponible de un producto de una sucursal")
    public ResponseEntity<ProductResponse> updateStock(@PathVariable String franchiseId,
                                                       @PathVariable String branchId,
                                                       @PathVariable String productId,
                                                       @Valid @RequestBody UpdateStockRequest request) {
        Product updated = updateStockUseCase.execute(franchiseId, branchId, productId, request.newStock());
        return ResponseEntity.ok(FranchiseWebMapper.toResponse(updated));
    }

    @GetMapping("/{franchiseId}/top-stock-products")
    @Operation(summary = "Consultar producto con mayor stock por cada sucursal (Requerimiento 7)",
            description = "Retorna un listado estructurado con el producto que más existencias tiene en cada una de las sucursales de la franquicia")
    public ResponseEntity<List<BranchTopProductResponse>> getTopStockProducts(@PathVariable String franchiseId) {
        List<BranchTopProduct> result = getTopStockProductsUseCase.execute(franchiseId);
        List<BranchTopProductResponse> response = result.stream()
                .map(FranchiseWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{franchiseId}/name")
    @Operation(summary = "Modificar nombre de una franquicia", description = "Actualiza el nombre de una franquicia existente")
    public ResponseEntity<FranchiseResponse> updateFranchiseName(@PathVariable String franchiseId,
                                                                 @Valid @RequestBody UpdateNameRequest request) {
        Franchise franchise = updateNamesUseCase.updateFranchiseName(franchiseId, request.name());
        return ResponseEntity.ok(FranchiseWebMapper.toResponse(franchise));
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/name")
    @Operation(summary = "Modificar nombre de una sucursal", description = "Actualiza el nombre de una sucursal perteneciente a una franquicia")
    public ResponseEntity<BranchResponse> updateBranchName(@PathVariable String franchiseId,
                                                           @PathVariable String branchId,
                                                           @Valid @RequestBody UpdateNameRequest request) {
        Branch branch = updateNamesUseCase.updateBranchName(franchiseId, branchId, request.name());
        return ResponseEntity.ok(FranchiseWebMapper.toResponse(branch));
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/name")
    @Operation(summary = "Modificar nombre de un producto", description = "Actualiza el nombre de un producto en una sucursal")
    public ResponseEntity<ProductResponse> updateProductName(@PathVariable String franchiseId,
                                                             @PathVariable String branchId,
                                                             @PathVariable String productId,
                                                             @Valid @RequestBody UpdateNameRequest request) {
        Product product = updateNamesUseCase.updateProductName(franchiseId, branchId, productId, request.name());
        return ResponseEntity.ok(FranchiseWebMapper.toResponse(product));
    }
}
