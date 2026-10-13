package br.com.fiap.cidadesesg.service;

import java.util.UUID;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(UUID id) {
        super("Cidade não encontrada: " + id);
    }
}
