package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    public static int crear(Usuario usuario) {
        try {
            Connection conexion = ConexionDB.obtenerConexion();

            String sql = "INSERT INTO usuario (nombre, correo, anio_de_generacion, rol, contrasenia) VALUES (?,?,?,?,?)";

            PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getCorreo());
            sentencia.setInt(3, usuario.getAnioDeGeneracion());
            sentencia.setString(4, usuario.getRol().toString());
            sentencia.setString(5, usuario.getContrasenia());
            sentencia.executeUpdate();

            ResultSet claves = sentencia.getGeneratedKeys();

            if (claves.next()) {
                return claves.getInt(1);
            }

            throw new RuntimeException("No se pudo obtener el ID del curso.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Usuario> listarTodos() {
        try {
            Connection conexion = ConexionDB.obtenerConexion();

            String sql = "SELECT * FROM usuario ORDER BY nombre";
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet filas = sentencia.executeQuery();

            List<Usuario> retorno = new ArrayList<>();

            while (filas.next()) {
                int id = filas.getInt("id");
                String nombre = filas.getString("nombre");
                String correo = filas.getString("correo");
                int anioGeneracion = filas.getInt("anio_de_generacion");
                TipoRol rol = TipoRol.valueOf(filas.getString("rol"));
                String contrasenia = filas.getString("contrasenia");
                boolean activo = filas.getBoolean("activo");

                Usuario usuario = new Usuario(id, nombre, correo, anioGeneracion, rol, contrasenia, activo);
                retorno.add(usuario);
            }
            return retorno;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Usuario> listarActivos() {
        try {
            Connection conexion = ConexionDB.obtenerConexion();

            String sql = "SELECT * FROM usuario WHERE activo = TRUE ORDER BY nombre";
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet filas = sentencia.executeQuery();

            List<Usuario> retorno = new ArrayList<>();

            while (filas.next()) {
                Usuario usuario = new Usuario(
                        filas.getInt("id"),
                        filas.getString("nombre"),
                        filas.getString("correo"),
                        filas.getInt("anio_de_generacion"),
                        TipoRol.valueOf(filas.getString("rol")),
                        filas.getString("contrasenia"),
                        filas.getBoolean("activo")
                );

                retorno.add(usuario);
            }

            return retorno;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre = ?, correo = ?, anio_de_generacion = ?, rol = ?, contrasenia = ? WHERE id = ?";
        try {
            Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getCorreo());
            sentencia.setInt(3, usuario.getAnioDeGeneracion());
            sentencia.setString(4, usuario.getRol().toString());
            sentencia.setString(5, usuario.getContrasenia());
            sentencia.setInt(6, usuario.getId());

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean desactivar(int id) {
        try {
            Connection conexion = ConexionDB.obtenerConexion();

            String sql = "UPDATE usuario SET activo = FALSE WHERE id = ?";

            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Usuario iniciarSesion(String correo, String contrasenia){
        try{
            Connection conexion = ConexionDB.obtenerConexion();

            String sql = "SELECT * FROM usuario WHERE correo = ? AND contrasenia = ?";
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setString(1, correo);
            sentencia.setString(2, contrasenia);

            ResultSet fila = sentencia.executeQuery();
            if(fila.next()){
                boolean activo = fila.getBoolean("activo");

                return new Usuario(
                        fila.getInt("id"),
                        fila.getString("nombre"),
                        fila.getString("correo"),
                        fila.getInt("anio_de_generacion"),
                        TipoRol.valueOf(fila.getString("rol")),
                        fila.getString("contrasenia"),
                        activo);
            }
            else{
                return null;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static boolean cambiarRol(int userId, TipoRol nuevoRol) {
        try {
            Connection conexion = ConexionDB.obtenerConexion();


            String sql = "UPDATE usuario SET rol = ? WHERE id = ?";

            PreparedStatement sentencia = conexion.prepareStatement(sql);

            sentencia.setString(1, nuevoRol.toString());

            sentencia.setInt(2, userId);

            return sentencia.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Usuario> buscarPorNombre(String nombre) {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT * FROM usuario " +
                "WHERE activo = TRUE AND nombre LIKE ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, "%" + nombre + "%");

            ResultSet fila = sentencia.executeQuery();

            while (fila.next()) {
                usuarios.add(new Usuario(
                        fila.getInt("id"),
                        fila.getString("nombre"),
                        fila.getString("correo"),
                        fila.getInt("anio_de_generacion"),
                        TipoRol.valueOf(fila.getString("rol")),
                        fila.getString("contrasenia"),
                        fila.getBoolean("activo")));
            }

            return usuarios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Usuario buscarPorCorreo(String correo) {
        try {
            Connection conexion = ConexionDB.obtenerConexion();

            String sql = "SELECT * FROM usuario WHERE correo = ?";
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, correo);

            ResultSet fila = sentencia.executeQuery();

            if (fila.next()) {
                return new Usuario(
                        fila.getInt("id"),
                        fila.getString("nombre"),
                        fila.getString("correo"),
                        fila.getInt("anio_de_generacion"),
                        TipoRol.valueOf(fila.getString("rol")),
                        fila.getString("contrasenia"),
                        fila.getBoolean("activo"));
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Usuario> filtrarPorRol(String rol) {

        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE activo = TRUE AND rol = ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, rol);
            ResultSet fila = sentencia.executeQuery();

            while (fila.next()) {
                usuarios.add(new Usuario(
                        fila.getInt("id"),
                        fila.getString("nombre"),
                        fila.getString("correo"),
                        fila.getInt("anio_de_generacion"),
                        TipoRol.valueOf(fila.getString("rol")),
                        fila.getString("contrasenia"),
                        fila.getBoolean("activo")));
            }
            return usuarios;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}