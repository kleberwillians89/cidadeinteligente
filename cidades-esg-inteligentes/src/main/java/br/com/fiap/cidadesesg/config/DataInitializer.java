package br.com.fiap.cidadesesg.config;

import br.com.fiap.cidadesesg.domain.City;
import br.com.fiap.cidadesesg.repository.CityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedCities(CityRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new City(null, "Curitiba", "PR", 1773718, 84, 42, 64.5, 88),
                        new City(null, "São Paulo", "SP", 11451999, 71, 18, 16.3, 76),
                        new City(null, "Florianópolis", "SC", 537211, 89, 34, 54.0, 73)
                ));
            }
        };
    }
}
