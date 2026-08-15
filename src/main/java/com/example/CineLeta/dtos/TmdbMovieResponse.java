package com.example.CineLeta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbMovieResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("title") String title,
        @JsonProperty("overview") String overview,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("vote_average") Double voteAverage) {
}
