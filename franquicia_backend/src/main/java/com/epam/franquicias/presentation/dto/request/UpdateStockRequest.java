package com.epam.franquicias.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateStockRequest(
        @NotNull(message = "El nuevo stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer newStock
) {}
