package com.example.CineLeta.repositories;

import com.example.CineLeta.models.OscarWinner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OscarWinnerRepository extends JpaRepository<OscarWinner, Integer> {
    List<OscarWinner> findByCategory(String category);
    List<OscarWinner> findByCategoryContainingIgnoreCase(String category);
    boolean existsByTmdbId(Integer tmdbId);
}
