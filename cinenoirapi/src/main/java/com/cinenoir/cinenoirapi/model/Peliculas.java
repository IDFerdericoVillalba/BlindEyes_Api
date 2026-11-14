package com.cinenoir.cinenoirapi.model;

public record Pelicula(
        Integer id,
        String titulo,
        String genero,
        String duracion,
        String clasificacion
) {}
