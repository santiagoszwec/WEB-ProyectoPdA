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
        String sql = "SELECT r.*, " +
                "p.titulo AS titulo_publicacion, " +
                "p.mensaje AS mensaje_publicacion, " +
                "c.mensaje AS mensaje_comentario, " +
                "u_reportante.nombre AS nombre_reportante, " +
                "u_autor.nombre AS nombre_autor " +
                "FROM reporte r " +
                "LEFT JOIN comentario c ON r.comentario_id = c.id " +
                "LEFT JOIN publicacion p ON p.id = COALESCE(r.publicacion_id, c.publicacion_id) " +
                "LEFT JOIN usuario u_reportante ON u_reportante.id = r.usuario_id " +
                "LEFT JOIN usuario u_autor ON u_autor.id = COALESCE(p.usuario_id, c.usuario_id) " +
                "WHERE r.fecha_resolucion IS NULL " +
                "ORDER BY r.fecha_reporte";
        return listarConSql(sql);
    }

    public static List<Reporte> listarReportesPublicacionesAbiertos() {
        String sql = "SELECT r.*, p.titulo AS titulo_publicacion, " +
                "p.mensaje AS mensaje_publicacion, " +
                "c.mensaje AS mensaje_comentario " +
                "FROM reporte r " +
                "LEFT JOIN publicacion p ON r.publicacion_id = p.id " +
                "LEFT JOIN comentario c ON r.comentario_id = c.id " +
                "WHERE r.publicacion_id IS NOT NULL AND r.fecha_resolucion IS NULL " +
                "ORDER BY r.fecha_reporte";

        return listarConSql(sql);
    }

    public static List<Reporte> listarReportesComentariosAbiertos() {
        String sql = "SELECT r.*, p.titulo AS titulo_publicacion, " +
                "p.mensaje AS mensaje_publicacion, " +
                "c.mensaje AS mensaje_comentario " +
                "FROM reporte r " +
                "LEFT JOIN publicacion p ON r.publicacion_id = p.id " +
                "LEFT JOIN comentario c ON r.comentario_id = c.id " +
                "WHERE r.comentario_id IS NOT NULL AND r.fecha_resolucion IS NULL " +
                "ORDER BY r.fecha_reporte";

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
        String sql = "SELECT r.*, " +
                "p.titulo AS titulo_publicacion, " +
                "p.mensaje AS mensaje_publicacion, " +
                "c.mensaje AS mensaje_comentario, " +
                "u_reportante.nombre AS nombre_reportante, " +
                "u_autor.nombre AS nombre_autor " +
                "FROM reporte r " +
                "LEFT JOIN comentario c ON r.comentario_id = c.id " +
                "LEFT JOIN publicacion p ON p.id = COALESCE(r.publicacion_id, c.publicacion_id) " +
                "LEFT JOIN usuario u_reportante ON u_reportante.id = r.usuario_id " +
                "LEFT JOIN usuario u_autor ON u_autor.id = COALESCE(p.usuario_id, c.usuario_id) " +
                "WHERE r.fecha_resolucion IS NULL " +
                "ORDER BY r.fecha_reporte";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) {
                    return mapearReporte(filas);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
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

        Reporte reporte = new Reporte(id, contenido, motivo, resolucion, fechaReporte, fechaResolucion, publicacionId, comentarioId, usuarioId);

        reporte.setTituloPublicacion(filas.getString("titulo_publicacion"));
        reporte.setMensajePublicacion(filas.getString("mensaje_publicacion"));
        reporte.setMensajeComentario(filas.getString("mensaje_comentario"));
        reporte.setNombreReportante(filas.getString("nombre_reportante"));
        reporte.setNombreAutor(filas.getString("nombre_autor"));

        return reporte;
    }
}