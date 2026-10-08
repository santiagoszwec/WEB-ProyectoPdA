package org.example.webproyectopda;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class Migracion {

    public static void migrar() {

        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement()) {

            String[] tablas = {

                    "CREATE TABLE IF NOT EXISTS usuario (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  nombre VARCHAR(100) NOT NULL," +
                            "  correo VARCHAR(100) NOT NULL," +
                            "  anio_de_generacion INT NOT NULL," +
                            "  rol VARCHAR(20) NOT NULL," +
                            "  contrasenia VARCHAR(255) NOT NULL," +
                            "  activo BOOLEAN NOT NULL DEFAULT TRUE" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS curso (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  nombre VARCHAR(100) NOT NULL," +
                            "  semestre INT NOT NULL," +
                            "  anio INT NOT NULL," +
                            "  creditos INT NOT NULL," +
                            "  descripcion TEXT NOT NULL," +
                            "  activo BOOLEAN NOT NULL DEFAULT TRUE" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS inscripcion (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  estado VARCHAR(20) NOT NULL," +
                            "  usuario_id INT NOT NULL," +
                            "  curso_id INT NOT NULL," +
                            "  FOREIGN KEY (usuario_id) REFERENCES usuario(id)," +
                            "  FOREIGN KEY (curso_id) REFERENCES curso(id)," +
                            "  UNIQUE (usuario_id, curso_id)" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS registro_de_horas (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  cantidad_de_horas FLOAT NOT NULL," +
                            "  fecha DATE NOT NULL," +
                            "  inscripcion_id INT NOT NULL," +
                            "  FOREIGN KEY (inscripcion_id) REFERENCES inscripcion(id) ON DELETE CASCADE" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS publicacion (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  mensaje TEXT NOT NULL," +
                            "  imagen_url VARCHAR(255)," +
                            "  fecha_publicacion DATE NOT NULL," +
                            "  activa BOOLEAN NOT NULL DEFAULT TRUE," +
                            "  usuario_id INT," +
                            "  curso_id INT NOT NULL," +
                            "  FOREIGN KEY (usuario_id) REFERENCES usuario(id)," +
                            "  FOREIGN KEY (curso_id) REFERENCES curso(id)" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS mensaje (" +
                            "  id INT PRIMARY KEY," +
                            "  categoria VARCHAR(50) NOT NULL," +
                            "  FOREIGN KEY (id) REFERENCES publicacion(id) ON DELETE CASCADE" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS duda (" +
                            "  id INT PRIMARY KEY," +
                            "  estado VARCHAR(20) NOT NULL," +
                            "  categoria VARCHAR(50) NOT NULL," +
                            "  FOREIGN KEY (id) REFERENCES publicacion(id) ON DELETE CASCADE" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS comentario (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  mensaje TEXT NOT NULL," +
                            "  imagen_url VARCHAR(255)," +
                            "  fecha_publicacion DATE NOT NULL," +
                            "  usuario_id INT NOT NULL," +
                            "  publicacion_id INT NOT NULL," +
                            "  comentario_padre_id INT," +
                            "  destacado BOOLEAN NOT NULL DEFAULT FALSE," +
                            "  activa BOOLEAN NOT NULL DEFAULT TRUE," +
                            "  FOREIGN KEY (usuario_id) REFERENCES usuario(id)," +
                            "  FOREIGN KEY (publicacion_id) REFERENCES publicacion(id) ON DELETE CASCADE," +
                            "  FOREIGN KEY (comentario_padre_id) REFERENCES comentario(id)" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS material (" +
                            "  id INT PRIMARY KEY," +
                            "  archivo_url VARCHAR(255) NOT NULL," +
                            "  tipo_material VARCHAR(50) NOT NULL," +
                            "  tipo_archivo VARCHAR(50) NOT NULL," +
                            "  tema VARCHAR(100) NOT NULL," +
                            "  FOREIGN KEY (id) REFERENCES publicacion(id) ON DELETE CASCADE" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS notificacion (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  fecha DATE NOT NULL," +
                            "  tipo VARCHAR(20) NOT NULL," +
                            "  mensaje TEXT NOT NULL," +
                            "  usuario_id INT NOT NULL," +
                            "  publicacion_id INT," +
                            "  FOREIGN KEY (usuario_id) REFERENCES usuario(id)," +
                            "  FOREIGN KEY (publicacion_id) REFERENCES publicacion(id)" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS suspension (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  usuario_id INT NOT NULL," +
                            "  motivo TEXT NOT NULL," +
                            "  fecha_suspension DATE NOT NULL," +
                            "  fecha_fin DATE NOT NULL," +
                            "  activa BOOLEAN NOT NULL DEFAULT TRUE," +
                            "  FOREIGN KEY (usuario_id) REFERENCES usuario(id)" +
                            ")",

                    "CREATE TABLE IF NOT EXISTS reporte (" +
                            "  id INT AUTO_INCREMENT PRIMARY KEY," +
                            "  contenido TEXT NOT NULL," +
                            "  motivo TEXT NOT NULL," +
                            "  resolucion TEXT," +
                            "  fecha_reporte DATE NOT NULL," +
                            "  fecha_resolucion DATE," +
                            "  usuario_id INT NOT NULL," +
                            "  publicacion_id INT," +
                            "  comentario_id INT," +
                            "  FOREIGN KEY (usuario_id) REFERENCES usuario(id)," +
                            "  FOREIGN KEY (publicacion_id) REFERENCES publicacion(id)," +
                            "  FOREIGN KEY (comentario_id) REFERENCES comentario(id) ON DELETE CASCADE" +
                            ")"
            };


            for (String sql : tablas) {
                stmt.executeUpdate(sql);

                String nombreTabla = sql.split("IF NOT EXISTS ")[1].split(" \\(")[0];

                System.out.println("Tabla creada: " + nombreTabla);
            }

            System.out.println("Migracion completada exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error en la migracion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        migrar();
    }
}