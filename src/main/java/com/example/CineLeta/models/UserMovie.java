package com.example.CineLeta.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "user_movies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tmdb_id"})
})
public class UserMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tmdb_id", referencedColumnName = "tmdb_id", nullable = false)
    private MovieCache movie;

    private Integer rating;

    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    @Column(name = "is_ignored")
    private Boolean isIgnored = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private WatchSession session;

    @CreationTimestamp
    @Column(name = "interacted_at", updatable = false)
    private LocalDateTime interactedAt;
}
