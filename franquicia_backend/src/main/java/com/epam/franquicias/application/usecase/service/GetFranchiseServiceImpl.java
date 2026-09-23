package com.epam.franquicias.application.usecase.service;

import com.epam.franquicias.application.usecase.GetFranchiseUseCase;
import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.repository.FranchiseRepository;

import java.util.List;

public class GetFranchiseServiceImpl implements GetFranchiseUseCase {

    private final FranchiseRepository repository;

    public GetFranchiseServiceImpl(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Franchise> getAll() {
        return repository.findAll();
    }

    @Override
    public Franchise getById(String franchiseId) {
        return repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));
    }
}
