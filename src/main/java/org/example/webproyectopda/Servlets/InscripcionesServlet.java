package org.example.webproyectopda.Servlets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.webproyectopda.DAOS.CursoDAO;
import org.example.webproyectopda.DAOS.InscripcionDAO;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.time.Year;

@WebServlet("/inscripciones")
public class InscripcionesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");
        int anioActual = Year.now().getValue();

        request.setAttribute("cursos", CursoDAO.listarDisponiblesParaInscripcion(usuario.getId(), anioActual));

        request.setAttribute("anioActual", anioActual);
        request.getRequestDispatcher("/inscripciones.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        try {
            int cursoId = Integer.parseInt(request.getParameter("cursoId"));

            boolean inscrito = InscripcionDAO.inscribirSiNoExiste(usuario.getId(), cursoId, Year.now().getValue());

            response.sendRedirect(request.getContextPath()
                    + "/inscripciones?resultado="
                    + (inscrito ? "exito" : "error"));

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath()
                    + "/inscripciones?resultado=error");
        }
    }
}