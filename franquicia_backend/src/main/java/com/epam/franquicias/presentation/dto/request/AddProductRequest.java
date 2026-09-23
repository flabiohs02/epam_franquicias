package com.epam.franquicias.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddProductRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre del producto debe tener entre 2 y 100 caracteres")
        String name,

        @NotNull(message = "El stock inicial es obligatorio")
        @Min(value = 0, message = "El stock inicial no puede ser negativo")
        Integer stock
) {}
