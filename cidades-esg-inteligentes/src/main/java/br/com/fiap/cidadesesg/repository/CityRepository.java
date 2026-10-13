package br.com.fiap.cidadesesg.repository;

import br.com.fiap.cidadesesg.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {
}
