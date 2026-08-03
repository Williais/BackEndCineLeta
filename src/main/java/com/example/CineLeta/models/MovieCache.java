package com.example.CineLeta.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movies_cache")
@Getter
@Setter
public class MovieCache {
    @Id
    @Column(name = "tmdb_id")
    @Setter(AccessLevel.NONE)
    private Integer tmdbId;

    private String title;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "runtime_minutes")
    private Integer runTimeMinutes;

    @Column(columnDefinition = "TEXT")
    private String genres;
}
