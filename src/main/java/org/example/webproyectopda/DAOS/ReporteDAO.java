package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.Modelos.Reporte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public static boolean crear(Reporte reporte) {
        String sql = "INSERT INTO reporte (contenido, motivo, resolucion, fecha_reporte, fecha_resolucion, " +
                "publicacion_id, comentario_id, usuario_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, reporte.getContenido());
            sentencia.setString(2, reporte.getMotivo());
            sentencia.setString(3, reporte.getResolucion());
            sentencia.setObject(4, reporte.getFechaReporte());

            if (reporte.getFechaResolucion() != null) {
                sentencia.setObject(5, reporte.getFechaResolucion());
            } else {
                sentencia.setNull(5, Types.DATE);
            }
            if (reporte.getPublicacionId() != null) {
                sentencia.setInt(6, reporte.getPublicacionId());
            } else {
                sentencia.setNull(6, Types.INTEGER);
            }
            if (reporte.getComentarioId() != null) {
                sentencia.setInt(7, reporte.getComentarioId());
            } else {
                sentencia.setNull(7, Types.INTEGER);
            }
            sentencia.setInt(8, reporte.getUsuarioId());

            return sentencia.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Reporte> listarReportesAbiertos() {
        String sql = "SELECT * FROM reporte WHERE fecha_resolucion IS NULL ORDER BY fecha_reporte";
        return listarConSql(sql);
    }

    public static List<Reporte> listarReportesPublicacionesAbiertos() {
        String sql = "SELECT * FROM reporte WHERE publicacion_id IS NOT NULL AND fecha_resolucion IS NULL ORDER BY fecha_reporte";
        return listarConSql(sql);
    }

    public static List<Reporte> listarReportesComentariosAbiertos() {
        String sql = "SELECT * FROM reporte WHERE comentario_id IS NOT NULL AND fecha_resolucion IS NULL ORDER BY fecha_reporte";
        return listarConSql(sql);
    }

    private static List<Reporte> listarConSql(String sql) {
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {

            List<Reporte> retorno = new ArrayList<>();
            while (filas.next()) {
                retorno.add(mapearReporte(filas));
            }
            return retorno;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Reporte buscarPorId(int id) {
        String sql = "SELECT * FROM reporte WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) {
                    return mapearReporte(filas);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean resolver(int id, String resolucion) {
        String sql = "UPDATE reporte SET resolucion = ?, fecha_resolucion = ? WHERE id = ? AND fecha_resolucion IS NULL";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, resolucion);
            sentencia.setObject(2, LocalDate.now());
            sentencia.setInt(3, id);
            return sentencia.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Reporte mapearReporte(ResultSet filas) throws SQLException {
        int id = filas.getInt("id");
        String contenido = filas.getString("contenido");
        String motivo = filas.getString("motivo");
        String resolucion = filas.getString("resolucion");
        LocalDate fechaReporte = filas.getObject("fecha_reporte", LocalDate.class);
        LocalDate fechaResolucion = filas.getObject("fecha_resolucion", LocalDate.class);

        int pubId = filas.getInt("publicacion_id");
        Integer publicacionId = filas.wasNull() ? null : pubId;

        int comId = filas.getInt("comentario_id");
        Integer comentarioId = filas.wasNull() ? null : comId;

        int usuarioId = filas.getInt("usuario_id");
        return new Reporte(id, contenido, motivo, resolucion, fechaReporte, fechaResolucion, publicacionId, comentarioId, usuarioId);
    }
}