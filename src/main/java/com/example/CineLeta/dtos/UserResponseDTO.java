package com.example.CineLeta.dtos;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String picture
) {
}
