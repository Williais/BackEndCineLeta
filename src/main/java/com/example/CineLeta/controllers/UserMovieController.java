package com.example.CineLeta.controllers;

import com.example.CineLeta.dtos.DashboardDTO;
import com.example.CineLeta.dtos.EvaluationRequestDTO;
import com.example.CineLeta.models.User;
import com.example.CineLeta.models.UserMovie;
import com.example.CineLeta.repositories.UserRepository;
import com.example.CineLeta.services.UserMovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-movies")
public class UserMovieController {

    private final UserMovieService userMovieService;
    private final UserRepository userRepository;

    public UserMovieController(UserMovieService userMovieService, UserRepository userRepository) {
        this.userMovieService = userMovieService;
        this.userRepository = userRepository;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<UserMovie> saveInteraction(
            @RequestBody EvaluationRequestDTO dto,
            @AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado na base de dados."));

        UserMovie savedInteraction = userMovieService.evaluateMovie(
                user.getId(),
                dto.tmdbId(),
                dto.rating(),
                dto.isFavorite(),
                dto.isIgnored(),
                dto.taggedEmails()
        );

        return ResponseEntity.ok(savedInteraction);
    }

    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> removeInteraction(
            @PathVariable Integer tmdbId,
            @AuthenticationPrincipal OAuth2User principal) {

        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        userMovieService.deleteUserMovie(user, tmdbId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return ResponseEntity.ok(userMovieService.getDashboardStats(user));
    }
}