package com.epam.franquicias.application.usecase;

import com.epam.franquicias.domain.model.Franchise;

import java.util.List;

public interface GetFranchiseUseCase {
    List<Franchise> getAll();
    Franchise getById(String franchiseId);
}
