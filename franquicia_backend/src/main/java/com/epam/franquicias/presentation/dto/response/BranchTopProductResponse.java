package com.epam.franquicias.presentation.dto.response;

public record BranchTopProductResponse(
        String branchId,
        String branchName,
        ProductResponse topProduct
) {}
