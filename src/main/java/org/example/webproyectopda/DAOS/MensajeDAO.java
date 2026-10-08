package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;
import org.example.webproyectopda.ENUMS.TipoCategoria;
import org.example.webproyectopda.Modelos.Mensaje;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MensajeDAO {

    public static boolean crear(Mensaje mensaje) {
        String sqlPublicacion = "INSERT INTO publicacion (mensaje, imagen_url, fecha_publicacion, usuario_id, curso_id) VALUES (?,?,?,?,?)";
        String sqlMensaje = "INSERT INTO mensaje (id, categoria) VALUES (?,?)";

        try {
            Connection conexion = ConexionDB.obtenerConexion();

            PreparedStatement sentenciaPublicacion = conexion.prepareStatement(sqlPublicacion, Statement.RETURN_GENERATED_KEYS);
            sentenciaPublicacion.setString(1, mensaje.getMensaje());
            sentenciaPublicacion.setString(2, mensaje.getImagenUrl());
            sentenciaPublicacion.setObject(3, mensaje.getFechaPublicacion());
            sentenciaPublicacion.setInt(4, mensaje.getUsuarioId());
            sentenciaPublicacion.setInt(5, mensaje.getCursoId());
            sentenciaPublicacion.executeUpdate();

            ResultSet generadas = sentenciaPublicacion.getGeneratedKeys();
            if (!generadas.next()) {
                return false;
            }

            int id = generadas.getInt(1);
            mensaje.setId(id);

            PreparedStatement sentenciaMensaje = conexion.prepareStatement(sqlMensaje);
            sentenciaMensaje.setInt(1, id);
            sentenciaMensaje.setString(2, mensaje.getCategoria().name());

            return sentenciaMensaje.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Mensaje> listarTodos() {
        String sql = "SELECT p.id, p.mensaje, p.imagen_url, p.fecha_publicacion, p.activa, p.usuario_id, p.curso_id, m.categoria " +
                "FROM publicacion p JOIN mensaje m ON m.id = p.id WHERE p.activa = TRUE ORDER BY p.fecha_publicacion";

        try {
            Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql);

            ResultSet filas = sentencia.executeQuery();

            List<Mensaje> retorno = new ArrayList<>();

            while (filas.next()) {
                retorno.add(mapearMensaje(filas));
            }

            return retorno;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mensaje mapearMensaje(ResultSet filas) throws SQLException {
        int id = filas.getInt("id");
        String texto = filas.getString("mensaje");
        String imagenUrl = filas.getString("imagen_url");
        LocalDate fechaPublicacion = filas.getObject("fecha_publicacion", LocalDate.class);
        boolean activa = filas.getBoolean("activa");
        int usuarioId = filas.getInt("usuario_id");
        int cursoId = filas.getInt("curso_id");
        TipoCategoria categoria = TipoCategoria.valueOf(filas.getString("categoria"));


        return new Mensaje(id, texto, imagenUrl, fechaPublicacion, !activa, usuarioId, cursoId, categoria);
    }
}
