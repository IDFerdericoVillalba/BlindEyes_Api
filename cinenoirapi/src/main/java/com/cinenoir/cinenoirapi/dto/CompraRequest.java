package com.cinenoir.cinenoirapi.dto;

public record CompraRequest(
        Integer idFuncion,
        String nombreCliente,
        Integer cantidadBoletos
) {}
