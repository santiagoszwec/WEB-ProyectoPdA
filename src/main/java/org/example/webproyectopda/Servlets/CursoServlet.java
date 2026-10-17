package org.example.webproyectopda.Servlets;

import org.example.webproyectopda.DAOS.CursoDAO;
import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.Modelos.Curso;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/curso")
public class CursoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion == null ||
                sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int idCurso = Integer.parseInt(request.getParameter("id"));
            Curso curso = CursoDAO.buscarPorId(idCurso);

            if (curso == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Curso no encontrado");
                return;
            }

            request.setAttribute("curso", curso);
            request.setAttribute("publicaciones", PublicacionDAO.listarPorCurso(idCurso));

            request.getRequestDispatcher("/curso.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "ID de curso inválido");
        }
    }
}