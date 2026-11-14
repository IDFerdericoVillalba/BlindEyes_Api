package com.cinenoir.cinenoirapi.controller;

import com.cinenoir.cinenoirapi.model.Pelicula;
import com.cinenoir.cinenoirapi.service.PeliculaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    private final PeliculaService service;

    public PeliculaController(PeliculaService service) {
        this.service = service;
    }

    // Listar todas las peliculas
    @GetMapping
    public ResponseEntity<List<Pelicula>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // Buscar pelicula por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> buscarPorId(
            @PathVariable int id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Crear pelicula
    @PostMapping
    public ResponseEntity<Pelicula> guardar(
            @RequestBody Pelicula pelicula) {

        int id = service.guardar(pelicula);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.buscarPorId(id));
    }

    // Actualizar pelicula
    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> actualizar(
            @PathVariable int id,
            @RequestBody Pelicula pelicula) {

        service.actualizar(id, pelicula);

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Eliminar pelicula
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable int id) {

        service.eliminar(id);

        return ResponseEntity.ok(
                "Pelicula eliminada correctamente"
        );
    }
}
