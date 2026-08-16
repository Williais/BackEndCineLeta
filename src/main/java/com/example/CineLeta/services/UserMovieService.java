package com.example.CineLeta.services;

import com.example.CineLeta.dtos.DashboardDTO;
import com.example.CineLeta.dtos.UserMovieResponseDTO;
import com.example.CineLeta.models.MovieCache;
import com.example.CineLeta.models.User;
import com.example.CineLeta.models.UserMovie;
import com.example.CineLeta.models.WatchSession;
import com.example.CineLeta.repositories.MovieCacheRepository;
import com.example.CineLeta.repositories.OscarWinnerRepository;
import com.example.CineLeta.repositories.UserMovieRepository;
import com.example.CineLeta.repositories.UserRepository;
import com.example.CineLeta.repositories.WatchSessionRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserMovieService {
    private final UserMovieRepository userMovieRepository;
    private final UserRepository userRepository;
    private final MovieCacheRepository movieCacheRepository;
    private final TmdbService tmdbService;
    private final WatchSessionRepository watchSessionRepository;
    private final OscarWinnerRepository oscarWinnerRepository;

    public UserMovieService(UserMovieRepository userMovieRepository, UserRepository userRepository, MovieCacheRepository movieCacheRepository, TmdbService tmdbService, WatchSessionRepository watchSessionRepository, OscarWinnerRepository oscarWinnerRepository) {
        this.userMovieRepository = userMovieRepository;
        this.userRepository = userRepository;
        this.movieCacheRepository = movieCacheRepository;
        this.tmdbService = tmdbService;
        this.watchSessionRepository = watchSessionRepository;
        this.oscarWinnerRepository = oscarWinnerRepository;
    }

    public UserMovie evaluateMovie(UUID userId, Integer tmdbId, Integer rating, Boolean isFavorite, Boolean isIgnored, List<String> taggedEmails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuario Nao Encontrado"));

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

        WatchSession session = null;
        if (taggedEmails != null && !taggedEmails.isEmpty()) {
            WatchSession newSession = new WatchSession();
            newSession.setCreator(user);
            newSession.setMovie(filmeAvaliado);
            session = watchSessionRepository.save(newSession);

            for (String email : taggedEmails) {
                Optional<User> friendOpt = userRepository.findByEmail(email);
                if (friendOpt.isPresent()) {
                    User friend = friendOpt.get();
                    if (userMovieRepository.findByUserAndMovie(friend, filmeAvaliado).isEmpty()) {
                        UserMovie friendInteraction = new UserMovie();
                        friendInteraction.setUser(friend);
                        friendInteraction.setMovie(filmeAvaliado);
                        friendInteraction.setRating(0);
                        friendInteraction.setIsFavorite(false);
                        friendInteraction.setIsIgnored(false);
                        friendInteraction.setSession(session);
                        userMovieRepository.save(friendInteraction);
                    }
                }
            }
        }

        UserMovie registroFinal = userMovieRepository.findByUserAndMovie(user, filmeAvaliado).orElse(new UserMovie());
        registroFinal.setUser(user);
        registroFinal.setMovie(filmeAvaliado);
        registroFinal.setRating(rating);
        registroFinal.setIsFavorite(isFavorite);
        registroFinal.setIsIgnored(isIgnored);
        if (session != null) {
            registroFinal.setSession(session);
        }

        return userMovieRepository.save(registroFinal);
    }

    public List<UserMovie> getUserMoviesRaw(User user) {
        return userMovieRepository.findByUser(user).stream().filter(avaliacao -> !avaliacao.getIsIgnored()).toList();
    }

    public List<UserMovieResponseDTO> getUserMovies(User user) {
        return userMovieRepository.findByUser(user).stream()
                .map(avaliacao -> new UserMovieResponseDTO(avaliacao.getMovie().getTmdbId(), avaliacao.getRating(), avaliacao.getIsIgnored()))
                .toList();
    }

    public DashboardDTO getDashboardStats(User user) {
        List<UserMovie> watched = getUserMoviesRaw(user);

        long totalMovies = watched.size();
        long totalRuntime = watched.stream().mapToLong(um -> um.getMovie().getRunTimeMinutes() != null ? um.getMovie().getRunTimeMinutes() : 0).sum();
        double averageRating = watched.stream().filter(um -> um.getRating() != null && um.getRating() > 0)
                .mapToInt(UserMovie::getRating).average().orElse(0.0);

        String favoriteGenre = watched.stream()
                .map(um -> um.getMovie().getGenres())
                .filter(Objects::nonNull)
                .flatMap(g -> Arrays.stream(g.split(",")))
                .map(String::trim)
                .filter(g -> !g.isEmpty())
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("Nenhum");

        List<DashboardDTO.DashboardMovieDTO> recentMovies = watched.stream()
                .sorted((a, b) -> b.getInteractedAt().compareTo(a.getInteractedAt()))
                .limit(10).map(this::toMovieDTO).toList();

        List<DashboardDTO.DashboardMovieDTO> lovedMovies = watched.stream()
                .filter(UserMovie::getIsFavorite)
                .sorted((a, b) -> b.getInteractedAt().compareTo(a.getInteractedAt()))
                .limit(5).map(this::toMovieDTO).toList();

        List<DashboardDTO.DashboardMovieDTO> highestRated = watched.stream()
                .filter(um -> um.getRating() != null && um.getRating() >= 4)
                .sorted((a, b) -> b.getInteractedAt().compareTo(a.getInteractedAt()))
                .limit(5).map(this::toMovieDTO).toList();

        List<DashboardDTO.DashboardMovieDTO> oscarMovies = watched.stream()
                .filter(um -> oscarWinnerRepository.existsByTmdbId(um.getMovie().getTmdbId()))
                .sorted((a, b) -> b.getInteractedAt().compareTo(a.getInteractedAt()))
                .map(this::toMovieDTO).toList();

        Map<String, Long> buddyCounts = new HashMap<>();
        List<DashboardDTO.SharedSessionDTO> sharedSessions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (UserMovie um : watched) {
            if (um.getSession() != null) {
                List<UserMovie> sessionMembers = userMovieRepository.findBySession(um.getSession());
                for (UserMovie member : sessionMembers) {
                    if (!member.getUser().getId().equals(user.getId())) {
                        String buddyName = member.getUser().getEmail().split("@")[0];
                        buddyCounts.put(buddyName, buddyCounts.getOrDefault(buddyName, 0L) + 1);

                        sharedSessions.add(new DashboardDTO.SharedSessionDTO(
                                um.getSession().getId().toString(),
                                toMovieDTO(um),
                                buddyName,
                                um.getSession().getSessionDate() != null ? um.getSession().getSessionDate().format(formatter) : ""
                        ));
                    }
                }
            }
        }

        DashboardDTO.TopBuddyDTO topBuddy = buddyCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new DashboardDTO.TopBuddyDTO(e.getKey(), e.getValue()))
                .orElse(null);

        return new DashboardDTO(totalMovies, totalRuntime, averageRating, favoriteGenre, topBuddy, recentMovies, lovedMovies, highestRated, oscarMovies, sharedSessions);
    }

    private DashboardDTO.DashboardMovieDTO toMovieDTO(UserMovie um) {
        return new DashboardDTO.DashboardMovieDTO(um.getMovie().getTmdbId(), um.getMovie().getTitle(), um.getMovie().getPosterPath(), um.getRating(), um.getIsFavorite());
    }
}