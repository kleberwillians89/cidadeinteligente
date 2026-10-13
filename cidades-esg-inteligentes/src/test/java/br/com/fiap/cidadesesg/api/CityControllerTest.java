package br.com.fiap.cidadesesg.api;

import br.com.fiap.cidadesesg.service.CityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CityService service;

    @Test
    void shouldListCities() throws Exception {
        CityResponse city = new CityResponse(
                UUID.randomUUID(), "Curitiba", "PR", 1773718,
                84, 42, 64.5, 88, 82.1, Instant.parse("2026-09-09T12:00:00Z")
        );
        when(service.findAll()).thenReturn(List.of(city));

        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Curitiba"))
                .andExpect(jsonPath("$[0].esgScore").value(82.1));
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        String invalidPayload = """
                {
                  "name": "",
                  "state": "S",
                  "population": 0,
                  "renewableEnergyPercentage": 150,
                  "recyclingRatePercentage": 20,
                  "greenAreaPerInhabitant": 10,
                  "publicTransportScore": 70
                }
                """;

        mockMvc.perform(post("/api/cities")
                        .contentType("application/json")
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"));
    }
}
