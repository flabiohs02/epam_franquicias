package com.epam.franquicias.application.usecase;

import com.epam.franquicias.domain.model.Franchise;

public interface CreateFranchiseUseCase {
    Franchise execute(String franchiseName);
}
