package com.cinenoir.cinenoirapi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Compra(
        Integer id,
        Integer idFuncion,
        String nombreCliente,
        Integer cantidadBoletos,
        BigDecimal totalPago,
        LocalDateTime fechaCompra
) {}
