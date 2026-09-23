package com.epam.franquicias.infrastructure;

import com.epam.franquicias.application.controller.FranchiseController;
import com.epam.franquicias.application.dto.request.AddBranchRequest;
import com.epam.franquicias.application.dto.request.AddProductRequest;
import com.epam.franquicias.application.dto.request.CreateFranchiseRequest;
import com.epam.franquicias.application.dto.request.UpdateStockRequest;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.BranchTopProduct;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.usecase.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FranchiseController.class)
class FranchiseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateFranchiseUseCase createFranchiseUseCase;

    @MockitoBean
    private AddBranchUseCase addBranchUseCase;

    @MockitoBean
    private AddProductUseCase addProductUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockitoBean
    private UpdateStockUseCase updateStockUseCase;

    @MockitoBean
    private GetTopStockProductsUseCase getTopStockProductsUseCase;

    @MockitoBean
    private GetFranchiseUseCase getFranchiseUseCase;

    @MockitoBean
    private UpdateNamesUseCase updateNamesUseCase;

    @Test
    @DisplayName("Endpoint POST /api/v1/franchises debe responder 201 Created")
    void shouldCreateFranchiseEndpoint() throws Exception {
        Franchise franchise = new Franchise("f1", "Franquicia de Prueba", List.of());
        when(createFranchiseUseCase.execute(eq("Franquicia de Prueba"))).thenReturn(franchise);

        CreateFranchiseRequest request = new CreateFranchiseRequest("Franquicia de Prueba");

        mockMvc.perform(post("/api/v1/franchises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("f1"))
                .andExpect(jsonPath("$.name").value("Franquicia de Prueba"));
    }

    @Test
    @DisplayName("Endpoint POST /api/v1/franchises/{id}/branches debe responder 201 Created")
    void shouldAddBranchEndpoint() throws Exception {
        Branch branch = new Branch("b1", "Sucursal Central", List.of());
        when(addBranchUseCase.execute(eq("f1"), eq("Sucursal Central"))).thenReturn(branch);

        AddBranchRequest request = new AddBranchRequest("Sucursal Central");

        mockMvc.perform(post("/api/v1/franchises/f1/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("b1"))
                .andExpect(jsonPath("$.name").value("Sucursal Central"));
    }

    @Test
    @DisplayName("Endpoint POST /api/v1/franchises/{id}/branches/{id}/products debe responder 201 Created")
    void shouldAddProductEndpoint() throws Exception {
        Product product = new Product("p1", "Producto Test", 25);
        when(addProductUseCase.execute(eq("f1"), eq("b1"), eq("Producto Test"), eq(25))).thenReturn(product);

        AddProductRequest request = new AddProductRequest("Producto Test", 25);

        mockMvc.perform(post("/api/v1/franchises/f1/branches/b1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.name").value("Producto Test"))
                .andExpect(jsonPath("$.stock").value(25));
    }

    @Test
    @DisplayName("Endpoint DELETE /api/v1/franchises/{id}/branches/{id}/products/{id} debe responder 204 No Content")
    void shouldDeleteProductEndpoint() throws Exception {
        doNothing().when(deleteProductUseCase).execute("f1", "b1", "p1");

        mockMvc.perform(delete("/api/v1/franchises/f1/branches/b1/products/p1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Endpoint PATCH /api/v1/franchises/{id}/branches/{id}/products/{id}/stock debe responder 200 OK")
    void shouldUpdateStockEndpoint() throws Exception {
        Product product = new Product("p1", "Producto Test", 99);
        when(updateStockUseCase.execute(eq("f1"), eq("b1"), eq("p1"), eq(99))).thenReturn(product);

        UpdateStockRequest request = new UpdateStockRequest(99);

        mockMvc.perform(patch("/api/v1/franchises/f1/branches/b1/products/p1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(99));
    }

    @Test
    @DisplayName("Endpoint GET /api/v1/franchises/{id}/top-stock-products debe responder 200 OK con el listado estructurado")
    void shouldGetTopStockProductsEndpoint() throws Exception {
        Product topProduct = new Product("p-top", "Producto Líder", 500);
        BranchTopProduct item = new BranchTopProduct("b1", "Sucursal Central", topProduct);
        when(getTopStockProductsUseCase.execute("f1")).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/franchises/f1/top-stock-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].branchId").value("b1"))
                .andExpect(jsonPath("$[0].branchName").value("Sucursal Central"))
                .andExpect(jsonPath("$[0].topProduct.name").value("Producto Líder"))
                .andExpect(jsonPath("$[0].topProduct.stock").value(500));
    }
}
