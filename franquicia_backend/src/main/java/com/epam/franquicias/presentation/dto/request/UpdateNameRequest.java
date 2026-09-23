package com.epam.franquicias.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNameRequest(
        @NotBlank(message = "El nuevo nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nuevo nombre debe tener entre 2 y 100 caracteres")
        String newName
) {}
