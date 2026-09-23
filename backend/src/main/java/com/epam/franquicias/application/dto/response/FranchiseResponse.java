package com.epam.franquicias.application.dto.response;

import java.util.List;

public record FranchiseResponse(
        String id,
        String name,
        List<BranchResponse> branches
) {}
