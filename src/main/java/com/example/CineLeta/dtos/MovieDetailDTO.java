package com.example.CineLeta.dtos;

public record MovieDetailDTO(
        Long tmdbId,
        String title,
        String overview,
        String posterUrl,
        String releaseYear,
        Double tmdbRating,
        String director,
        Integer runtime,
        String genre
) {}