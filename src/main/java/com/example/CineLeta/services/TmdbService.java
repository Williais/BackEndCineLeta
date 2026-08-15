package com.example.CineLeta.services;

import com.example.CineLeta.dtos.MovieDetailDTO;
import com.example.CineLeta.dtos.TmdbDetailedResponse;
import com.example.CineLeta.dtos.TmdbMovieResponse;
import com.example.CineLeta.dtos.TmdbPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Random;
@Service
public class TmdbService {
    private final RestClient tmdbClient;
    private final Random random = new Random();

    public TmdbService(RestClient tmdbRestClient) {
        this.tmdbClient = tmdbRestClient;
    }

    public MovieDetailDTO getRandomMovie() {

        int randomPage = random.nextInt(50) + 1;

        TmdbPageResponse response = tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("language", "pt-BR")
                        .queryParam("sort_by", "popularity.desc")
                        .queryParam("page", randomPage)
                        .build())
                .retrieve()
                .body(TmdbPageResponse.class);

        if (response == null || response.results() == null || response.results().isEmpty()) {
            throw new RuntimeException("Nenhum filme encontrado na roleta.");
        }

        List<TmdbMovieResponse> movies = response.results();
        int randomIndex = random.nextInt(movies.size());


        Long winnerId = movies.get(randomIndex).id();
        return getMovieDetails(winnerId);
    }

    public List<TmdbMovieResponse> searchMovies(String query) {
        TmdbPageResponse response = tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("language", "pt-BR")
                        .queryParam("query", query)
                        .queryParam("page", 1)
                        .build())
                .retrieve()
                .body(TmdbPageResponse.class);

        if (response == null || response.results() == null) {
            return List.of();
        }

        return response.results();
    }

    public MovieDetailDTO getMovieDetails(Long tmdbId) {

        TmdbDetailedResponse response = tmdbClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tmdbId)
                        .queryParam("language", "pt-BR")
                        .queryParam("append_to_response", "credits")
                        .build())
                .retrieve()
                .body(TmdbDetailedResponse.class);

        if (response == null) throw new RuntimeException("Filme não encontrado");

        String director = "Desconhecido";
        if (response.credits() != null && response.credits().crew() != null) {
            director = response.credits().crew().stream()
                    .filter(c -> "Director".equals(c.job()))
                    .map(TmdbDetailedResponse.Crew::name)
                    .findFirst()
                    .orElse("Desconhecido");
        }

        String genre = "Cinema";
        if (response.genres() != null && !response.genres().isEmpty()) {
            genre = response.genres().get(0).name();
        }

        String year = (response.releaseDate() != null && response.releaseDate().length() >= 4)
                ? response.releaseDate().substring(0, 4) : "N/A";
        String posterUrl = response.posterPath() != null
                ? "https://image.tmdb.org/t/p/w500" + response.posterPath()
                : "https://via.placeholder.com/500x750?text=Sem+Imagem";

        return new MovieDetailDTO(
                response.id(), response.title(), response.overview(), posterUrl,
                year, response.voteAverage(), director,
                response.runtime() != null ? response.runtime() : 0, genre
        );
    }
}
