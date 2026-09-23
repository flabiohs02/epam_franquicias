package com.epam.franquicias.infrastructure.persistence;

import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.repository.FranchiseRepository;
import com.epam.franquicias.infrastructure.persistence.document.FranchiseDocument;
import com.epam.franquicias.infrastructure.persistence.mapper.FranchiseMongoMapper;
import com.epam.franquicias.infrastructure.persistence.repository.SpringDataMongoFranchiseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MongoFranchiseRepositoryAdapter implements FranchiseRepository {

    private final SpringDataMongoFranchiseRepository mongoRepository;

    public MongoFranchiseRepositoryAdapter(SpringDataMongoFranchiseRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Franchise save(Franchise franchise) {
        FranchiseDocument document = FranchiseMongoMapper.toDocument(franchise);
        FranchiseDocument saved = mongoRepository.save(document);
        return FranchiseMongoMapper.toDomain(saved);
    }

    @Override
    public Optional<Franchise> findById(String id) {
        return mongoRepository.findById(id)
                .map(FranchiseMongoMapper::toDomain);
    }

    @Override
    public List<Franchise> findAll() {
        return mongoRepository.findAll().stream()
                .map(FranchiseMongoMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return mongoRepository.existsByNameIgnoreCase(name);
    }
}
