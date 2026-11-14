package com.cinenoir.cinenoirapi.repository;

import com.cinenoir.cinenoirapi.model.Compra;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public class CompraRepository {

    private final JdbcTemplate jdbc;

    public CompraRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Convertir datos SQL en objetos Compra
    private final org.springframework.jdbc.core.RowMapper<Compra> mapper =
            (rs, rowNum) -> new Compra(
                    rs.getInt("id"),
                    rs.getObject("id_funcion", Integer.class),
                    rs.getString("nombre_cliente"),
                    rs.getInt("cantidad_boletos"),
                    rs.getBigDecimal("total_pago"),
                    rs.getTimestamp("fecha_compra").toLocalDateTime()
            );

    // Listar todas las compras
    public List<Compra> listar() {
        String sql = """
            SELECT * FROM compras
            ORDER BY fecha_compra DESC
            """;

        return jdbc.query(sql, mapper);
    }

    // Buscar una compra por ID
    public Optional<Compra> buscarPorId(int id) {
        String sql = "SELECT * FROM compras WHERE id = ?";

        return jdbc.query(sql, mapper, id)
                .stream().findFirst();
    }

    // Registrar una nueva compra
    public int guardar(Compra compra) {
        String sql = """
            INSERT INTO compras
            (id_funcion, nombre_cliente, cantidad_boletos,
             total_pago, fecha_compra)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        return jdbc.queryForObject(
                sql,
                Integer.class,
                compra.idFuncion(),
                compra.nombreCliente(),
                compra.cantidadBoletos(),
                compra.totalPago(),
                compra.fechaCompra()
        );
    }

    // Consultar compras por rango de fechas
    public List<Compra> listarPorFechas(
            LocalDateTime inicio,
            LocalDateTime fin) {

        String sql = """
            SELECT * FROM compras
            WHERE fecha_compra >= ?
            AND fecha_compra < ?
            ORDER BY fecha_compra DESC
            """;

        return jdbc.query(sql, mapper, inicio, fin);
    }
}
