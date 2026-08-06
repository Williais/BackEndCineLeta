package com.example.CineLeta.repositories;

import com.example.CineLeta.models.WatchSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WatchSessionRepository extends JpaRepository<WatchSession, UUID> {
}
