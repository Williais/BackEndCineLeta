package com.example.CineLeta.controllers;

import com.example.CineLeta.dtos.UserResponseDTO;
import com.example.CineLeta.models.User;
import com.example.CineLeta.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String email = principal.getAttribute("email");
        User user = userService.getUserByEmail(email);

        UserResponseDTO response = new UserResponseDTO(
                user.getId(),
                user.getNickname(),
                user.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}