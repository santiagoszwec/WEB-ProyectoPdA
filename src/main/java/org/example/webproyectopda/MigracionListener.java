package org.example.webproyectopda;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Ejecuta la migración una sola vez al arrancar la aplicación.
 * Así las tablas se crean solas y no hace falta correr Migracion.main() a mano.
 */
@WebListener
public class MigracionListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        evento.getServletContext().log("Iniciando migración de la base de datos...");
        Migracion.migrar();
        evento.getServletContext().log("Migración de la base de datos finalizada.");
    }
}
