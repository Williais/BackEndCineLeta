package com.example.CineLeta.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TmdbDetailedResponse(
        Long id,
        String title,
        String overview,
        @JsonProperty("poster_path") String posterPath,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("vote_average") Double voteAverage,
        Integer runtime,
        List<Genre> genres,
        Credits credits
) {

    public record Genre(String name) {}
    public record Credits(List<Crew> crew) {}
    public record Crew(String job, String name) {}
}