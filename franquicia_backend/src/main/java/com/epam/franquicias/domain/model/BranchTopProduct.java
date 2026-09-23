package com.epam.franquicias.domain.model;

public record BranchTopProduct(
        String branchId,
        String branchName,
        Product topProduct
) {}
