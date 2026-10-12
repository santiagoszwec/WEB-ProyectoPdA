package org.example.webproyectopda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mariadb://localhost:3306/Laboratorio";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    private ConexionDB() {
    }

    public static Connection obtenerConexion() throws SQLException {

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MariaDB en el proyecto", e);
        }

        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
