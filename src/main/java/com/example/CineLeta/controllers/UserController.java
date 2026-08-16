package com.example.CineLeta.controllers;

import com.example.CineLeta.dtos.UserResponseDTO;
import com.example.CineLeta.dtos.UserSummaryDTO;
import com.example.CineLeta.models.User;
import com.example.CineLeta.repositories.UserRepository;
import com.example.CineLeta.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String email = principal.getAttribute("email");
        String googleName = principal.getAttribute("name");
        String picture = principal.getAttribute("picture");

        User user = userService.getUserByEmail(email);

        String displayName = (user.getNickname() != null && !user.getNickname().isBlank())
                ? user.getNickname()
                : googleName;

        UserResponseDTO response = new UserResponseDTO(
                user.getId(),
                displayName,
                user.getEmail(),
                picture
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/nickname")
    public ResponseEntity<?> updateNickname(
            @RequestBody com.example.CineLeta.dtos.UpdateNicknameDTO dto,
            @AuthenticationPrincipal OAuth2User principal) {

        String email = principal.getAttribute("email");
        User user = userService.getUserByEmail(email);
        if (!dto.nickname().matches("^[a-zA-Z0-9]+$")) {
            return ResponseEntity.badRequest().body("O Nickname deve conter apenas letras e números sem espaços.");
        }

        user.setNickname(dto.nickname());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryDTO>> searchUsers(
            @RequestParam String query,
            @AuthenticationPrincipal OAuth2User principal) {

        String currentEmail = principal.getAttribute("email");

        var results = userRepository.searchByNicknameOrEmail(query).stream()
                .filter(u -> !u.getEmail().equals(currentEmail))
                .map(u -> new com.example.CineLeta.dtos.UserSummaryDTO(
                        u.getNickname() != null ? u.getNickname() : u.getEmail().split("@")[0],
                        u.getEmail()
                ))
                .toList();

        return ResponseEntity.ok(results);
    }
}