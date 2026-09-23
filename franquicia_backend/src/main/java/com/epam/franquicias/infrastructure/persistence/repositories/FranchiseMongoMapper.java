package com.epam.franquicias.infrastructure.persistence.repositories;

import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.infrastructure.persistence.entities.BranchDocument;
import com.epam.franquicias.infrastructure.persistence.entities.FranchiseDocument;
import com.epam.franquicias.infrastructure.persistence.entities.ProductDocument;

import java.util.List;

public class FranchiseMongoMapper {

    public static Franchise toDomain(FranchiseDocument document) {
        if (document == null) return null;

        List<Branch> branches = document.getBranches() != null ?
                document.getBranches().stream()
                        .map(FranchiseMongoMapper::toDomain)
                        .toList() :
                List.of();

        return new Franchise(
                document.getId(),
                document.getName(),
                branches
        );
    }

    public static Branch toDomain(BranchDocument document) {
        if (document == null) return null;

        List<Product> products = document.getProducts() != null ?
                document.getProducts().stream()
                        .map(FranchiseMongoMapper::toDomain)
                        .toList() :
                List.of();

        return new Branch(
                document.getId(),
                document.getName(),
                products
        );
    }

    public static Product toDomain(ProductDocument document) {
        if (document == null) return null;
        return new Product(
                document.getId(),
                document.getName(),
                document.getStock()
        );
    }

    public static FranchiseDocument toDocument(Franchise domain) {
        if (domain == null) return null;

        List<BranchDocument> branchDocuments = domain.getBranches() != null ?
                domain.getBranches().stream()
                        .map(FranchiseMongoMapper::toDocument)
                        .toList() :
                List.of();

        return new FranchiseDocument(
                domain.getId(),
                domain.getName(),
                branchDocuments
        );
    }

    public static BranchDocument toDocument(Branch domain) {
        if (domain == null) return null;

        List<ProductDocument> productDocuments = domain.getProducts() != null ?
                domain.getProducts().stream()
                        .map(FranchiseMongoMapper::toDocument)
                        .toList() :
                List.of();

        return new BranchDocument(
                domain.getId(),
                domain.getName(),
                productDocuments
        );
    }

    public static ProductDocument toDocument(Product domain) {
        if (domain == null) return null;
        return new ProductDocument(
                domain.getId(),
                domain.getName(),
                domain.getStock()
        );
    }
}
