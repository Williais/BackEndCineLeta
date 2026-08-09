package com.example.CineLeta.dtos;

public record UserMovieResponseDTO(
        Integer tmdbId,
        Integer rating,
        Boolean isIgnored) {
}
