package com.cinenoir.cinenoirapi.repository;

import com.cinenoir.cinenoirapi.model.Funcion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FuncionRepository {

    private final JdbcTemplate jdbc;

    public FuncionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Convertir los resultados SQL en objetos Funcion
    private final org.springframework.jdbc.core.RowMapper<Funcion> mapper =
            (rs, rowNum) -> new Funcion(
                    rs.getInt("id"),
                    rs.getObject("id_pelicula", Integer.class),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getTime("hora").toLocalTime(),
                    rs.getString("sala"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("cupos_disponibles")
            );

    // Listar funciones
    public List<Funcion> listar() {
        String sql = "SELECT * FROM funciones ORDER BY id";
        return jdbc.query(sql, mapper);
    }

    // Buscar funcion por ID
    public Optional<Funcion> buscarPorId(int id) {
        String sql = "SELECT * FROM funciones WHERE id = ?";
        return jdbc.query(sql, mapper, id)
                .stream().findFirst();
    }

    // Crear funcion
    public int guardar(Funcion funcion) {
        String sql = """
            INSERT INTO funciones
            (id_pelicula, fecha, hora, sala, precio,
             cupos_disponibles)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        return jdbc.queryForObject(
                sql,
                Integer.class,
                funcion.idPelicula(),
                funcion.fecha(),
                funcion.hora(),
                funcion.sala(),
                funcion.precio(),
                funcion.cuposDisponibles()
        );
    }

    // Actualizar funcion
    public int actualizar(int id, Funcion funcion) {
        String sql = """
            UPDATE funciones
            SET id_pelicula = ?, fecha = ?, hora = ?,
                sala = ?, precio = ?, cupos_disponibles = ?
            WHERE id = ?
            """;

        return jdbc.update(
                sql,
                funcion.idPelicula(),
                funcion.fecha(),
                funcion.hora(),
                funcion.sala(),
                funcion.precio(),
                funcion.cuposDisponibles(),
                id
        );
    }

    // Eliminar funcion
    public int eliminar(int id) {
        String sql = "DELETE FROM funciones WHERE id = ?";
        return jdbc.update(sql, id);
    }

    // Bloquear una funcion durante la compra
    public Optional<Funcion> bloquearPorId(int id) {
        String sql = """
            SELECT * FROM funciones
            WHERE id = ?
            FOR UPDATE
            """;

        return jdbc.query(sql, mapper, id)
                .stream().findFirst();
    }

    // Descontar cupos disponibles
    public int descontarCupos(int id, int cantidad) {
        String sql = """
            UPDATE funciones
            SET cupos_disponibles = cupos_disponibles - ?
            WHERE id = ?
            AND cupos_disponibles >= ?
            """;

        return jdbc.update(sql, cantidad, id, cantidad);
    }

    // Verificar si la funcion tiene compras registradas
    public boolean tieneCompras(int id) {
        String sql = """
            SELECT COUNT(*) FROM compras
            WHERE id_funcion = ?
            """;

        Integer cantidad = jdbc.queryForObject(
                sql, Integer.class, id
        );

        return cantidad != null && cantidad > 0;
    }
}
