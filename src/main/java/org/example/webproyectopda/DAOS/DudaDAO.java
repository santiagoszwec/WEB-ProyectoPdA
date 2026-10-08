package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.EstadoDuda;
import org.example.webproyectopda.ENUMS.TipoCategoria;
import org.example.webproyectopda.Modelos.Duda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DudaDAO {

    // Duda hereda de Publicacion por tabla dividida: primero se inserta en
    // "publicacion" (para obtener el id autogenerado) y luego en "duda" con ese mismo id.
    public static boolean crear(Duda duda) {
        String sqlPublicacion = "INSERT INTO publicacion (mensaje, imagen_url, fecha_publicacion, usuario_id, curso_id) VALUES (?,?,?,?,?)";
        String sqlDuda = "INSERT INTO duda (id, estado, categoria) VALUES (?,?,?)";

        try {
            Connection conexion = ConexionDB.obtenerConexion();

            PreparedStatement sentenciaPublicacion = conexion.prepareStatement(sqlPublicacion, Statement.RETURN_GENERATED_KEYS);
            sentenciaPublicacion.setString(1, duda.getMensaje());
            sentenciaPublicacion.setString(2, duda.getImagenUrl());
            sentenciaPublicacion.setObject(3, duda.getFechaPublicacion());
            sentenciaPublicacion.setInt(4, duda.getUsuarioId());
            sentenciaPublicacion.setInt(5, duda.getCursoId());
            sentenciaPublicacion.executeUpdate();

            ResultSet generadas = sentenciaPublicacion.getGeneratedKeys();
            if (!generadas.next()) {
                return false;
            }

            int id = generadas.getInt(1);
            duda.setId(id);

            PreparedStatement sentenciaDuda = conexion.prepareStatement(sqlDuda);
            sentenciaDuda.setInt(1, id);
            sentenciaDuda.setObject(2, duda.getEstado().name());
            sentenciaDuda.setObject(3, duda.getCategoria().name());

            return sentenciaDuda.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Duda> listarTodos() {
        String sql = "SELECT p.id, p.mensaje, p.imagen_url, p.fecha_publicacion, p.activa, p.usuario_id, p.curso_id, d.estado, d.categoria " +
                "FROM publicacion p JOIN duda d ON d.id = p.id WHERE p.activa = TRUE ORDER BY p.fecha_publicacion";

        try {
            Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet filas = sentencia.executeQuery();

            List<Duda> retorno = new ArrayList<>();

            while (filas.next()) {
                retorno.add(mapearDuda(filas));
            }

            return retorno;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Duda buscarPorId(int id) {
        String sql = "SELECT p.id, p.mensaje, p.imagen_url, p.fecha_publicacion, p.activa, p.usuario_id, p.curso_id, d.estado, d.categoria " +
                "FROM publicacion p JOIN duda d ON d.id = p.id WHERE p.id = ?";

        try {
            Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, id);

            ResultSet fila = sentencia.executeQuery();

            if (fila.next()) {
                return mapearDuda(fila);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Duda mapearDuda(ResultSet filas) throws SQLException {
        int id = filas.getInt("id");
        String mensaje = filas.getString("mensaje");
        String imagenUrl = filas.getString("imagen_url");
        LocalDate fechaPublicacion = filas.getObject("fecha_publicacion", LocalDate.class);
        boolean activa = filas.getBoolean("activa");
        int usuarioId = filas.getInt("usuario_id");
        int cursoId = filas.getInt("curso_id");
        EstadoDuda estado = EstadoDuda.valueOf(filas.getString("estado"));
        TipoCategoria categoria = TipoCategoria.valueOf(filas.getString("categoria"));

        return new Duda(id, mensaje, imagenUrl, fechaPublicacion, !activa, usuarioId, cursoId, estado, categoria);
    }

    public static boolean marcarComoResuelta(int dudaId) {
        String sql = "UPDATE duda SET estado = ? WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, EstadoDuda.Resuelta.name());
            sentencia.setInt(2, dudaId);
            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean marcarResueltaYDestacarRespuesta(int dudaId, int comentarioId) {
        String sqlDuda = "UPDATE duda SET estado = ? WHERE id = ?";
        String sqlComentario = "UPDATE comentario SET destacado = TRUE WHERE id = ?";

        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            try (PreparedStatement stmtDuda = conexion.prepareStatement(sqlDuda)) {
                stmtDuda.setString(1, EstadoDuda.Resuelta.name());
                stmtDuda.setInt(2, dudaId);
                int filasDuda = stmtDuda.executeUpdate();

                try (PreparedStatement stmtComentario = conexion.prepareStatement(sqlComentario)) {
                    stmtComentario.setInt(1, comentarioId);
                    int filasComentario = stmtComentario.executeUpdate();

                    if (filasDuda == 1 && filasComentario == 1) {
                        conexion.commit();
                        return true;
                    }
                    conexion.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException ignored) { }
            }
        }
    }
}
