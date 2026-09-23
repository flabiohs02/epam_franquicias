package com.epam.franquicias.usecase;

import com.epam.franquicias.domain.model.Product;

public interface UpdateStockUseCase {
    Product execute(String franchiseId, String branchId, String productId, int newStock);
}
