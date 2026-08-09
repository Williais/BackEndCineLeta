package com.example.CineLeta.repositories;

import com.example.CineLeta.models.MovieCache;
import com.example.CineLeta.models.User;
import com.example.CineLeta.models.UserMovie;
import com.example.CineLeta.models.WatchSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMovieRepository extends JpaRepository<UserMovie, UUID> {

    Optional<UserMovie> findByUserAndMovie(User user, MovieCache movie);
    List<UserMovie> findBySession(WatchSession session);
    List<UserMovie> findByUser(User user);
}
