package com.example.CineLeta.controllers;

import com.example.CineLeta.dtos.MovieDetailDTO;
import com.example.CineLeta.services.OscarWinnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oscar")
public class OscarWinnerController {
    private final OscarWinnerService oscarWinnerService;
    public OscarWinnerController(OscarWinnerService oscarWinnerService) {
        this.oscarWinnerService = oscarWinnerService;
    }

    @GetMapping("/random")
    public ResponseEntity<MovieDetailDTO> getRandomMovie(@RequestParam(required = false) String category) {
        MovieDetailDTO resultado = oscarWinnerService.getRandomMovie(category);

        return ResponseEntity.ok(resultado);
    }
}
