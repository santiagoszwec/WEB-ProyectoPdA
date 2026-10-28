package org.example.webproyectopda.Filtros;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.NotificacionDAO;
import org.example.webproyectopda.Modelos.Notificacion;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Carga, en cada página, los datos de la campanita del header:
 *  - cabNotifTotal: cuántas notificaciones tiene el usuario
 *  - cabNotifRecientes: las últimas (máximo MAX_EN_LISTA) para el desplegable
 */
@WebFilter("/*")
public class NotificacionesCabeceraFilter implements Filter {

    private static final int MAX_EN_LISTA = 5;

    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) solicitud;

        if (!esRecursoEstatico(request)) {
            HttpSession sesion = request.getSession(false);
            Object logueado = (sesion == null) ? null : sesion.getAttribute("usuarioLogueado");

            if (logueado instanceof Usuario) {
                Usuario usuario = (Usuario) logueado;
                try {
                    List<Notificacion> todas = NotificacionDAO.listarPorUsuario(usuario.getId(), null, false);
                    request.setAttribute("cabNotifTotal", todas.size());
                    request.setAttribute("cabNotifRecientes",
                            todas.subList(0, Math.min(MAX_EN_LISTA, todas.size())));
                } catch (SQLException e) {
                    // Si falla, la campanita queda en 0: la página sigue funcionando igual
                    request.getServletContext().log(
                            "No se pudieron cargar las notificaciones del header (usuario " + usuario.getId() + ")", e);
                }
            }
        }

        cadena.doFilter(solicitud, respuesta);
    }

    private boolean esRecursoEstatico(HttpServletRequest request) {
        String ruta = request.getRequestURI().toLowerCase();
        return ruta.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|webp|woff2?|map)$");
    }
}