package com.epam.franquicias.application.usecase.service;

import com.epam.franquicias.application.usecase.CreateFranchiseUseCase;
import com.epam.franquicias.domain.exception.DuplicateEntityException;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.repository.FranchiseRepository;

public class CreateFranchiseServiceImpl implements CreateFranchiseUseCase {

    private final FranchiseRepository repository;

    public CreateFranchiseServiceImpl(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Franchise execute(String franchiseName) {
        if (franchiseName == null || franchiseName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la franquicia no puede estar vacío");
        }
        if (repository.existsByName(franchiseName.trim())) {
            throw new DuplicateEntityException("Ya existe una franquicia con el nombre '" + franchiseName.trim() + "'");
        }
        Franchise franchise = Franchise.create(franchiseName);
        return repository.save(franchise);
    }
}
