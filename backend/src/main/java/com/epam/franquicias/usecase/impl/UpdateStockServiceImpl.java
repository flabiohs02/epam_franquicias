package com.epam.franquicias.usecase.impl;

import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.domain.repository.FranchiseRepository;
import com.epam.franquicias.usecase.UpdateStockUseCase;

public class UpdateStockServiceImpl implements UpdateStockUseCase {

    private final FranchiseRepository repository;

    public UpdateStockServiceImpl(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product execute(String franchiseId, String branchId, String productId, int newStock) {
        Franchise franchise = repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));

        franchise.updateProductStock(branchId, productId, newStock);
        repository.save(franchise);

        Branch branch = franchise.getBranchOrThrow(branchId);
        return branch.getProductOrThrow(productId);
    }
}
