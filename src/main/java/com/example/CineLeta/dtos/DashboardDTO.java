package com.example.CineLeta.dtos;

import java.util.List;

public record DashboardDTO(
        long totalMovies,
        long totalRuntime,
        double averageRating,
        String favoriteGenre,
        TopBuddyDTO topBuddy,
        List<DashboardMovieDTO> recentMovies,
        List<DashboardMovieDTO> lovedMovies,
        List<DashboardMovieDTO> highestRated,
        List<DashboardMovieDTO> oscarMovies,
        List<SharedSessionDTO> sharedSessions
) {
    public record DashboardMovieDTO(Integer tmdbId, String title, String posterPath, Integer rating, Boolean isFavorite) {}
    public record TopBuddyDTO(String name, long count) {}
    public record SharedSessionDTO(String id, DashboardMovieDTO movie, String buddyName, String date) {}
}