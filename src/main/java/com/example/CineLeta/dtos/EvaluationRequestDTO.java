package com.example.CineLeta.dtos;

public record EvaluationRequestDTO(
        Integer tmdbId,
        Integer rating,
        Boolean isFavorite,
        Boolean isIgnored
){
}
