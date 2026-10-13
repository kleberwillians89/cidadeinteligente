package br.com.fiap.cidadesesg.api;

import br.com.fiap.cidadesesg.domain.City;

import java.time.Instant;
import java.util.UUID;

public record CityResponse(
        UUID id,
        String name,
        String state,
        long population,
        double renewableEnergyPercentage,
        double recyclingRatePercentage,
        double greenAreaPerInhabitant,
        double publicTransportScore,
        double esgScore,
        Instant updatedAt
) {
    public static CityResponse from(City city, double esgScore) {
        return new CityResponse(
                city.getId(),
                city.getName(),
                city.getState(),
                city.getPopulation(),
                city.getRenewableEnergyPercentage(),
                city.getRecyclingRatePercentage(),
                city.getGreenAreaPerInhabitant(),
                city.getPublicTransportScore(),
                esgScore,
                city.getUpdatedAt()
        );
    }
}
