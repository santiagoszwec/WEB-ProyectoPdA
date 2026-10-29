
package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.webproyectopda.DAOS.CursoDAO;
import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.Modelos.Curso;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/inicio", ""})
public class InicioServlet extends HttpServlet {

    private static final int MAX_CURSOS_EN_TARJETA = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");
        request.setAttribute("usuario", usuario);
        try {
            request.setAttribute("publicaciones", PublicacionDAO.listarUltimas(usuario.getId()));
        } catch (RuntimeException e) {
            log("Error al cargar el inicio del usuario " + usuario.getId(), e);
            request.setAttribute("error", "No se pudieron cargar las últimas publicaciones. Intentá de nuevo más tarde.");
        }

        try {
            List<Curso> cursando = new ArrayList<>();
            for (Curso curso : CursoDAO.listarPorUsuario(usuario.getId())) {
                if ("Cursando".equals(curso.getEstadoInscripcion())) {
                    cursando.add(curso);
                }
            }
            request.setAttribute("totalCursando", cursando.size());
            request.setAttribute("cursosCursando",
                    cursando.size() > MAX_CURSOS_EN_TARJETA
                            ? cursando.subList(0, MAX_CURSOS_EN_TARJETA)
                            : cursando);
        } catch (RuntimeException e) {
            log("Error al cargar los cursos del inicio del usuario " + usuario.getId(), e);
        }


        request.getRequestDispatcher("/inicio.jsp").forward(request, response);
    }
}