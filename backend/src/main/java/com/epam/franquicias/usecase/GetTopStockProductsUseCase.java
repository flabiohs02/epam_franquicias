package com.epam.franquicias.usecase;

import com.epam.franquicias.domain.model.BranchTopProduct;

import java.util.List;

public interface GetTopStockProductsUseCase {
    List<BranchTopProduct> execute(String franchiseId);
}
