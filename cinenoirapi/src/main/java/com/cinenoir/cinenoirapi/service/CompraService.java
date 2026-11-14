package com.cinenoir.cinenoirapi.service;

import com.cinenoir.cinenoirapi.model.Compra;
import com.cinenoir.cinenoirapi.model.Funcion;

import com.cinenoir.cinenoirapi.repository.CompraRepository;
import com.cinenoir.cinenoirapi.repository.FuncionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final FuncionRepository funcionRepository;

    public CompraService(
            CompraRepository compraRepository,
            FuncionRepository funcionRepository) {

        this.compraRepository = compraRepository;
        this.funcionRepository = funcionRepository;
    }

    // Listar todas las compras
    public List<Compra> listar() {
        return compraRepository.listar();
    }

    // Buscar una compra por ID
    public Compra buscarPorId(int id) {
        return compraRepository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La compra no existe"
                ));
    }

    // Registrar una nueva compra
    @Transactional
    public Compra registrar(
            Integer idFuncion,
            String nombreCliente,
            Integer cantidadBoletos) {

        // Validar datos de entrada
        if (idFuncion == null ||
                nombreCliente == null ||
                nombreCliente.isBlank() ||
                cantidadBoletos == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todos los campos son obligatorios"
            );
        }

        if (idFuncion <= 0 ||
                nombreCliente.length() > 100 ||
                cantidadBoletos <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la compra no son validos"
            );
        }

        // Buscar y bloquear la funcion
        Funcion funcion = funcionRepository.bloquearPorId(idFuncion)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La funcion no existe"
                ));

        // Verificar cupos disponibles
        if (cantidadBoletos > funcion.cuposDisponibles()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No hay cupos suficientes"
            );
        }

        // Calcular valor total
        BigDecimal total = funcion.precio().multiply(
                BigDecimal.valueOf(cantidadBoletos)
        );

        // Crear objeto de compra
        Compra compra = new Compra(
                null,
                idFuncion,
                nombreCliente.trim(),
                cantidadBoletos,
                total,
                LocalDateTime.now()
        );

        // Descontar cupos
        int filas = funcionRepository.descontarCupos(
                idFuncion, cantidadBoletos
        );

        if (filas != 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No fue posible reservar los cupos"
            );
        }

        // Guardar compra
        int idCompra = compraRepository.guardar(compra);

        // Devolver compra registrada
        return new Compra(
                idCompra,
                compra.idFuncion(),
                compra.nombreCliente(),
                compra.cantidadBoletos(),
                compra.totalPago(),
                compra.fechaCompra()
        );
    }

    // Consultar compras entre dos fechas
    public List<Compra> listarPorFechas(
            LocalDate inicio,
            LocalDate fin) {

        if (inicio == null || fin == null ||
                fin.isBefore(inicio)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El rango de fechas no es valido"
            );
        }

        return compraRepository.listarPorFechas(
                inicio.atStartOfDay(),
                fin.plusDays(1).atStartOfDay()
        );
    }
}
