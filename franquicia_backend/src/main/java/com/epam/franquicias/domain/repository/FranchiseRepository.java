package com.epam.franquicias.domain.repository;

import com.epam.franquicias.domain.model.Franchise;

import java.util.List;
import java.util.Optional;

public interface FranchiseRepository {
    Franchise save(Franchise franchise);
    Optional<Franchise> findById(String id);
    List<Franchise> findAll();
    void deleteById(String id);
    boolean existsByName(String name);
}
