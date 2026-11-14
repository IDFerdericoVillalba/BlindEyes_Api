package com.cinenoir.cinenoirapi.repository;

import com.cinenoir.cinenoirapi.model.Pelicula;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PeliculaRepository {

    private final JdbcTemplate jdbc;

    public PeliculaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Listar todas las peliculas
    public List<Pelicula> listar() {
        String sql = "SELECT * FROM peliculas ORDER BY id";

        return jdbc.query(sql, (rs, rowNum) ->
                new Pelicula(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("genero"),
                        rs.getString("duracion"),
                        rs.getString("clasificacion")
                )
        );
    }

    // Buscar pelicula por ID
    public Optional<Pelicula> buscarPorId(int id) {
        return listarPorId(id).stream().findFirst();
    }

    private List<Pelicula> listarPorId(int id) {
        String sql = "SELECT * FROM peliculas WHERE id = ?";

        return jdbc.query(sql, (rs, rowNum) ->
                new Pelicula(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("genero"),
                        rs.getString("duracion"),
                        rs.getString("clasificacion")
                ), id
        );
    }

    // Crear pelicula
    public int guardar(Pelicula pelicula) {
        String sql = """
            INSERT INTO peliculas
            (titulo, genero, duracion, clasificacion)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;

        return jdbc.queryForObject(
                sql,
                Integer.class,
                pelicula.titulo(),
                pelicula.genero(),
                pelicula.duracion(),
                pelicula.clasificacion()
        );
    }

    // Actualizar pelicula
    public int actualizar(int id, Pelicula pelicula) {
        String sql = """
            UPDATE peliculas
            SET titulo = ?, genero = ?, duracion = ?,
                clasificacion = ?
            WHERE id = ?
            """;

        return jdbc.update(
                sql,
                pelicula.titulo(),
                pelicula.genero(),
                pelicula.duracion(),
                pelicula.clasificacion(),
                id
        );
    }

    // Eliminar pelicula
    public int eliminar(int id) {
        String sql = "DELETE FROM peliculas WHERE id = ?";
        return jdbc.update(sql, id);
    }

    // Bloquear una pelicula mientras se valida su eliminacion
    public boolean bloquearPorId(int id) {
        String sql = """
            SELECT id FROM peliculas
            WHERE id = ?
            FOR UPDATE
            """;

        return !jdbc.queryForList(sql, Integer.class, id).isEmpty();
    }

    // Verificar si la pelicula tiene funciones
    public boolean tieneFunciones(int id) {
        String sql = """
            SELECT COUNT(*) FROM funciones
            WHERE id_pelicula = ?
            """;

        Integer cantidad = jdbc.queryForObject(
                sql, Integer.class, id
        );

        return cantidad != null && cantidad > 0;
    }
}
