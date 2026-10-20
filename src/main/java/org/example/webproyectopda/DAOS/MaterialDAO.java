package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.TipoArchivo;
import org.example.webproyectopda.ENUMS.TipoMaterial;
import org.example.webproyectopda.Modelos.Material;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    // La creación toca dos tablas (publicacion + material): se hacen ambas en la misma
    // transacción y se cierra la conexión, para no dejar publicaciones huérfanas.
    public static boolean crear(Material material) {
        String sqlPublicacion = "INSERT INTO publicacion (mensaje, imagen_url, fecha_publicacion, usuario_id, curso_id) VALUES (?,?,?,?,?)";
        String sqlMaterial = "INSERT INTO material (id, archivo_url, tipo_material, tipo_archivo, tema) VALUES (?,?,?,?,?)";

        Connection conexion = null;
        try {
            conexion = ConexionDB.obtenerConexion();
            conexion.setAutoCommit(false);

            int id;
            try (PreparedStatement sentenciaPublicacion =
                         conexion.prepareStatement(sqlPublicacion, Statement.RETURN_GENERATED_KEYS)) {

                sentenciaPublicacion.setString(1, material.getMensaje());
                sentenciaPublicacion.setString(2, material.getImagenUrl());
                sentenciaPublicacion.setObject(3, material.getFechaPublicacion());
                sentenciaPublicacion.setInt(4, material.getUsuarioId());
                sentenciaPublicacion.setInt(5, material.getCursoId());

                if (sentenciaPublicacion.executeUpdate() != 1) {
                    conexion.rollback();
                    return false;
                }

                try (ResultSet generadas = sentenciaPublicacion.getGeneratedKeys()) {
                    if (!generadas.next()) {
                        conexion.rollback();
                        return false;
                    }
                    id = generadas.getInt(1);
                }
            }

            material.setId(id);

            try (PreparedStatement sentenciaMaterial = conexion.prepareStatement(sqlMaterial)) {
                sentenciaMaterial.setInt(1, id);
                sentenciaMaterial.setString(2, material.getArchivoUrl());
                sentenciaMaterial.setString(3, material.getTipoMaterial().name());
                sentenciaMaterial.setString(4, material.getTipoArchivo().name());
                sentenciaMaterial.setString(5, material.getTema());

                if (sentenciaMaterial.executeUpdate() != 1) {
                    conexion.rollback();
                    return false;
                }
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            rollbackEnSilencio(conexion);
            throw new RuntimeException(e);
        } finally {
            restaurarYCerrar(conexion);
        }
    }

    private static void rollbackEnSilencio(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.rollback();
            } catch (SQLException ignored) {
                // no se puede hacer nada más si falla el rollback
            }
        }
    }

    private static void restaurarYCerrar(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.setAutoCommit(true);
                conexion.close();
            } catch (SQLException ignored) {
                // no se puede hacer nada más si falla el cierre
            }
        }
    }

    public static List<Material> listarTodos() {
        String sql = "SELECT p.id, p.mensaje, p.imagen_url, p.fecha_publicacion, p.activa, p.usuario_id, p.curso_id, m.archivo_url, m.tipo_material, m.tipo_archivo, m.tema " +
                "FROM publicacion p JOIN material m ON m.id = p.id WHERE p.activa = TRUE ORDER BY p.fecha_publicacion";

        try {
            Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet filas = sentencia.executeQuery();

            List<Material> retorno = new ArrayList<>();

            while (filas.next()) {
                retorno.add(mapearMaterial(filas));
            }

            return retorno;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Material buscarPorId(int id) {
        String sql = "SELECT p.id, p.mensaje, p.imagen_url, p.fecha_publicacion, p.activa, p.usuario_id, p.curso_id, " +
                "m.archivo_url, m.tipo_material, m.tipo_archivo, m.tema " +
                "FROM publicacion p JOIN material m ON m.id = p.id WHERE p.id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? mapearMaterial(filas) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Material mapearMaterial(ResultSet filas) throws SQLException {
        int id = filas.getInt("id");
        String mensaje = filas.getString("mensaje");
        String imagenUrl = filas.getString("imagen_url");
        LocalDate fechaPublicacion = filas.getObject("fecha_publicacion", LocalDate.class);
        boolean activa = filas.getBoolean("activa");
        int usuarioId = filas.getInt("usuario_id");
        int cursoId = filas.getInt("curso_id");
        String archivoUrl = filas.getString("archivo_url");
        TipoMaterial tipoMaterial = TipoMaterial.valueOf(filas.getString("tipo_material"));
        TipoArchivo tipoArchivo = TipoArchivo.valueOf(filas.getString("tipo_archivo"));
        String tema = filas.getString("tema");

        return new Material(id, mensaje, imagenUrl, fechaPublicacion, !activa, usuarioId, cursoId, archivoUrl, tipoMaterial, tipoArchivo, tema);
    }
}