package com.epam.franquicias.application.dto.response;

public record ProductResponse(
        String id,
        String name,
        int stock
) {}
