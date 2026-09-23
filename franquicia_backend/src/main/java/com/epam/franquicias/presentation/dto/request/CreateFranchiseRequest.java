package com.epam.franquicias.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFranchiseRequest(
        @NotBlank(message = "El nombre de la franquicia es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre de la franquicia debe tener entre 2 y 100 caracteres")
        String name
) {}
