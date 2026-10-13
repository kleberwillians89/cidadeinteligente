package br.com.fiap.cidadesesg.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cities")
public class City {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(nullable = false)
    private long population;

    @Column(name = "renewable_energy_percentage", nullable = false)
    private double renewableEnergyPercentage;

    @Column(name = "recycling_rate_percentage", nullable = false)
    private double recyclingRatePercentage;

    @Column(name = "green_area_per_inhabitant", nullable = false)
    private double greenAreaPerInhabitant;

    @Column(name = "public_transport_score", nullable = false)
    private double publicTransportScore;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected City() {
    }

    public City(UUID id, String name, String state, long population,
                double renewableEnergyPercentage, double recyclingRatePercentage,
                double greenAreaPerInhabitant, double publicTransportScore) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.population = population;
        this.renewableEnergyPercentage = renewableEnergyPercentage;
        this.recyclingRatePercentage = recyclingRatePercentage;
        this.greenAreaPerInhabitant = greenAreaPerInhabitant;
        this.publicTransportScore = publicTransportScore;
    }

    public void update(String name, String state, long population,
                       double renewableEnergyPercentage, double recyclingRatePercentage,
                       double greenAreaPerInhabitant, double publicTransportScore) {
        this.name = name;
        this.state = state;
        this.population = population;
        this.renewableEnergyPercentage = renewableEnergyPercentage;
        this.recyclingRatePercentage = recyclingRatePercentage;
        this.greenAreaPerInhabitant = greenAreaPerInhabitant;
        this.publicTransportScore = publicTransportScore;
    }

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getState() { return state; }
    public long getPopulation() { return population; }
    public double getRenewableEnergyPercentage() { return renewableEnergyPercentage; }
    public double getRecyclingRatePercentage() { return recyclingRatePercentage; }
    public double getGreenAreaPerInhabitant() { return greenAreaPerInhabitant; }
    public double getPublicTransportScore() { return publicTransportScore; }
    public Instant getUpdatedAt() { return updatedAt; }
}
