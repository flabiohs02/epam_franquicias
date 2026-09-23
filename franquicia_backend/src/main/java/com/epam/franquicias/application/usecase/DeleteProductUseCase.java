package com.epam.franquicias.application.usecase;

public interface DeleteProductUseCase {
    void execute(String franchiseId, String branchId, String productId);
}
