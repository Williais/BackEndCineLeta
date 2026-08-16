package com.example.CineLeta.services;

import com.example.CineLeta.dtos.UserMovieResponseDTO;
import com.example.CineLeta.models.MovieCache;
import com.example.CineLeta.models.User;
import com.example.CineLeta.models.UserMovie;
import com.example.CineLeta.repositories.MovieCacheRepository;
import com.example.CineLeta.repositories.UserMovieRepository;
import com.example.CineLeta.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserMovieService {
    private final UserMovieRepository userMovieRepository;
    private final UserRepository userRepository;
    private final MovieCacheRepository movieCacheRepository;
    private final TmdbService tmdbService;

    public UserMovieService( TmdbService tmdbService, UserMovieRepository userMovieRepository, UserRepository userRepository, MovieCacheRepository movieCacheRepository) {
        this.userMovieRepository = userMovieRepository;
        this.userRepository = userRepository;
        this.movieCacheRepository = movieCacheRepository;
        this.tmdbService = tmdbService;
    }

    public UserMovie evaluateMovie(UUID userId, Integer tmdbId, Integer rating, Boolean isFavorite, Boolean isIgnored) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario Nao Encontrado"));

        MovieCache filmeAvaliado = movieCacheRepository.findById(tmdbId).orElseGet(() -> {

            var tmdbData = tmdbService.getMovieDetails(tmdbId.longValue());
            MovieCache novoFilme = new MovieCache();
            novoFilme.setTmdbId(tmdbId);
            novoFilme.setTitle(tmdbData.title());
            novoFilme.setPosterPath(tmdbData.posterUrl());
            novoFilme.setRunTimeMinutes(tmdbData.runtime());
            novoFilme.setGenres(tmdbData.genre());
            return movieCacheRepository.save(novoFilme);
        });

        UserMovie registroFinal = userMovieRepository.findByUserAndMovie(user, filmeAvaliado)
                .orElse(new UserMovie());

        registroFinal.setUser(user);
        registroFinal.setMovie(filmeAvaliado);
        registroFinal.setRating(rating);
        registroFinal.setIsFavorite(isFavorite);
        registroFinal.setIsIgnored(isIgnored);

        return userMovieRepository.save(registroFinal);
    }

    public List<UserMovieResponseDTO> getUserMovies(User user) {
        List<UserMovie> avaliacoes = userMovieRepository.findByUser(user);

        return avaliacoes.stream()
                .map(avaliacao -> new UserMovieResponseDTO(
                        avaliacao.getMovie().getTmdbId(),
                        avaliacao.getRating(),
                        avaliacao.getIsIgnored()
                ))
                .toList();
    }
}
