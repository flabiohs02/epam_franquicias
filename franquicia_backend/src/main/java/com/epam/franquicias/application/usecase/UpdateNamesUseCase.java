package com.epam.franquicias.application.usecase;

import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;

public interface UpdateNamesUseCase {
    Franchise updateFranchiseName(String franchiseId, String newName);
    Branch updateBranchName(String franchiseId, String branchId, String newName);
    Product updateProductName(String franchiseId, String branchId, String productId, String newName);
}
