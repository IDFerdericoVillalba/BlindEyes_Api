package com.cinenoir.cinenoirapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record Funcion(
        Integer id,
        Integer idPelicula,
        LocalDate fecha,
        LocalTime hora,
        String sala,
        BigDecimal precio,
        Integer cuposDisponibles
) {}
