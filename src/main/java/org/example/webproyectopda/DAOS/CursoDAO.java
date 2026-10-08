package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.Modelos.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    public static boolean crear(Curso curso) {
        String sql = "INSERT INTO curso (nombre, semestre, anio, creditos, descripcion) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conexion = ConexionDB.obtenerConexion();

            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setString(1, curso.getNombre());
            sentencia.setInt(2, curso.getSemestre());
            sentencia.setInt(3, curso.getAnio());
            sentencia.setInt(4, curso.getCreditos());
            sentencia.setString(5, curso.getDescripcion());

            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean desactivar(int id) {

        String sql = "UPDATE curso SET activo = FALSE WHERE id = ?";

        try (
                Connection conexion = ConexionDB.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)
        ) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Curso> listarTodos() {

        String sql = "SELECT * FROM curso WHERE activo = TRUE ORDER BY nombre";

        try (Connection conexion = ConexionDB.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {

            List<Curso> cursos = new ArrayList<>();

            while (resultado.next()) {

                Curso curso = new Curso(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getInt("semestre"),
                        resultado.getInt("anio"),
                        resultado.getInt("creditos"),
                        resultado.getString("descripcion"),
                        resultado.getBoolean("activo"));
                cursos.add(curso);
            }
            return cursos;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean actualizar(Curso curso){
        String sql = "UPDATE curso SET nombre= ?, semestre = ?, anio = ?, creditos = ?, descripcion = ? WHERE id = ?";

        try{
            Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, curso.getNombre());
            sentencia.setInt(2, curso.getSemestre());
            sentencia.setInt(3, curso.getAnio());
            sentencia.setInt(4, curso.getCreditos());
            sentencia.setString(5, curso.getDescripcion());
            sentencia.setInt(6, curso.getId());

            return sentencia.executeUpdate() == 1;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public static Curso buscarPorId(int id) {
        String sql = "SELECT * FROM curso WHERE id = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return new Curso(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getInt("semestre"),
                            resultado.getInt("anio"),
                            resultado.getInt("creditos"),
                            resultado.getString("descripcion"),
                            resultado.getBoolean("activo")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}