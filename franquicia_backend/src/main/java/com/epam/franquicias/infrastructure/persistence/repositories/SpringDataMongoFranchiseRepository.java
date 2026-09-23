package com.epam.franquicias.infrastructure.persistence.repositories;

import com.epam.franquicias.infrastructure.persistence.entities.FranchiseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataMongoFranchiseRepository extends MongoRepository<FranchiseDocument, String> {
    Optional<FranchiseDocument> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
