package com.epam.franquicias.presentation;

import com.epam.franquicias.application.usecase.*;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.BranchTopProduct;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.presentation.controllers.FranchiseController;
import com.epam.franquicias.presentation.dto.request.AddBranchRequest;
import com.epam.franquicias.presentation.dto.request.AddProductRequest;
import com.epam.franquicias.presentation.dto.request.CreateFranchiseRequest;
import com.epam.franquicias.presentation.dto.request.UpdateStockRequest;
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
    @DisplayName("POST /api/v1/franchises - Debe retornar 201 Created al crear franquicia")
    void shouldReturn201WhenCreatingFranchise() throws Exception {
        CreateFranchiseRequest request = new CreateFranchiseRequest("Franquicia Subway");
        Franchise franchise = Franchise.create("Franquicia Subway");

        when(createFranchiseUseCase.execute("Franquicia Subway")).thenReturn(franchise);

        mockMvc.perform(post("/api/v1/franchises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Franquicia Subway"))
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("POST /api/v1/franchises/{id}/branches - Debe retornar 201 Created al agregar sucursal")
    void shouldReturn201WhenAddingBranch() throws Exception {
        AddBranchRequest request = new AddBranchRequest("Sucursal Unicentro");
        Branch branch = Branch.create("Sucursal Unicentro");

        when(addBranchUseCase.execute(eq("f-123"), eq("Sucursal Unicentro"))).thenReturn(branch);

        mockMvc.perform(post("/api/v1/franchises/f-123/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sucursal Unicentro"));
    }

    @Test
    @DisplayName("POST /api/v1/franchises/{id}/branches/{branchId}/products - Debe retornar 201 Created")
    void shouldReturn201WhenAddingProduct() throws Exception {
        AddProductRequest request = new AddProductRequest("Sub Italiano", 30);
        Product product = Product.create("Sub Italiano", 30);

        when(addProductUseCase.execute(eq("f-1"), eq("b-1"), eq("Sub Italiano"), eq(30))).thenReturn(product);

        mockMvc.perform(post("/api/v1/franchises/f-1/branches/b-1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sub Italiano"))
                .andExpect(jsonPath("$.stock").value(30));
    }

    @Test
    @DisplayName("DELETE /api/v1/franchises/{id}/branches/{branchId}/products/{productId} - Debe retornar 204 No Content")
    void shouldReturn204WhenDeletingProduct() throws Exception {
        doNothing().when(deleteProductUseCase).execute("f-1", "b-1", "p-1");

        mockMvc.perform(delete("/api/v1/franchises/f-1/branches/b-1/products/p-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/v1/franchises/{id}/branches/{branchId}/products/{productId}/stock - Debe retornar 200 OK")
    void shouldReturn200WhenUpdatingStock() throws Exception {
        UpdateStockRequest request = new UpdateStockRequest(80);
        Product product = Product.create("Galleta Avena", 80);

        when(updateStockUseCase.execute(eq("f-1"), eq("b-1"), eq("p-1"), eq(80))).thenReturn(product);

        mockMvc.perform(patch("/api/v1/franchises/f-1/branches/b-1/products/p-1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(80));
    }

    @Test
    @DisplayName("GET /api/v1/franchises/{id}/top-stock-products - Debe retornar 200 OK con el listado del Requerimiento 7")
    void shouldReturn200WithTopStockProducts() throws Exception {
        Product topProduct = Product.create("Bebida Grande", 120);
        BranchTopProduct topItem = new BranchTopProduct("b-1", "Sucursal 1", topProduct);

        when(getTopStockProductsUseCase.execute("f-1")).thenReturn(List.of(topItem));

        mockMvc.perform(get("/api/v1/franchises/f-1/top-stock-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].branchId").value("b-1"))
                .andExpect(jsonPath("$[0].branchName").value("Sucursal 1"))
                .andExpect(jsonPath("$[0].topProduct.name").value("Bebida Grande"))
                .andExpect(jsonPath("$[0].topProduct.stock").value(120));
    }
}
