package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.Modelos.Notificacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
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
    public static List<Notificacion> listarPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM notificacion WHERE usuario_id = ? ORDER BY id DESC";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);

            try (ResultSet filas = sentencia.executeQuery()) {
                List<Notificacion> retorno = new ArrayList<>();
                while (filas.next()) {
                    int publicacionIdRaw = filas.getInt("publicacion_id");
                    Integer publicacionId = filas.wasNull() ? null : publicacionIdRaw;
                    retorno.add(new Notificacion(
                            filas.getInt("id"),
                            filas.getObject("fecha", java.time.LocalDate.class),
                            org.example.webproyectopda.ENUMS.TipoNotificacion.valueOf(filas.getString("tipo")),
                            filas.getString("mensaje"),
                            filas.getInt("usuario_id"),
                            publicacionId
                    ));
                }
                return retorno;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}