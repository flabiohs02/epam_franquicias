package com.epam.franquicias.application.usecase.service;

import com.epam.franquicias.application.usecase.UpdateNamesUseCase;
import com.epam.franquicias.domain.exception.DuplicateEntityException;
import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.domain.repository.FranchiseRepository;

public class UpdateNamesServiceImpl implements UpdateNamesUseCase {

    private final FranchiseRepository repository;

    public UpdateNamesServiceImpl(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Franchise updateFranchiseName(String franchiseId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre de la franquicia no puede estar vacío");
        }
        Franchise franchise = repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));

        if (!franchise.getName().equalsIgnoreCase(newName.trim()) && repository.existsByName(newName.trim())) {
            throw new DuplicateEntityException("Ya existe una franquicia con el nombre '" + newName.trim() + "'");
        }

        franchise.updateName(newName);
        return repository.save(franchise);
    }

    @Override
    public Branch updateBranchName(String franchiseId, String branchId, String newName) {
        Franchise franchise = repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));

        franchise.updateBranchName(branchId, newName);
        repository.save(franchise);

        return franchise.getBranchOrThrow(branchId);
    }

    @Override
    public Product updateProductName(String franchiseId, String branchId, String productId, String newName) {
        Franchise franchise = repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));

        franchise.updateProductName(branchId, productId, newName);
        repository.save(franchise);

        return franchise.getBranchOrThrow(branchId).getProductOrThrow(productId);
    }
}
