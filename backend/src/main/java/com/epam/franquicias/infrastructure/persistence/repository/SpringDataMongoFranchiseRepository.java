package com.epam.franquicias.infrastructure.persistence.repository;

import com.epam.franquicias.infrastructure.persistence.document.FranchiseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataMongoFranchiseRepository extends MongoRepository<FranchiseDocument, String> {
    boolean existsByNameIgnoreCase(String name);
    Optional<FranchiseDocument> findByNameIgnoreCase(String name);
}
