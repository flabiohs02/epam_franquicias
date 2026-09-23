package com.epam.franquicias.application.mapper;

import com.epam.franquicias.application.dto.response.BranchResponse;
import com.epam.franquicias.application.dto.response.BranchTopProductResponse;
import com.epam.franquicias.application.dto.response.FranchiseResponse;
import com.epam.franquicias.application.dto.response.ProductResponse;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.BranchTopProduct;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;

import java.util.List;

public class FranchiseWebMapper {

    public static FranchiseResponse toResponse(Franchise franchise) {
        if (franchise == null) return null;

        List<BranchResponse> branchResponses = franchise.getBranches() != null ?
                franchise.getBranches().stream()
                        .map(FranchiseWebMapper::toResponse)
                        .toList() :
                List.of();

        return new FranchiseResponse(
                franchise.getId(),
                franchise.getName(),
                branchResponses
        );
    }

    public static BranchResponse toResponse(Branch branch) {
        if (branch == null) return null;

        List<ProductResponse> productResponses = branch.getProducts() != null ?
                branch.getProducts().stream()
                        .map(FranchiseWebMapper::toResponse)
                        .toList() :
                List.of();

        return new BranchResponse(
                branch.getId(),
                branch.getName(),
                productResponses
        );
    }

    public static ProductResponse toResponse(Product product) {
        if (product == null) return null;
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getStock()
        );
    }

    public static BranchTopProductResponse toResponse(BranchTopProduct item) {
        if (item == null) return null;
        return new BranchTopProductResponse(
                item.branchId(),
                item.branchName(),
                toResponse(item.topProduct())
        );
    }
}
