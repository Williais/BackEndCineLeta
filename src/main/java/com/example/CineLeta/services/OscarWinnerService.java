package com.example.CineLeta.services;

import com.example.CineLeta.models.OscarWinner;
import com.example.CineLeta.repositories.OscarWinnerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OscarWinnerService {

    private final OscarWinnerRepository oscarWinnerRepository;
    public OscarWinnerService(OscarWinnerRepository oscarWinnerRepository) {
        this.oscarWinnerRepository = oscarWinnerRepository;
    }

    public OscarWinner getRandomMovie(String category){
        List<OscarWinner> filmes;

        if (category == null || category.isBlank()){
           filmes = oscarWinnerRepository.findAll();
        }else {
            filmes = oscarWinnerRepository.findByCategory(category);
        }

        if(filmes.isEmpty()){
            throw new IllegalArgumentException("Nenhum filme encontrado para esta categoria");
        }

        Random random = new Random();
        int  randomNumber = ThreadLocalRandom.current().nextInt(filmes.size());

        return filmes.get(randomNumber);
    }
}
