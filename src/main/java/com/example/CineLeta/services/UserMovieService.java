package com.example.CineLeta.services;

import com.example.CineLeta.models.MovieCache;
import com.example.CineLeta.models.User;
import com.example.CineLeta.models.UserMovie;
import com.example.CineLeta.repositories.MovieCacheRepository;
import com.example.CineLeta.repositories.UserMovieRepository;
import com.example.CineLeta.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserMovieService {
    private final UserMovieRepository userMovieRepository;
    private final UserRepository userRepository;
    private final MovieCacheRepository movieCacheRepository;

    public UserMovieService(UserMovieRepository userMovieRepository, UserRepository userRepository, MovieCacheRepository movieCacheRepository) {
        this.userMovieRepository = userMovieRepository;
        this.userRepository = userRepository;
        this.movieCacheRepository = movieCacheRepository;
    }

    public UserMovie evaluateMovie(UUID userId, Integer tmdbId, Integer rating, Boolean isIgnored) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
           throw new  IllegalArgumentException("Usuario Nao Encontrado");
        }

        User user = userOptional.get();

        Optional<MovieCache>  movieCache = movieCacheRepository.findById(tmdbId);
        MovieCache filmeAvaliado;

        if (movieCache.isEmpty()) {
            MovieCache novoFilme = new MovieCache();
            novoFilme.setTmdbId(tmdbId);
            filmeAvaliado = movieCacheRepository.save(novoFilme);
        } else {
            filmeAvaliado = movieCache.get();
        }

        Optional<UserMovie> avaliacaoExistente = userMovieRepository.findByUserAndMovie(user, filmeAvaliado);

        UserMovie registroFinal;

        if (avaliacaoExistente.isPresent()) {
            avaliacaoExistente.get().setRating(rating);
            avaliacaoExistente.get().setIsIgnored(isIgnored);

            registroFinal = avaliacaoExistente.get();

        }else {

            UserMovie userMovieResult = new UserMovie();
            userMovieResult.setRating(rating);
            userMovieResult.setIsIgnored(isIgnored);
            userMovieResult.setUser(user);
            userMovieResult.setMovie(filmeAvaliado);

            registroFinal = userMovieResult;
        }

        userMovieRepository.save(registroFinal);
        return registroFinal;
    }


}
