package com.example.CineLeta.seeders;

import com.example.CineLeta.models.OscarWinner;
import com.example.CineLeta.repositories.OscarWinnerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class OscarDataSeeder implements CommandLineRunner {

    private final OscarWinnerRepository oscarWinnerRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${TMDB_KEY}")
    private String tmdbApiKey;

    public OscarDataSeeder(OscarWinnerRepository oscarWinnerRepository) {
        this.oscarWinnerRepository = oscarWinnerRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run(String... args) throws Exception {

        if (oscarWinnerRepository.count() > 0) {
            System.out.println("[SEEDER] A tabela oscar_winners já possui dados. Execução ignorada.");
            return;
        }

        System.out.println("[SEEDER] Iniciando a importação e filtragem avançada do dataset do Oscar...");

        ClassPathResource resource = new ClassPathResource("the_oscar_award.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length < 8) continue;


                String isWinner = data[7].trim().replace("\"", "");
                if (!isWinner.equalsIgnoreCase("True")) {
                    continue;
                }

                String canonCategory = data[4].trim().replace("\"", "").toUpperCase();

                boolean isCategoriaDesejada =
                        canonCategory.contains("PICTURE") ||
                                canonCategory.contains("DIRECTING") ||
                                canonCategory.contains("CINEMATOGRAPHY") ||
                                canonCategory.contains("INTERNATIONAL") || canonCategory.contains("FOREIGN") ||
                                canonCategory.contains("SCORE") ||
                                canonCategory.contains("ANIMATED") ||
                                canonCategory.contains("VISUAL EFFECTS") ||
                                canonCategory.contains("ACTOR IN A LEADING") ||
                                canonCategory.contains("ACTRESS IN A LEADING") ||
                                canonCategory.contains("SCREENPLAY") ||
                                canonCategory.contains("EDITING");

                if (!isCategoriaDesejada) continue;

                Integer awardYear = Integer.parseInt(data[1].trim().replace("\"", ""));
                Integer editionNumber = Integer.parseInt(data[2].trim().replace("\"", ""));
                String filmName = data[6].trim().replace("\"", "");

                System.out.println("[SEEDER] Processando Vencedor: " + canonCategory + " | Filme: " + filmName + " (" + awardYear + ")");

                Integer tmdbId = fetchTmdbId(filmName, awardYear);

                if (tmdbId != null) {
                    OscarWinner winner = new OscarWinner();
                    winner.setAwardYear(awardYear);
                    winner.setEditionNumber(editionNumber);
                    winner.setCategory(canonCategory);
                    winner.setTmdbId(tmdbId);

                    oscarWinnerRepository.save(winner);
                } else {
                    System.err.println("[SEEDER - API AVISO] Não encontrou TMDB ID para: " + filmName);
                }

                Thread.sleep(150);
            }
            System.out.println("[SEEDER] Importação exata finalizada com sucesso!");
        }
    }

    private Integer fetchTmdbId(String filmName, Integer year) {
        try {
            String url = String.format(
                    "https://api.themoviedb.org/3/search/movie?api_key=%s&query=%s&year=%d",
                    tmdbApiKey, filmName.replace(" ", "+").replace("&", "and"), year
            );

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("results");

            if (results.isArray() && !results.isEmpty()) {
                return results.get(0).path("id").asInt();
            }
        } catch (Exception e) {
            System.err.println("Erro API no filme: " + filmName + " - " + e.getMessage());
        }
        return null;
    }
}