package com.epam.franquicias.usecase.impl;

import com.epam.franquicias.domain.exception.EntityNotFoundException;
import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.model.Product;
import com.epam.franquicias.domain.repository.FranchiseRepository;
import com.epam.franquicias.usecase.AddProductUseCase;

public class AddProductServiceImpl implements AddProductUseCase {

    private final FranchiseRepository repository;

    public AddProductServiceImpl(FranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product execute(String franchiseId, String branchId, String productName, int initialStock) {
        Franchise franchise = repository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("Franquicia no encontrada con ID: " + franchiseId));

        Product product = Product.create(productName, initialStock);
        franchise.addProductToBranch(branchId, product);

        repository.save(franchise);
        return product;
    }
}
