package com.cinenoir.cinenoirapi.service;

import com.cinenoir.cinenoirapi.model.Funcion;
import com.cinenoir.cinenoirapi.repository.FuncionRepository;
import com.cinenoir.cinenoirapi.repository.PeliculaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FuncionService {

    private final FuncionRepository repository;
    private final PeliculaRepository peliculaRepository;

    public FuncionService(
            FuncionRepository repository,
            PeliculaRepository peliculaRepository) {

        this.repository = repository;
        this.peliculaRepository = peliculaRepository;
    }

    // Listar funciones
    public List<Funcion> listar() {
        return repository.listar();
    }

    // Buscar funcion por ID
    public Funcion buscarPorId(int id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La funcion no existe"
                ));
    }

    // Validar los datos de una funcion
    private void validar(Funcion funcion) {

        if (funcion == null ||
                funcion.idPelicula() == null ||
                funcion.fecha() == null ||
                funcion.hora() == null ||
                funcion.sala() == null ||
                funcion.sala().isBlank() ||
                funcion.precio() == null ||
                funcion.cuposDisponibles() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todos los campos son obligatorios"
            );
        }

        if (funcion.sala().length() > 10 ||
                funcion.precio().precision() -
                        funcion.precio().scale() > 8 ||
                funcion.precio().scale() > 2) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La sala o el precio tienen un formato invalido"
            );
        }

        if (funcion.precio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio debe ser mayor que cero"
            );
        }

        if (funcion.cuposDisponibles() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los cupos no pueden ser negativos"
            );
        }

        if (peliculaRepository.buscarPorId(
                funcion.idPelicula()).isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La pelicula seleccionada no existe"
            );
        }
    }

    // Crear funcion
    public int guardar(Funcion funcion) {
        validar(funcion);
        return repository.guardar(funcion);
    }

    // Actualizar funcion
    public void actualizar(int id, Funcion funcion) {
        validar(funcion);
        buscarPorId(id);
        repository.actualizar(id, funcion);
    }

    // Eliminar funcion con validacion
    @Transactional
    public void eliminar(int id) {

        if (repository.bloquearPorId(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La funcion no existe"
            );
        }

        if (repository.tieneCompras(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una funcion con compras registradas"
            );
        }

        repository.eliminar(id);
    }
}
