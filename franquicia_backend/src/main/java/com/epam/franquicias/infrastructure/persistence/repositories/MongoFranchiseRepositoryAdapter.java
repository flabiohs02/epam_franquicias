package com.epam.franquicias.infrastructure.persistence.repositories;

import com.epam.franquicias.domain.model.Franchise;
import com.epam.franquicias.domain.repository.FranchiseRepository;
import com.epam.franquicias.infrastructure.persistence.entities.FranchiseDocument;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MongoFranchiseRepositoryAdapter implements FranchiseRepository {

    private final SpringDataMongoFranchiseRepository springDataRepository;

    public MongoFranchiseRepositoryAdapter(SpringDataMongoFranchiseRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Franchise save(Franchise franchise) {
        FranchiseDocument document = FranchiseMongoMapper.toDocument(franchise);
        FranchiseDocument saved = springDataRepository.save(document);
        return FranchiseMongoMapper.toDomain(saved);
    }

    @Override
    public Optional<Franchise> findById(String id) {
        return springDataRepository.findById(id)
                .map(FranchiseMongoMapper::toDomain);
    }

    @Override
    public List<Franchise> findAll() {
        return springDataRepository.findAll().stream()
                .map(FranchiseMongoMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return springDataRepository.existsByNameIgnoreCase(name);
    }
}
