package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.EstadoDuda;
import org.example.webproyectopda.ENUMS.TipoCategoria;
import org.example.webproyectopda.Modelos.Duda;
import org.example.webproyectopda.Modelos.Notificacion;

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
        String sqlPublicacion = "INSERT INTO publicacion (mensaje, titulo, imagen_url, fecha_publicacion, fecha_limite, usuario_id, curso_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlDuda = "INSERT INTO duda (id, estado, categoria) VALUES (?, ?, ?)";

        try {
            Connection conexion = ConexionDB.obtenerConexion();

            PreparedStatement sentenciaPublicacion = conexion.prepareStatement(sqlPublicacion, Statement.RETURN_GENERATED_KEYS);

            sentenciaPublicacion.setString(1, duda.getMensaje());
            sentenciaPublicacion.setString(2, duda.getTitulo());
            sentenciaPublicacion.setString(3, duda.getImagenUrl());
            sentenciaPublicacion.setObject(4, duda.getFechaPublicacion());

            if (duda.getFechaLimite() != null) {
                sentenciaPublicacion.setObject(5, duda.getFechaLimite());
            } else {
                sentenciaPublicacion.setNull(5, java.sql.Types.DATE);
            }

            sentenciaPublicacion.setInt(6, duda.getUsuarioId());
            sentenciaPublicacion.setInt(7, duda.getCursoId());

            int filasInsertadas = sentenciaPublicacion.executeUpdate();

            if (filasInsertadas != 1) {
                return false;
            }

            ResultSet generadas = sentenciaPublicacion.getGeneratedKeys();

            if (!generadas.next()) {
                return false;
            }

            int id = generadas.getInt(1);
            duda.setId(id);

            PreparedStatement sentenciaDuda = conexion.prepareStatement(sqlDuda);
            sentenciaDuda.setInt(1, id);
            sentenciaDuda.setString(2, duda.getEstado().name());
            sentenciaDuda.setString(3, duda.getCategoria().name());

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

    public static Duda buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id, p.mensaje, p.titulo, p.imagen_url, " +
                "p.fecha_publicacion, p.fecha_limite, p.activa, " +
                "p.usuario_id, p.curso_id, d.estado, d.categoria, " +
                "u.nombre AS autor_nombre " +
                "FROM publicacion p " +
                "JOIN duda d ON d.id = p.id " +
                "LEFT JOIN usuario u ON u.id = p.usuario_id " +
                "WHERE p.id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet fila = sentencia.executeQuery()) {
                if (fila.next()) {
                    Duda duda = mapearDuda(fila);
                    duda.setAutorNombre(fila.getString("autor_nombre"));
                    return duda;
                }
                return null;
            }
        }
    }

    private static Duda mapearDuda(ResultSet filas) throws SQLException {
        int id = filas.getInt("id");
        String mensaje = filas.getString("mensaje");
        String titulo = filas.getString("titulo");
        LocalDate fechaLimite = filas.getObject("fecha_limite", LocalDate.class);
        String imagenUrl = filas.getString("imagen_url");
        LocalDate fechaPublicacion = filas.getObject("fecha_publicacion", LocalDate.class);
        boolean activa = filas.getBoolean("activa");
        int usuarioId = filas.getInt("usuario_id");
        int cursoId = filas.getInt("curso_id");
        EstadoDuda estado = EstadoDuda.valueOf(filas.getString("estado"));
        TipoCategoria categoria = TipoCategoria.valueOf(filas.getString("categoria"));

        Duda duda = new Duda(id, mensaje, imagenUrl, fechaPublicacion, !activa, usuarioId, cursoId, estado, categoria);

        duda.setTitulo(titulo);
        duda.setFechaLimite(fechaLimite);

        return duda;
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
    public static boolean marcarResueltaYDestacarRespuesta(int dudaId, int respuestaId, Notificacion notificacion)
            throws SQLException {
        String sqlDuda = "UPDATE duda SET estado = ? WHERE id = ? AND estado = ?";
        String sqlRespuesta = "UPDATE comentario SET destacado = TRUE "
                + "WHERE id = ? AND publicacion_id = ? AND comentario_padre_id IS NULL AND activa = TRUE";

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement sentenciaDuda = conexion.prepareStatement(sqlDuda);
                 PreparedStatement sentenciaRespuesta = conexion.prepareStatement(sqlRespuesta)) {

                sentenciaDuda.setString(1, EstadoDuda.Resuelta.name());
                sentenciaDuda.setInt(2, dudaId);
                sentenciaDuda.setString(3, EstadoDuda.Abierta.name());

                sentenciaRespuesta.setInt(1, respuestaId);
                sentenciaRespuesta.setInt(2, dudaId);

                if (sentenciaDuda.executeUpdate() != 1 || sentenciaRespuesta.executeUpdate() != 1) {
                    conexion.rollback();
                    return false;
                }

                if (notificacion != null) {
                    NotificacionDAO.insertarNotificacion(conexion, notificacion);
                }
                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }
}
