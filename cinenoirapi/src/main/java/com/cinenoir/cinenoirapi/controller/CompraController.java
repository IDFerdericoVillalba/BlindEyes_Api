package com.cinenoir.cinenoirapi.controller;

import com.cinenoir.cinenoirapi.dto.CompraRequest;
import com.cinenoir.cinenoirapi.model.Compra;
import com.cinenoir.cinenoirapi.service.CompraService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService service;

    public CompraController(CompraService service) {
        this.service = service;
    }

    // Listar compras o filtrar por fechas
    @GetMapping
    public ResponseEntity<List<Compra>> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin) {

        if (inicio == null && fin == null) {
            return ResponseEntity.ok(service.listar());
        }

        if (inicio == null || fin == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes enviar ambas fechas"
            );
        }

        return ResponseEntity.ok(
                service.listarPorFechas(inicio, fin)
        );
    }

    // Buscar compra por ID
    @GetMapping("/{id}")
    public ResponseEntity<Compra> buscarPorId(
            @PathVariable int id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Registrar compra
    @PostMapping
    public ResponseEntity<Compra> registrar(
            @RequestBody CompraRequest request) {

        Compra compra = service.registrar(
                request.idFuncion(),
                request.nombreCliente(),
                request.cantidadBoletos()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(compra);
    }
}
