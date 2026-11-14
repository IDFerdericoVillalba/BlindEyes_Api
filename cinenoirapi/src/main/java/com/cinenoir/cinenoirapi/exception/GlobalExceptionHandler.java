package com.cinenoir.cinenoirapi.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Construir la respuesta de error
    private ResponseEntity<Map<String, Object>> respuesta(
            HttpStatus estado,
            String mensaje,
            HttpServletRequest request) {

        Map<String, Object> error = new LinkedHashMap<>();

        error.put("fecha", LocalDateTime.now());
        error.put("estado", estado.value());
        error.put("error", estado.getReasonPhrase());
        error.put("mensaje", mensaje);
        error.put("ruta", request.getRequestURI());

        return ResponseEntity.status(estado).body(error);
    }

    // Errores generados por los servicios
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarEstado(
            ResponseStatusException ex,
            HttpServletRequest request) {

        HttpStatus estado = HttpStatus.resolve(
                ex.getStatusCode().value()
        );

        if (estado == null) {
            estado = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String mensaje = ex.getReason() != null
                ? ex.getReason()
                : "No se pudo completar la solicitud";

        return respuesta(estado, mensaje, request);
    }

    // JSON invalido o datos incompatibles
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return respuesta(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud contiene datos invalidos",
                request
        );
    }

    // Parametros con tipos incorrectos
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarTipo(
            TypeMismatchException ex,
            HttpServletRequest request) {

        return respuesta(
                HttpStatus.BAD_REQUEST,
                "Uno de los parametros tiene un formato incorrecto",
                request
        );
    }

    // Conflictos con restricciones de PostgreSQL
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        return respuesta(
                HttpStatus.CONFLICT,
                "La operacion incumple una restriccion de la base de datos",
                request
        );
    }

    // Otros errores de acceso a la base de datos
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> manejarBaseDatos(
            DataAccessException ex,
            HttpServletRequest request) {

        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrio un error al acceder a la base de datos",
                request
        );
    }
}
