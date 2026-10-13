CREATE TABLE cities (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    state VARCHAR(2) NOT NULL,
    population BIGINT NOT NULL CHECK (population > 0),
    renewable_energy_percentage DOUBLE PRECISION NOT NULL CHECK (renewable_energy_percentage BETWEEN 0 AND 100),
    recycling_rate_percentage DOUBLE PRECISION NOT NULL CHECK (recycling_rate_percentage BETWEEN 0 AND 100),
    green_area_per_inhabitant DOUBLE PRECISION NOT NULL CHECK (green_area_per_inhabitant >= 0),
    public_transport_score DOUBLE PRECISION NOT NULL CHECK (public_transport_score BETWEEN 0 AND 100),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_cities_state ON cities (state);
