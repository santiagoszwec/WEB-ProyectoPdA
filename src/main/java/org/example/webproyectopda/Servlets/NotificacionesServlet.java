package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.NotificacionDAO;
import org.example.webproyectopda.ENUMS.TipoNotificacion;
import org.example.webproyectopda.Modelos.Notificacion;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/notificaciones")
public class NotificacionesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        String tipoParam = request.getParameter("tipo");
        String ordenParam = request.getParameter("orden");

        TipoNotificacion tipo = null; // null = "Todos"
        if (tipoParam != null && !tipoParam.isBlank()) {
            try {
                tipo = TipoNotificacion.valueOf(tipoParam);
            } catch (IllegalArgumentException e) {
                request.setAttribute("aviso", "El tipo seleccionado no es válido. Se muestran todos.");
                tipoParam = "";
            }
        }

        boolean masAntiguasPrimero = "asc".equals(ordenParam); // por defecto: más recientes primero
        if (!masAntiguasPrimero) {
            ordenParam = "desc";
        }
        try {
            List<Notificacion> notificaciones =
                    NotificacionDAO.listarPorUsuario(usuario.getId(), tipo, masAntiguasPrimero);
            request.setAttribute("listaNotificaciones", notificaciones);
        } catch (SQLException e) {
            log("Error al listar las notificaciones del usuario " + usuario.getId(), e);
            request.setAttribute("error", "No se pudieron cargar tus notificaciones. Intentá de nuevo más tarde.");
        }


        request.setAttribute("tipos", TipoNotificacion.values());
        request.setAttribute("filtroTipo", tipoParam);
        request.setAttribute("filtroOrden", ordenParam);
        request.getRequestDispatcher("/notificaciones.jsp").forward(request, response);
    }
}