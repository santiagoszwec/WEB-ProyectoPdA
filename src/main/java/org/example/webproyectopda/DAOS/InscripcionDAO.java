package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.TipoEstado;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Curso;
import org.example.webproyectopda.Modelos.Inscripcion;
import org.example.webproyectopda.Modelos.Usuario;

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

    public static Inscripcion buscarPorId(int inscripcionId) {
        String sql = "SELECT * FROM inscripcion WHERE id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, inscripcionId);

            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? mapearInscripcion(filas) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Inscripcion buscarPorUsuarioYCurso(int usuarioId, int cursoId) {
        String sql = "SELECT * FROM inscripcion WHERE usuario_id = ? AND curso_id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);

            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? mapearInscripcion(filas) : null;
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

    public static TipoEstado obtenerEstado(int usuarioId, int cursoId) {
        String sql = "SELECT estado FROM inscripcion WHERE usuario_id = ? AND curso_id = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);
            sentencia.setInt(2, cursoId);

            try (ResultSet fila = sentencia.executeQuery()) {
                if (fila.next()) {
                    return TipoEstado.valueOf(fila.getString("estado"));
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean inscribirSiNoExiste(int usuarioId, int cursoId, int anio) {
        String sql = "INSERT INTO inscripcion (estado, usuario_id, curso_id)SELECT ?, ?, c.id FROM curso c " +
                "WHERE c.id = ? AND c.activo = TRUE AND c.anio = ? AND NOT EXISTS (" +
                "SELECT 1 FROM inscripcion i WHERE i.usuario_id = ? AND i.curso_id = c.id)";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, TipoEstado.Cursando.name());
            sentencia.setInt(2, usuarioId);
            sentencia.setInt(3, cursoId);
            sentencia.setInt(4, anio);
            sentencia.setInt(5, usuarioId);
            return sentencia.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ¿Ambos usuarios están inscriptos en al menos un curso activo?
    public static boolean existeCursoEnComun(int usuarioA, int usuarioB) {
        String sql = "SELECT 1 FROM inscripcion a " +
                "INNER JOIN inscripcion b ON b.curso_id = a.curso_id AND b.usuario_id = ? " +
                "INNER JOIN curso c ON c.id = a.curso_id AND c.activo = TRUE " +
                "WHERE a.usuario_id = ? LIMIT 1";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioB);
            sentencia.setInt(2, usuarioA);

            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Cursos compartidos, con el estado de inscripción del usuario objetivo.
    public static List<Curso> listarEnComun(int usuarioObjetivo, int usuarioConsulta) {
        String sql = "SELECT c.*, i1.estado AS estado_inscripcion FROM inscripcion i1 " +
                "INNER JOIN inscripcion i2 ON i2.curso_id = i1.curso_id AND i2.usuario_id = ? " +
                "INNER JOIN curso c ON c.id = i1.curso_id AND c.activo = TRUE " +
                "WHERE i1.usuario_id = ? " +
                "ORDER BY c.anio DESC, c.semestre DESC, c.nombre";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioConsulta);
            sentencia.setInt(2, usuarioObjetivo);

            try (ResultSet filas = sentencia.executeQuery()) {
                List<Curso> cursos = new ArrayList<>();
                while (filas.next()) {
                    Curso curso = new Curso(
                            filas.getInt("id"),
                            filas.getString("nombre"),
                            filas.getInt("semestre"),
                            filas.getInt("anio"),
                            filas.getInt("creditos"),
                            filas.getString("descripcion"),
                            filas.getBoolean("activo"));
                    curso.setEstadoInscripcion(filas.getString("estado_inscripcion"));
                    cursos.add(curso);
                }
                return cursos;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Estudiantes con inscripción activa en "Cursando" de un curso, sin incluir al consultante.
    public static List<Usuario> listarCompanierosCursando(int cursoId, int usuarioId) {
        String sql = "SELECT u.* FROM usuario u " +
                "INNER JOIN inscripcion i ON i.usuario_id = u.id " +
                "WHERE i.curso_id = ? AND i.estado = ? AND u.activo = TRUE AND u.id <> ? " +
                "ORDER BY u.nombre";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, cursoId);
            sentencia.setString(2, TipoEstado.Cursando.name());
            sentencia.setInt(3, usuarioId);

            try (ResultSet filas = sentencia.executeQuery()) {
                List<Usuario> companieros = new ArrayList<>();
                while (filas.next()) {
                    companieros.add(new Usuario(
                            filas.getInt("id"),
                            filas.getString("nombre"),
                            filas.getString("correo"),
                            filas.getInt("anio_de_generacion"),
                            TipoRol.valueOf(filas.getString("rol")),
                            filas.getString("contrasenia"),
                            filas.getBoolean("activo")));
                }
                return companieros;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}