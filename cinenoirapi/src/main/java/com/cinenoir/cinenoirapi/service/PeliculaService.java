package com.cinenoir.cinenoirapi.service;

import com.cinenoir.cinenoirapi.model.Pelicula;
import com.cinenoir.cinenoirapi.repository.PeliculaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PeliculaService {

    private final PeliculaRepository repository;

    public PeliculaService(PeliculaRepository repository) {
        this.repository = repository;
    }

    // Listar peliculas
    public List<Pelicula> listar() {
        return repository.listar();
    }

    // Buscar pelicula por ID
    public Pelicula buscarPorId(int id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La pelicula no existe"
                ));
    }

    // Validar datos de la pelicula
    private void validar(Pelicula pelicula) {
        if (pelicula == null ||
                pelicula.titulo() == null ||
                pelicula.titulo().isBlank() ||
                pelicula.genero() == null ||
                pelicula.genero().isBlank() ||
                pelicula.duracion() == null ||
                pelicula.duracion().isBlank() ||
                pelicula.clasificacion() == null ||
                pelicula.clasificacion().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todos los campos son obligatorios"
            );
        }

        if (pelicula.titulo().length() > 250 ||
                pelicula.genero().length() > 250 ||
                pelicula.duracion().length() > 10 ||
                pelicula.clasificacion().length() > 10) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uno de los campos supera la longitud permitida"
            );
        }
        // Validar que la duracion sea un numero positivo
        try {
            int minutos = Integer.parseInt(
                    pelicula.duracion()
            );

            if (minutos <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La duracion debe ser un numero de minutos mayor que cero"
            );
        }

    }

    // Crear pelicula
    public int guardar(Pelicula pelicula) {
        validar(pelicula);
        return repository.guardar(pelicula);
    }

    // Actualizar pelicula
    public void actualizar(int id, Pelicula pelicula) {
        validar(pelicula);
        buscarPorId(id);
        repository.actualizar(id, pelicula);
    }

    // Eliminar pelicula con validacion
    @Transactional
    public void eliminar(int id) {

        if (!repository.bloquearPorId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La pelicula no existe"
            );
        }

        if (repository.tieneFunciones(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una pelicula con funciones"
            );
        }

        repository.eliminar(id);
    }

}
