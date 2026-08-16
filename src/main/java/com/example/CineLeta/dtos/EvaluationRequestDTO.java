package com.example.CineLeta.dtos;

import java.util.List;

public record EvaluationRequestDTO(
        Integer tmdbId,
        Integer rating,
        Boolean isFavorite,
        Boolean isIgnored,
        List<String>taggedEmails
){
}
