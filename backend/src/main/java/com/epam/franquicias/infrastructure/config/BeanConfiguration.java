package com.epam.franquicias.infrastructure.config;

import com.epam.franquicias.domain.repository.FranchiseRepository;
import com.epam.franquicias.usecase.*;
import com.epam.franquicias.usecase.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(FranchiseRepository repository) {
        return new CreateFranchiseServiceImpl(repository);
    }

    @Bean
    public AddBranchUseCase addBranchUseCase(FranchiseRepository repository) {
        return new AddBranchServiceImpl(repository);
    }

    @Bean
    public AddProductUseCase addProductUseCase(FranchiseRepository repository) {
        return new AddProductServiceImpl(repository);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(FranchiseRepository repository) {
        return new DeleteProductServiceImpl(repository);
    }

    @Bean
    public UpdateStockUseCase updateStockUseCase(FranchiseRepository repository) {
        return new UpdateStockServiceImpl(repository);
    }

    @Bean
    public GetTopStockProductsUseCase getTopStockProductsUseCase(FranchiseRepository repository) {
        return new GetTopStockProductsServiceImpl(repository);
    }

    @Bean
    public GetFranchiseUseCase getFranchiseUseCase(FranchiseRepository repository) {
        return new GetFranchiseServiceImpl(repository);
    }

    @Bean
    public UpdateNamesUseCase updateNamesUseCase(FranchiseRepository repository) {
        return new UpdateNamesServiceImpl(repository);
    }
}
