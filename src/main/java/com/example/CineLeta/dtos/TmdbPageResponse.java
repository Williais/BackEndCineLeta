package com.example.CineLeta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TmdbPageResponse(
        @JsonProperty("results") List<TmdbMovieResponse> results
) {
}
