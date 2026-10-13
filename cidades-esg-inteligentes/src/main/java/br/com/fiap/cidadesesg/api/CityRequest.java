package br.com.fiap.cidadesesg.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CityRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(min = 2, max = 2) String state,
        @Min(1) long population,
        @DecimalMin("0.0") @DecimalMax("100.0") double renewableEnergyPercentage,
        @DecimalMin("0.0") @DecimalMax("100.0") double recyclingRatePercentage,
        @DecimalMin("0.0") double greenAreaPerInhabitant,
        @DecimalMin("0.0") @DecimalMax("100.0") double publicTransportScore
) {
}
