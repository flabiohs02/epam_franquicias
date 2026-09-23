package com.epam.franquicias.usecase;

public interface DeleteProductUseCase {
    void execute(String franchiseId, String branchId, String productId);
}
