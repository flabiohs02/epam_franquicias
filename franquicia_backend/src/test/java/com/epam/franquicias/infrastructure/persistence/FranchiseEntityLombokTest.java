package com.epam.franquicias.infrastructure.persistence;

import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.infrastructure.persistence.entities.BranchDocument;
import com.epam.franquicias.infrastructure.persistence.entities.FranchiseDocument;
import com.epam.franquicias.infrastructure.persistence.entities.ProductDocument;
import com.epam.franquicias.infrastructure.persistence.repositories.FranchiseMongoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JUnit 5 Tests: Lombok Entities and Persistence Mappings")
class FranchiseEntityLombokTest {

    @Test
    @DisplayName("Debe construir ProductDocument usando Lombok Builder, Getters y Setters")
    void shouldBuildProductDocumentWithLombok() {
        ProductDocument product = ProductDocument.builder()
                .id("p-1")
                .name("Café Espresso")
                .stock(40)
                .build();

        assertNotNull(product);
        assertEquals("p-1", product.getId());
        assertEquals("Café Espresso", product.getName());
        assertEquals(40, product.getStock());

        product.setStock(60);
        assertEquals(60, product.getStock());
    }

    @Test
    @DisplayName("Debe construir BranchDocument usando Lombok Builder y verificar lista por defecto")
    void shouldBuildBranchDocumentWithLombok() {
        ProductDocument p1 = new ProductDocument("p-1", "Latte", 25);
        BranchDocument branch = BranchDocument.builder()
                .id("b-1")
                .name("Sucursal Salitre")
                .products(List.of(p1))
                .build();

        assertNotNull(branch);
        assertEquals("b-1", branch.getId());
        assertEquals("Sucursal Salitre", branch.getName());
        assertEquals(1, branch.getProducts().size());
        assertEquals("Latte", branch.getProducts().get(0).getName());
    }

    @Test
    @DisplayName("Debe construir FranchiseDocument usando Lombok y mapear bidireccionalmente con el dominio")
    void shouldMapBidirectionallyBetweenDomainAndLombokDocument() {
        Franchise domainFranchise = Franchise.create("Franquicia Juan Valdez");
        Branch domainBranch = Branch.create("Zona Rosa");
        Product domainProduct = Product.create("Nevado Café", 15);
        domainBranch.addProduct(domainProduct);
        domainFranchise.addBranch(domainBranch);

        FranchiseDocument document = FranchiseMongoMapper.toDocument(domainFranchise);

        assertNotNull(document);
        assertEquals(domainFranchise.getId(), document.getId());
        assertEquals("Franquicia Juan Valdez", document.getName());
        assertEquals(1, document.getBranches().size());
        assertEquals("Zona Rosa", document.getBranches().get(0).getName());
        assertEquals("Nevado Café", document.getBranches().get(0).getProducts().get(0).getName());

        Franchise reconstructed = FranchiseMongoMapper.toDomain(document);
        assertNotNull(reconstructed);
        assertEquals(domainFranchise.getId(), reconstructed.getId());
        assertEquals(domainFranchise.getName(), reconstructed.getName());
        assertEquals(1, reconstructed.getBranches().size());
        assertEquals("Zona Rosa", reconstructed.getBranches().get(0).getName());
        assertEquals(15, reconstructed.getBranches().get(0).getProducts().get(0).getStock());
    }
}
