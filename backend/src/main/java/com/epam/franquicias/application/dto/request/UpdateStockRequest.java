package com.epam.franquicias.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateStockRequest(
        @NotNull(message = "El nuevo stock es obligatorio")
        @Min(value = 0, message = "El nuevo stock no puede ser negativo")
        Integer newStock
) {}
