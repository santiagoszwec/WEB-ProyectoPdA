package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.TipoEstado;
import org.example.webproyectopda.Modelos.Inscripcion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscripcionDAO {

    public static boolean crear(Inscripcion inscripcion) {
        String sql = "INSERT INTO inscripcion (estado, usuario_id, curso_id) VALUES (?,?,?)";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, inscripcion.getEstado().name());
            sentencia.setInt(2, inscripcion.getUsuarioId());
            sentencia.setInt(3, inscripcion.getCursoId());

            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean usuarioExisteYActivo(int usuarioId) {
        String sql = "SELECT activo FROM usuario WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() && fila.getBoolean("activo");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean cursoExisteYActivo(int cursoId) {
        String sql = "SELECT activo FROM curso WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, cursoId);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() && fila.getBoolean("activo");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean existeInscripcion(int usuarioId, int cursoId) {
        String sql = "SELECT 1 FROM inscripcion WHERE usuario_id = ? AND curso_id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Inscripcion> listarTodas() {
        String sql = "SELECT * FROM inscripcion ORDER BY id";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {

            return mapearInscripciones(filas);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Inscripcion> listarPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM inscripcion WHERE usuario_id = ? ORDER BY id";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);

            try (ResultSet filas = sentencia.executeQuery()) {
                return mapearInscripciones(filas);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Inscripcion> listarPorCurso(int cursoId) {
        String sql = "SELECT * FROM inscripcion WHERE curso_id = ? ORDER BY id";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, cursoId);

            try (ResultSet filas = sentencia.executeQuery()) {
                return mapearInscripciones(filas);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Inscripcion> listarPorEstado(TipoEstado estado) {
        String sql = "SELECT * FROM inscripcion WHERE estado = ? ORDER BY id";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, estado.name());

            try (ResultSet filas = sentencia.executeQuery()) {
                return mapearInscripciones(filas);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean actualizarEstado(int inscripcionId, TipoEstado estado) {
        String sql = "UPDATE inscripcion SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, estado.name());
            sentencia.setInt(2, inscripcionId);

            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean eliminar(int inscripcionId) {
        String sql = "DELETE FROM inscripcion WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, inscripcionId);

            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Inscripcion> mapearInscripciones(ResultSet filas) throws SQLException {
        List<Inscripcion> inscripciones = new ArrayList<>();
        while (filas.next()) {
            inscripciones.add(mapearInscripcion(filas));
        }
        return inscripciones;
    }

    private static Inscripcion mapearInscripcion(ResultSet fila) throws SQLException {
        return new Inscripcion(
                fila.getInt("id"),
                TipoEstado.valueOf(fila.getString("estado")),
                fila.getInt("usuario_id"),
                fila.getInt("curso_id")
        );
    }

    public static List<Integer> listarUsuariosCursando(int cursoId) {
        String sql = "SELECT usuario_id FROM inscripcion WHERE curso_id = ? AND estado = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, cursoId);
            sentencia.setString(2, TipoEstado.Cursando.name());

            try (ResultSet filas = sentencia.executeQuery()) {
                List<Integer> usuariosIds = new ArrayList<>();
                while (filas.next()) {
                    usuariosIds.add(filas.getInt("usuario_id"));
                }
                return usuariosIds;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}