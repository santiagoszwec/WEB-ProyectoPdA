package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.TipoNotificacion;
import org.example.webproyectopda.Modelos.Notificacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

public class NotificacionDAO {

    public static boolean crear(Notificacion notificacion) {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            return insertarNotificacion(conexion, notificacion);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean insertarNotificacion(Connection conexion, Notificacion notificacion) throws SQLException {

        String sql = "INSERT INTO notificacion (fecha, tipo, mensaje, usuario_id, publicacion_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setObject(1, notificacion.getFecha());
            sentencia.setString(2, notificacion.getTipo().toString());
            sentencia.setString(3, notificacion.getMensaje());
            sentencia.setInt(4, notificacion.getUsuarioId());

            if (notificacion.getPublicacionId() != null) {
                sentencia.setInt(5, notificacion.getPublicacionId());
            } else {
                sentencia.setNull(5, Types.INTEGER);
            }


            return sentencia.executeUpdate() == 1;
        }
    }
    public static List<Notificacion> listarPorUsuario(int usuarioId, TipoNotificacion tipo, boolean masAntiguasPrimero)
            throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT n.id, n.fecha, n.tipo, n.mensaje, n.usuario_id, n.publicacion_id, "
                + "p.curso_id, p.activa AS publicacion_activa, (d.id IS NOT NULL) AS es_duda "
                + "FROM notificacion n "
                + "LEFT JOIN publicacion p ON p.id = n.publicacion_id "
                + "LEFT JOIN duda d ON d.id = p.id "
                + "WHERE n.usuario_id = ?";

        if (tipo != null) {
            sql += " AND n.tipo = ?";
        }

        if (masAntiguasPrimero) {
            sql += " ORDER BY n.fecha ASC, n.id ASC";
        } else {
            sql += " ORDER BY n.fecha DESC, n.id DESC";
        }

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            if (tipo != null) {
                sentencia.setString(2, tipo.name());
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    notificaciones.add(crearNotificacion(resultado));
                }
            }
        }
        return notificaciones;
    }
    public static boolean eliminar(int notificacionId, int usuarioId) throws SQLException {
        String sql = "DELETE FROM notificacion WHERE id = ? AND usuario_id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, notificacionId);
            sentencia.setInt(2, usuarioId);

            return sentencia.executeUpdate() == 1;
        }
    }
    private static Notificacion crearNotificacion(ResultSet resultado) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(resultado.getInt("id"));
        notificacion.setFecha(resultado.getObject("fecha", LocalDate.class));
        notificacion.setTipo(TipoNotificacion.valueOf(resultado.getString("tipo")));
        notificacion.setMensaje(resultado.getString("mensaje"));
        notificacion.setUsuarioId(resultado.getInt("usuario_id"));

        int publicacionId = resultado.getInt("publicacion_id");
        notificacion.setPublicacionId(resultado.wasNull() ? null : publicacionId);
        int cursoId = resultado.getInt("curso_id");
        notificacion.setCursoId(resultado.wasNull() ? null : cursoId);
        notificacion.setEsDuda(resultado.getBoolean("es_duda"));
        notificacion.setPublicacionActiva(resultado.getBoolean("publicacion_activa"));
        return notificacion;
    }
}