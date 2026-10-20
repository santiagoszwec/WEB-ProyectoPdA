package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;

// Pantalla de administración: listado de publicaciones para editarlas.
@WebServlet("/gestionPublicaciones")
public class GestionPublicacionesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        Usuario usuario = sesion == null
                ? null
                : (Usuario) sesion.getAttribute("usuarioLogueado");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != TipoRol.Admin) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Esta sección es solo para administradores");
            return;
        }

        Object exito = sesion.getAttribute("mensajeExito");
        if (exito != null) {
            request.setAttribute("mensajeExito", exito);
            sesion.removeAttribute("mensajeExito");
        }

        request.setAttribute("publicaciones", PublicacionDAO.listarTodas());
        request.getRequestDispatcher("/gestionPublicaciones.jsp").forward(request, response);
    }
}
