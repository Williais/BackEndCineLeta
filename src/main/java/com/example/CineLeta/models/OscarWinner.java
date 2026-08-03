package com.example.CineLeta.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "oscar_winners")
public class OscarWinner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(name = "edition_number")
    private Integer editionNumber;

    @Column(name = "award_year")
    private Integer awardYear;

    private String category;

    @Column(name = "tmdb_id")
    private Integer tmdbId;
}
