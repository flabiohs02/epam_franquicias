package com.epam.franquicias.application.usecase.service;

import com.epam.franquicias.application.usecase.DeleteProductUseCase;
import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.repository.FranchiseRepository;

public class DeleteProductServiceImpl implements DeleteProductUseCase {

    private final FranchiseRepository repository;

    public DeleteProductServiceImpl(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(String franchiseId, String branchId, String productId) {
        Franchise franchise = repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));

        franchise.removeProductFromBranch(branchId, productId);
        repository.save(franchise);
    }
}
