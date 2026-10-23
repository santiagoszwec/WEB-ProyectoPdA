package org.example.webproyectopda.Servlets;

import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.Modelos.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/publicacion/desactivar")
public class DesactivarPublicacionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || !(sesion.getAttribute("usuarioLogueado") instanceof Usuario)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        try {
            int publicacionId = Integer.parseInt(request.getParameter("publicacionId"));

            int cursoId = Integer.parseInt(request.getParameter("cursoId"));

            boolean desactivada = PublicacionDAO.desactivarPropia(publicacionId, usuario.getId());

            if (desactivada) {
                sesion.setAttribute("mensajeExito", "La publicación se eliminó correctamente.");
            } else {
                sesion.setAttribute("mensajeError", "No se pudo eliminar la publicación.");
            }

            response.sendRedirect(request.getContextPath() + "/curso?id=" + cursoId);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificador inválido.");
        }
    }
}