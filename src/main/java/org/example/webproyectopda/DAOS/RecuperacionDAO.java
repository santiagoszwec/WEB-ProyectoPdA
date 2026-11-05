package org.example.webproyectopda.DAOS;

import org.example.webproyectopda.ConexionDB;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

// Tokens de un solo uso para recuperar la contraseña.
public class RecuperacionDAO {

    private static final int MINUTOS_VALIDEZ = 30;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    // Crea un token nuevo para el usuario y devuelve el texto que va en el enlace.
    // En la base solo se guarda su hash.
    public static String crear(int usuarioId) {
        byte[] bytes = new byte[32];
        ALEATORIO.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        try (Connection conexion = ConexionDB.obtenerConexion()) {

            // Un solo enlace vigente por usuario: los anteriores quedan inutilizados.
            try (PreparedStatement invalidar = conexion.prepareStatement(
                    "UPDATE recuperacion_contrasenia SET usado = TRUE WHERE usuario_id = ? AND usado = FALSE")) {
                invalidar.setInt(1, usuarioId);
                invalidar.executeUpdate();
            }

            try (PreparedStatement insertar = conexion.prepareStatement(
                    "INSERT INTO recuperacion_contrasenia (usuario_id, token_hash, expira) VALUES (?, ?, ?)")) {
                insertar.setInt(1, usuarioId);
                insertar.setString(2, hash(token));
                insertar.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusMinutes(MINUTOS_VALIDEZ)));
                insertar.executeUpdate();
            }
            return token;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Devuelve el id del usuario si el token existe, no se usó y no venció; si no, null.
    public static Integer usuarioDelToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String sql = "SELECT usuario_id FROM recuperacion_contrasenia "
                + "WHERE token_hash = ? AND usado = FALSE AND expira > ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, hash(token));
            sentencia.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? fila.getInt("usuario_id") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Marca el token como usado. Devuelve true solo si todavía era válido (sirve una sola vez).
    public static boolean consumir(String token) {
        String sql = "UPDATE recuperacion_contrasenia SET usado = TRUE "
                + "WHERE token_hash = ? AND usado = FALSE AND expira > ?";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, hash(token));
            sentencia.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String hash(String token) {
        try {
            byte[] resumen = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : resumen) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}