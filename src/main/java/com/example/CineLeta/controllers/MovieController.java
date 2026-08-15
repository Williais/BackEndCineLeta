package com.example.CineLeta.controllers;

import com.example.CineLeta.dtos.MovieDetailDTO;
import com.example.CineLeta.dtos.TmdbMovieResponse;
import com.example.CineLeta.services.TmdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final TmdbService tmdbService;

    public MovieController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/random")
    public ResponseEntity<MovieDetailDTO> getRandomMovie() {
        return ResponseEntity.ok(tmdbService.getRandomMovie());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDetailDTO> getMovieDetails(@PathVariable Long id) {
        return ResponseEntity.ok(tmdbService.getMovieDetails(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TmdbMovieResponse>> searchMovies(@RequestParam String query) {
        List<TmdbMovieResponse> movies = tmdbService.searchMovies(query);
        return ResponseEntity.ok(movies);
    }
}