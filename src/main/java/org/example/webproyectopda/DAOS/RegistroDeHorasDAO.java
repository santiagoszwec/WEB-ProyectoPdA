package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.Modelos.RegistroDeHoras;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistroDeHorasDAO {

    // Total de horas estudiadas agrupado por materia (curso).
    public static class TotalMateria {
        private final String curso;
        private final double totalHoras;

        public TotalMateria(String curso, double totalHoras) {
            this.curso = curso;
            this.totalHoras = totalHoras;
        }

        public String getCurso() {
            return curso;
        }

        public double getTotalHoras() {
            return totalHoras;
        }
    }

    // Horas estudiadas en una fecha concreta (serie para el gráfico de evolución).
    public static class PuntoSerie {
        private final LocalDate fecha;
        private final double horas;

        public PuntoSerie(LocalDate fecha, double horas) {
            this.fecha = fecha;
            this.horas = horas;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public double getHoras() {
            return horas;
        }
    }

    // Registro de horas con el nombre de la materia a la que pertenece, para listarlos en pantalla.
    public static class RegistroConMateria {
        private final RegistroDeHoras registro;
        private final String curso;

        public RegistroConMateria(RegistroDeHoras registro, String curso) {
            this.registro = registro;
            this.curso = curso;
        }

        public RegistroDeHoras getRegistro() {
            return registro;
        }

        public String getCurso() {
            return curso;
        }
    }

    public static boolean crear(RegistroDeHoras registro) {
        String sql = "INSERT INTO registro_de_horas (cantidad_de_horas, fecha, inscripcion_id) VALUES (?,?,?)";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setFloat(1, registro.getCantidadDeHoras());
            sentencia.setObject(2, registro.getFecha());
            sentencia.setInt(3, registro.getInscripcionId());

            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<RegistroConMateria> listarPorUsuario(int usuarioId) {
        String sql = "SELECT r.id, r.cantidad_de_horas, r.fecha, r.inscripcion_id, c.nombre AS curso " +
                "FROM registro_de_horas r " +
                "INNER JOIN inscripcion i ON i.id = r.inscripcion_id " +
                "INNER JOIN curso c ON c.id = i.curso_id " +
                "WHERE i.usuario_id = ? " +
                "ORDER BY r.fecha DESC, r.id DESC";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);

            try (ResultSet filas = sentencia.executeQuery()) {
                List<RegistroConMateria> retorno = new ArrayList<>();
                while (filas.next()) {
                    RegistroDeHoras registro = new RegistroDeHoras(
                            filas.getInt("id"),
                            filas.getFloat("cantidad_de_horas"),
                            filas.getObject("fecha", LocalDate.class),
                            filas.getInt("inscripcion_id"));
                    retorno.add(new RegistroConMateria(registro, filas.getString("curso")));
                }
                return retorno;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<TotalMateria> totalesPorMateria(int usuarioId) {
        String sql = "SELECT c.nombre AS curso, COALESCE(SUM(r.cantidad_de_horas), 0) AS total " +
                "FROM inscripcion i " +
                "INNER JOIN curso c ON c.id = i.curso_id " +
                "LEFT JOIN registro_de_horas r ON r.inscripcion_id = i.id " +
                "WHERE i.usuario_id = ? " +
                "GROUP BY c.id, c.nombre " +
                "ORDER BY c.nombre";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);

            try (ResultSet filas = sentencia.executeQuery()) {
                List<TotalMateria> retorno = new ArrayList<>();
                while (filas.next()) {
                    retorno.add(new TotalMateria(filas.getString("curso"), filas.getDouble("total")));
                }
                return retorno;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PuntoSerie> seriePorFecha(int usuarioId) {
        String sql = "SELECT r.fecha, SUM(r.cantidad_de_horas) AS horas " +
                "FROM registro_de_horas r " +
                "INNER JOIN inscripcion i ON i.id = r.inscripcion_id " +
                "WHERE i.usuario_id = ? " +
                "GROUP BY r.fecha " +
                "ORDER BY r.fecha";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, usuarioId);

            try (ResultSet filas = sentencia.executeQuery()) {
                List<PuntoSerie> retorno = new ArrayList<>();
                while (filas.next()) {
                    retorno.add(new PuntoSerie(
                            filas.getObject("fecha", LocalDate.class),
                            filas.getDouble("horas")));
                }
                return retorno;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
