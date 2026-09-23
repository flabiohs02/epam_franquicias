package com.epam.franquicias.infrastructure.persistence.mapper;

import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.infrastructure.persistence.document.BranchDocument;
import com.epam.franquicias.infrastructure.persistence.document.FranchiseDocument;
import com.epam.franquicias.infrastructure.persistence.document.ProductDocument;

import java.util.ArrayList;
import java.util.List;

public class FranchiseMongoMapper {

    public static FranchiseDocument toDocument(Franchise domain) {
        if (domain == null) return null;

        List<BranchDocument> branchDocs = domain.getBranches().stream()
                .map(FranchiseMongoMapper::toBranchDocument)
                .toList();

        return new FranchiseDocument(
                domain.getId(),
                domain.getName(),
                new ArrayList<>(branchDocs)
        );
    }

    public static Franchise toDomain(FranchiseDocument doc) {
        if (doc == null) return null;

        List<Branch> branches = doc.getBranches() != null ?
                doc.getBranches().stream()
                        .map(FranchiseMongoMapper::toBranchDomain)
                        .toList() :
                List.of();

        return new Franchise(
                doc.getId(),
                doc.getName(),
                branches
        );
    }

    public static BranchDocument toBranchDocument(Branch branch) {
        if (branch == null) return null;

        List<ProductDocument> productDocs = branch.getProducts().stream()
                .map(FranchiseMongoMapper::toProductDocument)
                .toList();

        return new BranchDocument(
                branch.getId(),
                branch.getName(),
                new ArrayList<>(productDocs)
        );
    }

    public static Branch toBranchDomain(BranchDocument doc) {
        if (doc == null) return null;

        List<Product> products = doc.getProducts() != null ?
                doc.getProducts().stream()
                        .map(FranchiseMongoMapper::toProductDomain)
                        .toList() :
                List.of();

        return new Branch(
                doc.getId(),
                doc.getName(),
                products
        );
    }

    public static ProductDocument toProductDocument(Product product) {
        if (product == null) return null;
        return new ProductDocument(
                product.getId(),
                product.getName(),
                product.getStock()
        );
    }

    public static Product toProductDomain(ProductDocument doc) {
        if (doc == null) return null;
        return new Product(
                doc.getId(),
                doc.getName(),
                doc.getStock()
        );
    }
}
