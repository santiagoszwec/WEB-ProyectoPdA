package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.webproyectopda.DAOS.CursoDAO;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;

@WebServlet("/misCursos")
public class MisCursosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");
        request.setAttribute("cursos", CursoDAO.listarPorUsuario(usuario.getId()));
        request.getRequestDispatcher("/misCursos.jsp").forward(request, response);
    }
}