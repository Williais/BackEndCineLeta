package com.example.CineLeta.repositories;

import com.example.CineLeta.models.MovieCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCacheRepository extends JpaRepository<MovieCache, Integer> {
}
