package com.example.CineLeta.dtos;

import java.util.UUID;

public record EvaluationRequestDTO(
        UUID userId,
        Integer tmdbId,
        Integer rating,
        Boolean isIgnored
){
}
