package br.com.fiap.cidadesesg.service;

import br.com.fiap.cidadesesg.api.CityRequest;
import br.com.fiap.cidadesesg.api.CityResponse;
import br.com.fiap.cidadesesg.domain.City;
import br.com.fiap.cidadesesg.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository repository;

    @InjectMocks
    private CityService service;

    @Test
    void shouldCalculateWeightedEsgScore() {
        City city = new City(UUID.randomUUID(), "Cidade Verde", "SP", 100000, 80, 60, 40, 90);

        double score = service.calculateScore(city);

        assertThat(score).isEqualTo(77.0);
    }

    @Test
    void shouldReturnRankingFromHighestToLowestScore() {
        City first = new City(UUID.randomUUID(), "Cidade A", "SP", 100000, 95, 80, 50, 90);
        City second = new City(UUID.randomUUID(), "Cidade B", "RJ", 120000, 30, 20, 10, 40);
        when(repository.findAll()).thenReturn(List.of(second, first));

        List<CityResponse> ranking = service.ranking();

        assertThat(ranking).extracting(CityResponse::name).containsExactly("Cidade A", "Cidade B");
    }

    @Test
    void shouldCreateCityWithNormalizedState() {
        CityRequest request = new CityRequest("Cidade Nova", "sp", 50000, 70, 40, 20, 75);
        when(repository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CityResponse created = service.create(request);

        assertThat(created.state()).isEqualTo("SP");
        assertThat(created.esgScore()).isEqualTo(57.5);
    }

    @Test
    void shouldFailWhenCityDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(CityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
