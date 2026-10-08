package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.TipoNotificacion;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Notificacion;
import org.example.webproyectopda.Modelos.Suspension;
import org.example.webproyectopda.Modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuspensionDAO {

    public static boolean suspender(int usuarioId, String motivo, int dias) {
        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            LocalDate fechaSuspension = LocalDate.now();
            LocalDate fechaFin = fechaSuspension.plusDays(dias);

            boolean usuarioOk = ejecutarUpdate(conexion,
                    "UPDATE usuario SET activo = FALSE WHERE id = ?", usuarioId);

            boolean suspensionOk = ejecutarUpdate(conexion,
                    "INSERT INTO suspension (usuario_id, motivo, fecha_suspension, fecha_fin, activa) VALUES (?,?,?,?,TRUE)",
                    usuarioId, motivo, fechaSuspension, fechaFin);

            boolean notificacionOk = NotificacionDAO.insertarNotificacion(conexion, new Notificacion(
                    fechaSuspension, TipoNotificacion.Suspension,
                    "Su cuenta ha sido suspendida hasta " + fechaFin + ". Motivo: " + motivo, usuarioId, null));

            if (!usuarioOk || !suspensionOk || !notificacionOk) {
                conexion.rollback();
                return false;
            }

            conexion.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
            } catch (SQLException rollbackEx) {
                // ignorar error al deshacer
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (conexion != null) {
                    conexion.setAutoCommit(true);
                    conexion.close();
                }
            } catch (SQLException e) {
                // ignorar error al cerrar
            }
        }
    }

    public static boolean levantar(int usuarioId) {
        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            boolean usuarioOk = ejecutarUpdate(conexion,
                    "UPDATE usuario SET activo = TRUE WHERE id = ?", usuarioId);

            boolean suspensionOk = ejecutarUpdate(conexion,
                    "UPDATE suspension SET activa = FALSE WHERE usuario_id = ? AND activa = TRUE", usuarioId);

            boolean notificacionOk = NotificacionDAO.insertarNotificacion(conexion, new Notificacion(
                    LocalDate.now(), TipoNotificacion.Suspension,
                    "Su suspensión ha sido levantada. Ya puede iniciar sesión.", usuarioId, null));

            if (!usuarioOk || !suspensionOk || !notificacionOk) {
                conexion.rollback();
                return false;
            }

            conexion.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
            } catch (SQLException rollbackEx) {
                // ignorar error al deshacer
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (conexion != null) {
                    conexion.setAutoCommit(true);
                    conexion.close();
                }
            } catch (SQLException e) {
                // ignorar error al cerrar
            }
        }
    }

    public static Suspension obtenerSuspensionActiva(int usuarioId) {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            String sql = "SELECT * FROM suspension WHERE usuario_id = ? AND activa = TRUE ORDER BY fecha_suspension DESC LIMIT 1";
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setInt(1, usuarioId);
                try (ResultSet fila = sentencia.executeQuery()) {
                    if (fila.next()) {
                        return new Suspension(
                                fila.getInt("id"),
                                fila.getInt("usuario_id"),
                                fila.getString("motivo"),
                                fila.getObject("fecha_suspension", LocalDate.class),
                                fila.getObject("fecha_fin", LocalDate.class),
                                fila.getBoolean("activa")
                        );
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Usuario> listarSuspendidos() {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            String sql = "SELECT u.* FROM usuario u INNER JOIN suspension s ON s.usuario_id = u.id " +
                    "WHERE s.activa = TRUE ORDER BY u.nombre";
            try (PreparedStatement sentencia = conexion.prepareStatement(sql);
                 ResultSet filas = sentencia.executeQuery()) {

                List<Usuario> retorno = new ArrayList<>();

                while (filas.next()) {
                    retorno.add(new Usuario(
                            filas.getInt("id"),
                            filas.getString("nombre"),
                            filas.getString("correo"),
                            filas.getInt("anio_de_generacion"),
                            TipoRol.valueOf(filas.getString("rol")),
                            filas.getString("contrasenia"),
                            filas.getBoolean("activo")
                    ));
                }

                return retorno;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean ejecutarUpdate(Connection conexion, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                sentencia.setObject(i + 1, parametros[i]);
            }
            return sentencia.executeUpdate() == 1;
        }
    }
}