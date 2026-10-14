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
        String sql = "SELECT id, fecha, tipo, mensaje, usuario_id, publicacion_id "
                + "FROM notificacion WHERE usuario_id = ?";

        if (tipo != null) {
            sql += " AND tipo = ?";
        }

        if (masAntiguasPrimero) {
            sql += " ORDER BY fecha ASC, id ASC";
        } else {
            sql += " ORDER BY fecha DESC, id DESC";
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

    private static Notificacion crearNotificacion(ResultSet resultado) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(resultado.getInt("id"));
        notificacion.setFecha(resultado.getObject("fecha", LocalDate.class));
        notificacion.setTipo(TipoNotificacion.valueOf(resultado.getString("tipo")));
        notificacion.setMensaje(resultado.getString("mensaje"));
        notificacion.setUsuarioId(resultado.getInt("usuario_id"));

        int publicacionId = resultado.getInt("publicacion_id");
        notificacion.setPublicacionId(resultado.wasNull() ? null : publicacionId);
        return notificacion;
    }
}