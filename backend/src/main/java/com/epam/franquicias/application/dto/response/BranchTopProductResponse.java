package com.epam.franquicias.application.dto.response;

public record BranchTopProductResponse(
        String branchId,
        String branchName,
        ProductResponse topProduct
) {}
