package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.webproyectopda.DAOS.InscripcionDAO;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;

@WebServlet("/bajaInscripcion")
public class BajaInscripcionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || !(sesion.getAttribute("usuarioLogueado") instanceof Usuario usuario)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int cursoId = Integer.parseInt(request.getParameter("cursoId"));

            boolean eliminada = InscripcionDAO.eliminarInscripcion(usuario.getId(), cursoId);

            if (eliminada) {
                sesion.setAttribute("mensajeCursos", "Te diste de baja del curso correctamente");
            } else {
                sesion.setAttribute("mensajeCursos", "No se encontró tu inscripción en ese curso");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Curso no válido");
            return;
        } catch (RuntimeException e) {
            log("Error al eliminar la inscripción", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo eliminar la inscripción");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/misCursos");
    }
}