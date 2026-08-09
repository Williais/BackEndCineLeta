package com.example.CineLeta.controllers;

import com.example.CineLeta.dtos.EvaluationRequestDTO;
import com.example.CineLeta.dtos.UserMovieResponseDTO;
import com.example.CineLeta.models.User;
import com.example.CineLeta.models.UserMovie;
import com.example.CineLeta.services.UserMovieService;
import com.example.CineLeta.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usermovies")
public class UserMovieController {

    private final UserService userService;
    private final UserMovieService userMovieService;
    public UserMovieController(UserMovieService userMovieService,  UserService userService) {
        this.userService = userService;
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

    @GetMapping("/my-list")
    public ResponseEntity<List<UserMovieResponseDTO>> getMyMovies(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String email = principal.getAttribute("email");

        User usuarioLogado = userService.getUserByEmail(email);
        List<UserMovieResponseDTO> minhaLista = userMovieService.getUserMovies(usuarioLogado);

        return ResponseEntity.ok(minhaLista);
    }
}
