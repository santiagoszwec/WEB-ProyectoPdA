package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.TipoCategoria;
import org.example.webproyectopda.ENUMS.TipoNotificacion;

import org.example.webproyectopda.ENUMS.*;
import org.example.webproyectopda.Modelos.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PublicacionDAO {
    public static boolean crear(Publicacion publicacion) {
        try {
            Connection conexion = ConexionDB.obtenerConexion();

            String sql = "INSERT INTO publicacion (mensaje, imagen_url, fecha_publicacion, usuario_id, curso_id) VALUES (?,?,?,?,?)";

            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, publicacion.getMensaje());
            sentencia.setString(2, publicacion.getImagenUrl());
            sentencia.setObject(3, publicacion.getFechaPublicacion());
            sentencia.setInt(4, publicacion.getUsuarioId());
            sentencia.setInt(5, publicacion.getCursoId());

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Publicacion> listarActivas(int usuarioId, int cursoId) {
        List<Publicacion> retorno = new ArrayList<>();

        String sql = "SELECT p.* FROM publicacion p INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? ORDER BY p.fecha_publicacion";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);
            ResultSet filas = sentencia.executeQuery();

            while (filas.next()) {
                int id = filas.getInt("id");
                String mensaje = filas.getString("mensaje");
                String imagenUrl = filas.getString("imagen_url");
                LocalDate fechaPublicacion = filas.getObject("fecha_publicacion", LocalDate.class);
                boolean activa = filas.getBoolean("activa");
                int usuarioPublicacion = filas.getInt("usuario_id");
                int cursoPublicacion = filas.getInt("curso_id");

                Publicacion publicacion = new Publicacion(id, mensaje, imagenUrl, fechaPublicacion, !activa, usuarioPublicacion, cursoPublicacion);
                retorno.add(publicacion);
            }
            return retorno;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean actualizar(Publicacion publicacion) {
        String sql = "UPDATE publicacion SET mensaje = ?, imagen_url = ?, fecha_publicacion = ? WHERE id = ?";
        try {
            Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setString(1, publicacion.getMensaje());
            sentencia.setString(2, publicacion.getImagenUrl());
            sentencia.setObject(3, publicacion.getFechaPublicacion());
            sentencia.setInt(4, publicacion.getId());

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean esMaterial(int id) {
        String sql = "SELECT 1 FROM material WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean esDuda(int id) {
        String sql = "SELECT 1 FROM duda WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean actualizarMaterial(int id, String archivoUrl, TipoMaterial tipoMaterial, TipoArchivo tipoArchivo) {
        String sql = "UPDATE material SET archivo_url = ?, tipo_material = ?, tipo_archivo = ? WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, archivoUrl);
            sentencia.setString(2, tipoMaterial.toString());
            sentencia.setString(3, tipoArchivo.toString());
            sentencia.setInt(4, id);

            return sentencia.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean actualizarCategoria(int id, TipoCategoria categoria) {
        String sql = esDuda(id)
                ? "UPDATE duda SET categoria = ? WHERE id = ?"
                : "UPDATE mensaje SET categoria = ? WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, categoria.toString());
            sentencia.setInt(2, id);

            return sentencia.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean darDeBaja(int id, String motivo) {
        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            Integer autorId = obtenerAutorId(conexion, id);

            boolean publicacionOk = ejecutarUpdate(conexion,
                    "UPDATE publicacion SET activa = FALSE WHERE id = ?", id);
            ejecutarUpdate(conexion,
                    "UPDATE comentario SET activa = FALSE WHERE publicacion_id = ?", id);
            ejecutarUpdate(conexion,
                    "UPDATE reporte SET resolucion = ?, fecha_resolucion = ? WHERE publicacion_id = ? AND fecha_resolucion IS NULL",
                    motivo, LocalDate.now(), id);

            boolean notificacionOk = true;
            if (autorId != null) {
                notificacionOk = NotificacionDAO.insertarNotificacion(conexion, new Notificacion(
                        LocalDate.now(), TipoNotificacion.Baja,
                        "Su publicación ha sido dada de baja por infracción de las normas.", autorId, id));
            }

            if (!publicacionOk || !notificacionOk) {
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

    private static Integer obtenerAutorId(Connection conexion, int publicacionId) throws SQLException {
        String sql = "SELECT usuario_id FROM publicacion WHERE id = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, publicacionId);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (fila.next()) {
                    int autorId = fila.getInt("usuario_id");
                    return fila.wasNull() ? null : autorId;
                }
            }
        }
        throw new SQLException("No se encontró la publicación con id " + publicacionId);
    }

    private static boolean ejecutarUpdate(Connection conexion, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                Object p = parametros[i];
                if (p instanceof Integer) {
                    sentencia.setInt(i + 1, (Integer) p);
                } else if (p instanceof String) {
                    sentencia.setString(i + 1, (String) p);
                } else if (p instanceof LocalDate) {
                    sentencia.setObject(i + 1, (LocalDate) p);
                } else {
                    sentencia.setObject(i + 1, p);
                }
            }
            sentencia.executeUpdate();
            return true;
        }
    }

    public static Publicacion buscarPorId(int id) {
        try (Connection conexion = ConexionDB.obtenerConexion()) {
            String sql = "SELECT * FROM publicacion WHERE id = ?";
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setInt(1, id);
                try (ResultSet filas = sentencia.executeQuery()) {
                    if (filas.next()) {
                        int pid = filas.getInt("id");
                        String mensaje = filas.getString("mensaje");
                        String imagenUrl = filas.getString("imagen_url");
                        LocalDate fechaPublicacion = filas.getObject("fecha_publicacion", LocalDate.class);
                        boolean activa = filas.getBoolean("activa");
                        int usuarioId = filas.getInt("usuario_id");
                        int cursoId = filas.getInt("curso_id");
                        return new Publicacion(pid, mensaje, imagenUrl, fechaPublicacion, !activa, usuarioId, cursoId);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean reactivar(int id) {
        try {
            Connection conexion = ConexionDB.obtenerConexion();

            String sql = "UPDATE publicacion SET activa = TRUE WHERE id = ?";

            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Mensaje> listarMensajes(int usuarioId, int cursoId) {
        List<Mensaje> mensajes = new ArrayList<>();

        String sql = "SELECT p.*, msg.categoria AS categoria_mensaje FROM publicacion p " +
                "INNER JOIN mensaje msg ON p.id = msg.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? ORDER BY p.fecha_publicacion";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);

            ResultSet rs = sentencia.executeQuery();

            while (rs.next()) {
                mensajes.add(convertirMensaje(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return mensajes;
    }

    public static List<Publicacion> filtrarMensajesPorCategoria(String categoria, int usuarioId, int cursoId) {

        List<Publicacion> publicaciones = new ArrayList<>();

        String sql = "SELECT p.*, msg.categoria AS categoria_mensaje FROM publicacion p " +
                "INNER JOIN mensaje msg ON p.id = msg.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? AND msg.categoria = ? " +
                "ORDER BY p.fecha_publicacion";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);
            sentencia.setString(3, categoria);

            ResultSet filas = sentencia.executeQuery();

            while (filas.next()) {
                publicaciones.add(convertirMensaje(filas));
            }
            return publicaciones;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Publicacion> listarDudas(int usuarioId, int cursoId) {

        List<Publicacion> publicaciones = new ArrayList<>();

        String sql = "SELECT p.*, d.estado AS estado_duda, d.categoria AS categoria_duda FROM publicacion p " +
                "INNER JOIN duda d ON p.id = d.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? ORDER BY p.fecha_publicacion";

        try (Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);
            ResultSet filas = sentencia.executeQuery();

            while (filas.next()) {
                publicaciones.add(convertirDuda(filas));
            }

            return publicaciones;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Publicacion> filtrarDudasPorCategoria(String categoria, int usuarioId, int cursoId) {

        List<Publicacion> publicaciones = new ArrayList<>();

        String sql = "SELECT p.*, d.estado AS estado_duda, d.categoria AS categoria_duda FROM publicacion p " +
                "INNER JOIN duda d ON p.id = d.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? AND d.categoria = ? " +
                "ORDER BY p.fecha_publicacion";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);
            sentencia.setString(3, categoria);

            ResultSet filas = sentencia.executeQuery();

            while (filas.next()) {
                publicaciones.add(convertirDuda(filas));
            }
            return publicaciones;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Publicacion> filtrarDudasPorEstado(String estado, int usuarioId, int cursoId) {

        List<Publicacion> publicaciones = new ArrayList<>();

        String sql = "SELECT p.*, d.estado AS estado_duda, d.categoria AS categoria_duda FROM publicacion p " +
                "INNER JOIN duda d ON p.id = d.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? AND d.estado = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);
            sentencia.setString(3, estado);
            ResultSet filas = sentencia.executeQuery();

            while (filas.next()) {
                publicaciones.add(convertirDuda(filas));
            }
            return publicaciones;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Publicacion> listarMateriales(int usuarioId, int cursoId) {

        List<Publicacion> publicaciones = new ArrayList<>();

        String sql = "SELECT p.*, m.archivo_url, m.tipo_material, m.tipo_archivo, m.tema FROM publicacion p " +
                "INNER JOIN material m ON p.id = m.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? ORDER BY p.fecha_publicacion";

        try (Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);
            ResultSet filas = sentencia.executeQuery();
            while (filas.next()) {
                publicaciones.add(convertirMaterial(filas));
            }
            return publicaciones;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Publicacion> filtrarMaterialesPorTipo(String tipoMaterial, int usuarioId, int cursoId) {

        List<Publicacion> publicaciones = new ArrayList<>();

        String sql = "SELECT p.*, m.archivo_url, m.tipo_material, m.tipo_archivo, m.tema FROM publicacion p " +
                "INNER JOIN material m ON p.id = m.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? AND m.tipo_material = ? " +
                "ORDER BY p.fecha_publicacion";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);
            sentencia.setString(3, tipoMaterial);

            ResultSet filas = sentencia.executeQuery();

            while (filas.next()) {
                publicaciones.add(convertirMaterial(filas));
            }
            return publicaciones;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Publicacion> filtrarMaterialesPorArchivo(String tipoArchivo, int usuarioId, int cursoId) {

        List<Publicacion> publicaciones = new ArrayList<>();

        String sql = "SELECT p.*, m.archivo_url, m.tipo_material, m.tipo_archivo, m.tema FROM publicacion p " +
                "INNER JOIN material m ON p.id = m.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? AND m.tipo_archivo = ? " +
                "ORDER BY p.fecha_publicacion";

        try (Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);
            sentencia.setString(3, tipoArchivo);

            ResultSet filas = sentencia.executeQuery();

            while (filas.next()) {
                publicaciones.add(convertirMaterial(filas));
            }
            return publicaciones;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Publicacion> filtrarMaterialesPorEtiquetas(List<TipoMaterial> tiposMaterial, List<TipoArchivo> tiposArchivo, int usuarioId, int cursoId) {

        List<Publicacion> publicaciones = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT p.*, m.archivo_url, m.tipo_material, m.tipo_archivo, m.tema FROM publicacion p " +
                        "INNER JOIN material m ON p.id = m.id INNER JOIN inscripcion i ON p.curso_id = i.curso_id " +
                        "WHERE p.activa = TRUE AND i.usuario_id = ? AND p.curso_id = ? ");

        List<String> parametros = new ArrayList<>();

        if (tiposMaterial != null && !tiposMaterial.isEmpty()) {
            sql.append("AND m.tipo_material IN (");

            for (int i = 0; i < tiposMaterial.size(); i++) {
                sql.append(i == 0 ? "?" : ",?");
                parametros.add(tiposMaterial.get(i).name());
            }
            sql.append(") ");
        }

        if (tiposArchivo != null && !tiposArchivo.isEmpty()) {
            sql.append("AND m.tipo_archivo IN (");

            for (int i = 0; i < tiposArchivo.size(); i++) {
                sql.append(i == 0 ? "?" : ",?");
                parametros.add(tiposArchivo.get(i).name());
            }
            sql.append(") ");
        }
        sql.append("ORDER BY p.fecha_publicacion");

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);

            for (int i = 0; i < parametros.size(); i++) {
                sentencia.setString(i + 3, parametros.get(i));
            }

            ResultSet filas = sentencia.executeQuery();

            while (filas.next()) {
                publicaciones.add(convertirMaterial(filas));
            }
            return publicaciones;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Material convertirMaterial(ResultSet fila)
            throws SQLException {

        return new Material(fila.getInt("id"),
                fila.getString("mensaje"),
                fila.getString("imagen_url"),
                fila.getObject("fecha_publicacion", LocalDate.class),
                !fila.getBoolean("activa"),
                fila.getInt("usuario_id"),
                fila.getInt("curso_id"),
                fila.getString("archivo_url"),
                TipoMaterial.valueOf(fila.getString("tipo_material")),
                TipoArchivo.valueOf(fila.getString("tipo_archivo")),
                fila.getString("tema"));
    }
    private static Duda convertirDuda(ResultSet fila)
            throws SQLException {

        return new Duda(fila.getInt("id"),
                fila.getString("mensaje"),
                fila.getString("imagen_url"),
                fila.getObject("fecha_publicacion", LocalDate.class),
                !fila.getBoolean("activa"),
                fila.getInt("usuario_id"),
                fila.getInt("curso_id"),
                EstadoDuda.valueOf(fila.getString("estado_duda")),
                TipoCategoria.valueOf(fila.getString("categoria_duda")));
    }
    private static Mensaje convertirMensaje(ResultSet fila)
            throws SQLException {

        return new Mensaje(fila.getInt("id"),
                fila.getString("mensaje"),
                fila.getString("imagen_url"),
                fila.getObject("fecha_publicacion", LocalDate.class),
                !fila.getBoolean("activa"),
                fila.getInt("usuario_id"),
                fila.getInt("curso_id"),
                TipoCategoria.valueOf(fila.getString("categoria_mensaje")));
    }
}

