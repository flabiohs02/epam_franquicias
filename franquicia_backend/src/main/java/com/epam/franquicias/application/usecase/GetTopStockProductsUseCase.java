package com.epam.franquicias.application.usecase;

import com.epam.franquicias.domain.model.BranchTopProduct;

import java.util.List;

public interface GetTopStockProductsUseCase {
    List<BranchTopProduct> execute(String franchiseId);
}
