package com.example.CineLeta.services;

import com.example.CineLeta.dtos.MovieDetailDTO;
import com.example.CineLeta.models.OscarWinner;
import com.example.CineLeta.repositories.OscarWinnerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OscarWinnerService {

    private final OscarWinnerRepository oscarWinnerRepository;
    private final TmdbService tmdbService;

    public OscarWinnerService(OscarWinnerRepository oscarWinnerRepository, TmdbService tmdbService) {
        this.oscarWinnerRepository = oscarWinnerRepository;
        this.tmdbService = tmdbService;
    }



    public MovieDetailDTO getRandomMovie(String category) {
        List<OscarWinner> filmes;

        if (category == null || category.isBlank() || category.equals("ALL")) {
            filmes = oscarWinnerRepository.findAll();
        } else {
            filmes = oscarWinnerRepository.findByCategoryContainingIgnoreCase(category);
        }

        if(filmes.isEmpty()) {
            throw new IllegalArgumentException("Nenhum filme encontrado para esta categoria");
        }

        int randomNumber = ThreadLocalRandom.current().nextInt(filmes.size());
        OscarWinner winner = filmes.get(randomNumber);

        return tmdbService.getMovieDetails(winner.getTmdbId().longValue());
    }
}
