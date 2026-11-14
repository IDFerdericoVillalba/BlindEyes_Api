package com.cinenoir.cinenoirapi.controller;

import com.cinenoir.cinenoirapi.model.Funcion;
import com.cinenoir.cinenoirapi.service.FuncionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funciones")
public class FuncionController {

    private final FuncionService service;

    public FuncionController(FuncionService service) {
        this.service = service;
    }

    // Listar todas las funciones
    @GetMapping
    public ResponseEntity<List<Funcion>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // Buscar funcion por ID
    @GetMapping("/{id}")
    public ResponseEntity<Funcion> buscarPorId(
            @PathVariable int id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Crear funcion
    @PostMapping
    public ResponseEntity<Funcion> guardar(
            @RequestBody Funcion funcion) {

        int id = service.guardar(funcion);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.buscarPorId(id));
    }

    // Actualizar funcion
    @PutMapping("/{id}")
    public ResponseEntity<Funcion> actualizar(
            @PathVariable int id,
            @RequestBody Funcion funcion) {

        service.actualizar(id, funcion);

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Eliminar funcion
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable int id) {

        service.eliminar(id);

        return ResponseEntity.ok(
                "Funcion eliminada correctamente"
        );
    }
}
