package br.com.fiap.cidadesesg.service;

import br.com.fiap.cidadesesg.api.CityRequest;
import br.com.fiap.cidadesesg.api.CityResponse;
import br.com.fiap.cidadesesg.domain.City;
import br.com.fiap.cidadesesg.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CityService {

    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CityResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CityResponse> ranking() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .sorted(Comparator.comparingDouble(CityResponse::esgScore).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public CityResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Transactional
    public CityResponse create(CityRequest request) {
        City city = new City(
                null,
                request.name().trim(),
                request.state().trim().toUpperCase(),
                request.population(),
                request.renewableEnergyPercentage(),
                request.recyclingRatePercentage(),
                request.greenAreaPerInhabitant(),
                request.publicTransportScore()
        );
        return toResponse(repository.save(city));
    }

    @Transactional
    public CityResponse update(UUID id, CityRequest request) {
        City city = findEntity(id);
        city.update(
                request.name().trim(),
                request.state().trim().toUpperCase(),
                request.population(),
                request.renewableEnergyPercentage(),
                request.recyclingRatePercentage(),
                request.greenAreaPerInhabitant(),
                request.publicTransportScore()
        );
        return toResponse(repository.save(city));
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    double calculateScore(City city) {
        double normalizedGreenArea = Math.min(city.getGreenAreaPerInhabitant() * 2.0, 100.0);
        double score = city.getRenewableEnergyPercentage() * 0.35
                + city.getRecyclingRatePercentage() * 0.25
                + normalizedGreenArea * 0.20
                + city.getPublicTransportScore() * 0.20;
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private City findEntity(UUID id) {
        return repository.findById(id).orElseThrow(() -> new CityNotFoundException(id));
    }

    private CityResponse toResponse(City city) {
        return CityResponse.from(city, calculateScore(city));
    }
}
