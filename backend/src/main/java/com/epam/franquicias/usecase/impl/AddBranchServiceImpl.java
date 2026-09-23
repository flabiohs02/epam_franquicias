package com.epam.franquicias.usecase.impl;

import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.Branch;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.repository.FranchiseRepository;
import com.epam.franquicias.usecase.AddBranchUseCase;

public class AddBranchServiceImpl implements AddBranchUseCase {

    private final FranchiseRepository repository;

    public AddBranchServiceImpl(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Branch execute(String franchiseId, String branchName) {
        Franchise franchise = repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));

        Branch branch = Branch.create(branchName);
        franchise.addBranch(branch);

        repository.save(franchise);
        return branch;
    }
}
