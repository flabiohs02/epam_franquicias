package com.epam.franquicias.application.usecase.service;

import com.epam.franquicias.application.usecase.GetTopStockProductsUseCase;
import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.BranchTopProduct;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.repository.FranchiseRepository;

import java.util.List;

public class GetTopStockProductsServiceImpl implements GetTopStockProductsUseCase {

    private final FranchiseRepository repository;

    public GetTopStockProductsServiceImpl(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BranchTopProduct> execute(String franchiseId) {
        Franchise franchise = repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));

        return franchise.getTopStockProductsByBranch();
    }
}
