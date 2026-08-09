package com.example.CineLeta.controllers;

import com.example.CineLeta.dtos.EvaluationRequestDTO;
import com.example.CineLeta.models.UserMovie;
import com.example.CineLeta.services.UserMovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/usermovies")
public class UserMovieController {
    private final UserMovieService userMovieService;
    public UserMovieController(UserMovieService userMovieService) {
        this.userMovieService = userMovieService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<UserMovie> evaluate(@RequestBody EvaluationRequestDTO request) {
        Boolean isIgnored = request.isIgnored();
        Integer rating = request.rating();
        Integer tmdbId = request.tmdbId();
        UUID userId = request.userId();

        UserMovie resposta = userMovieService.evaluateMovie(userId, tmdbId, rating, isIgnored);

        return ResponseEntity.ok(resposta);
    }
}
