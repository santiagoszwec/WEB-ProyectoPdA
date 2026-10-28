package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.Modelos.Notificacion;
import org.example.webproyectopda.Modelos.Tarea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {

    public static List<Tarea> listarPorInscripcion(int inscripcionId) throws SQLException {
        String sql = "SELECT * FROM tarea WHERE inscripcion_id = ? " +
                "ORDER BY completada, fecha_limite IS NULL, fecha_limite, id";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, inscripcionId);

            try (ResultSet filas = sentencia.executeQuery()) {
                List<Tarea> tareas = new ArrayList<>();
                while (filas.next()) {
                    tareas.add(mapearTarea(filas));
                }
                return tareas;
            }
        }
    }

    public static void crear(Tarea tarea) throws SQLException {
        String sql = "INSERT INTO tarea (titulo, fecha_limite, inscripcion_id) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, tarea.getTitulo());
            if (tarea.getFechaLimite() != null) {
                sentencia.setObject(2, tarea.getFechaLimite());
            } else {
                sentencia.setNull(2, Types.DATE);
            }
            sentencia.setInt(3, tarea.getInscripcionId());

            sentencia.executeUpdate();
        }
    }

    public static boolean marcarCompletada(int tareaId, int inscripcionId, Notificacion notificacion) throws SQLException {
        String sql = "UPDATE tarea SET completada = TRUE, fecha_completada = ? " +
                "WHERE id = ? AND inscripcion_id = ? AND completada = FALSE";

        try (Connection conexion = ConexionDB.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                int filasActualizadas;
                try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                    sentencia.setObject(1, LocalDate.now());
                    sentencia.setInt(2, tareaId);
                    sentencia.setInt(3, inscripcionId);
                    filasActualizadas = sentencia.executeUpdate();
                }

                if (filasActualizadas != 1) {
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

    private static Tarea mapearTarea(ResultSet fila) throws SQLException {
        Tarea tarea = new Tarea();
        tarea.setId(fila.getInt("id"));
        tarea.setTitulo(fila.getString("titulo"));
        tarea.setFechaLimite(fila.getObject("fecha_limite", LocalDate.class));
        tarea.setCompletada(fila.getBoolean("completada"));
        tarea.setFechaCompletada(fila.getObject("fecha_completada", LocalDate.class));
        tarea.setInscripcionId(fila.getInt("inscripcion_id"));
        return tarea;
    }
}