package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.Modelos.Comentario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.example.webproyectopda.Modelos.Notificacion;
import org.example.webproyectopda.ENUMS.TipoNotificacion;
import java.util.ArrayDeque;
import java.util.Deque;

public class ComentarioDAO {

    public static boolean crear(Comentario comentario) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return insertar(conexion, comentario);
        }
    }

    private static boolean insertar(Connection conexion, Comentario comentario) throws SQLException {
        String sql = "INSERT INTO comentario (mensaje, imagen_url, imagen_public_id, fecha_publicacion, "
                + "usuario_id, publicacion_id, comentario_padre_id, destacado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, comentario.getMensaje());
            sentencia.setString(2, comentario.getImagenUrl());
            sentencia.setString(3, comentario.getImagenPublicId());
            sentencia.setObject(4, comentario.getFechaPublicacion());
            sentencia.setInt(5, comentario.getUsuarioId());
            sentencia.setInt(6, comentario.getPublicacionId());

            if (comentario.getComentarioPadreId() != null) {
                sentencia.setInt(7, comentario.getComentarioPadreId());
            } else {
                sentencia.setNull(7, Types.INTEGER);
            }

            sentencia.setBoolean(8, comentario.isDestacado());
            return sentencia.executeUpdate() == 1;
        }
    }

    public static void crearRespuesta(Comentario respuesta, Notificacion notificacion) throws SQLException {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                insertar(conexion, respuesta);
                if (notificacion != null) {
                    NotificacionDAO.insertarNotificacion(conexion, notificacion);
                }
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    public static List<Comentario> listarPorPublicacionTodos(int publicacionId) {
        String sql = "SELECT * FROM comentario WHERE publicacion_id = ? AND activa = TRUE ORDER BY fecha_publicacion ASC";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, publicacionId);

            ResultSet filas = sentencia.executeQuery();

            List<Comentario> comentarios = new ArrayList<>();
            while (filas.next()) {
                comentarios.add(mapearComentario(filas));
            }
            return comentarios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Comentario> listarPorPublicacion(int publicacionId) {

        String sql = "SELECT * FROM comentario " +
                "WHERE publicacion_id = ? AND comentario_padre_id IS NULL AND activa = TRUE " +
                "ORDER BY destacado DESC, fecha_publicacion ASC";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, publicacionId);

            ResultSet filas = sentencia.executeQuery();

            List<Comentario> comentarios = new ArrayList<>();

            while (filas.next()) {
                comentarios.add(mapearComentario(filas));
            }

            return comentarios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Comentario> listarRespuestas(int comentarioPadreId) {

        String sql = "SELECT * FROM comentario " +
                     "WHERE comentario_padre_id = ? AND activa = TRUE " +
                     "ORDER BY fecha_publicacion ASC";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, comentarioPadreId);

            ResultSet filas = sentencia.executeQuery();

            List<Comentario> comentarios = new ArrayList<>();

            while (filas.next()) {
                comentarios.add(mapearComentario(filas));
            }

            return comentarios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean marcarComoDestacado(int comentarioId) {
        String sql = "UPDATE comentario SET destacado = TRUE WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, comentarioId);
            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Comentario buscarPorId(int comentarioId) {
        String sql = "SELECT * FROM comentario WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, comentarioId);
            ResultSet filas = sentencia.executeQuery();

            if (filas.next()) {
                return mapearComentario(filas);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean darDeBaja(int comentarioId, String motivo) {
        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            Integer autorId = obtenerAutorId(conexion, comentarioId);
            Integer publicacionId = obtenerPublicacionId(conexion, comentarioId);
            List<Integer> idsAApagar = obtenerDescendientesIncluido(conexion, comentarioId);

            for (int id : idsAApagar) {
                try (PreparedStatement stmt = conexion.prepareStatement(
                        "UPDATE comentario SET activa = FALSE WHERE id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
            }

            try (PreparedStatement stmt = conexion.prepareStatement(
                    "UPDATE reporte SET resolucion = ?, fecha_resolucion = ? WHERE comentario_id = ? AND fecha_resolucion IS NULL")) {
                stmt.setString(1, motivo);
                stmt.setObject(2, LocalDate.now());
                stmt.setInt(3, comentarioId);
                stmt.executeUpdate();
            }

            boolean notificacionOk = true;
            if (autorId != null && publicacionId != null) {
                notificacionOk = org.example.webproyectopda.DAOS.NotificacionDAO.insertarNotificacion(conexion, new Notificacion(
                        LocalDate.now(), TipoNotificacion.Baja,
                        "Tu comentario ha sido eliminado por un administrador. Motivo: " + motivo, autorId, publicacionId));
            }

            if (!notificacionOk) {
                conexion.rollback();
                return false;
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conexion != null) conexion.rollback();
            } catch (SQLException ignored) { }
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

    private static Integer obtenerAutorId(Connection conexion, int comentarioId) throws SQLException {
        try (PreparedStatement stmt = conexion.prepareStatement(
                "SELECT usuario_id FROM comentario WHERE id = ?")) {
            stmt.setInt(1, comentarioId);
            try (ResultSet fila = stmt.executeQuery()) {
                return fila.next() ? fila.getInt("usuario_id") : null;
            }
        }
    }

    private static Integer obtenerPublicacionId(Connection conexion, int comentarioId) throws SQLException {
        try (PreparedStatement stmt = conexion.prepareStatement(
                "SELECT publicacion_id FROM comentario WHERE id = ?")) {
            stmt.setInt(1, comentarioId);
            try (ResultSet fila = stmt.executeQuery()) {
                return fila.next() ? fila.getInt("publicacion_id") : null;
            }
        }
    }

    // Junta el propio comentario + todos sus descendientes (hijos, nietos, etc.), a cualquier profundidad.
    private static List<Integer> obtenerDescendientesIncluido(Connection conexion, int comentarioId) throws SQLException {
        List<Integer> resultado = new ArrayList<>();
        Deque<Integer> pendientes = new ArrayDeque<>();
        pendientes.add(comentarioId);

        while (!pendientes.isEmpty()) {
            int actual = pendientes.poll();
            resultado.add(actual);

            try (PreparedStatement stmt = conexion.prepareStatement(
                    "SELECT id FROM comentario WHERE comentario_padre_id = ?")) {
                stmt.setInt(1, actual);
                try (ResultSet filas = stmt.executeQuery()) {
                    while (filas.next()) {
                        pendientes.add(filas.getInt("id"));
                    }
                }
            }
        }
        return resultado;
    }

    public static List<Comentario> listarActivos() {
        String sql = "SELECT * FROM comentario WHERE activa = TRUE ORDER BY fecha_publicacion";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {

            List<Comentario> comentarios = new ArrayList<>();
            while (filas.next()) {
                comentarios.add(mapearComentario(filas));
            }
            return comentarios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Comentario> listarDeDuda(int dudaId) throws SQLException {
        List<Comentario> comentarios = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre AS autor_nombre "
                + "FROM comentario c "
                + "JOIN usuario u ON u.id = c.usuario_id "
                + "WHERE c.publicacion_id = ? AND c.activa = TRUE "
                + "ORDER BY c.destacado DESC, c.fecha_publicacion ASC, c.id ASC";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, dudaId);

            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    Comentario comentario = mapearComentario(fila);
                    comentario.setAutorNombre(fila.getString("autor_nombre"));
                    comentarios.add(comentario);
                }
            }
        }
        return comentarios;
    }

    private static Comentario mapearComentario(ResultSet fila) throws SQLException {

        int padreId = fila.getInt("comentario_padre_id");
        Integer comentarioPadreId = fila.wasNull() ? null : padreId;

        Comentario comentario = new Comentario(
                fila.getInt("id"),
                fila.getString("mensaje"),
                fila.getString("imagen_url"),
                fila.getObject("fecha_publicacion", LocalDate.class),
                fila.getInt("usuario_id"),
                fila.getInt("publicacion_id"),
                comentarioPadreId,
                fila.getBoolean("destacado"),
                fila.getBoolean("activa")
        );
        comentario.setImagenPublicId(fila.getString("imagen_public_id"));
        return comentario;
    }

    public static boolean desactivarPropio(int comentarioId, int usuarioId) {
        String sql = "UPDATE comentario SET activa = FALSE WHERE id = ? AND usuario_id = ? AND activa = TRUE";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, comentarioId);
            sentencia.setInt(2, usuarioId);

            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
