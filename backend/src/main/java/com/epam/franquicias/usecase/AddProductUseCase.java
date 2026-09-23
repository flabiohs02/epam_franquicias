package com.epam.franquicias.usecase;

import com.epam.franquicias.domain.model.Product;

public interface AddProductUseCase {
    Product execute(String franchiseId, String branchId, String productName, int initialStock);
}
