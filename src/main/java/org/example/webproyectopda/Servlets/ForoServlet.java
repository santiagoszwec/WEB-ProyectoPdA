package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.ENUMS.TipoCategoria;
import org.example.webproyectopda.Modelos.PublicacionFeed;
import org.example.webproyectopda.Modelos.Usuario;
import org.example.webproyectopda.Utilidades.Validaciones;

import java.io.IOException;
import java.util.List;

// Foro: búsqueda de dudas en todos los cursos donde el usuario está inscripto.
@WebServlet("/foro")
public class ForoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        String texto = request.getParameter("q");
        texto = texto == null ? "" : texto.trim();

        String categoria = request.getParameter("categoria");
        categoria = categoria == null ? "" : categoria.trim();

        // Valores inválidos de categoría se ignoran en lugar de romper la consulta.
        if (!categoria.isEmpty() && !Validaciones.enumValido(TipoCategoria.class, categoria)) {
            categoria = "";
        }

        boolean busco = request.getParameter("enviado") != null;

        List<PublicacionFeed> resultados =
                PublicacionDAO.buscarDudas(usuario.getId(),
                        texto.isEmpty() ? null : texto,
                        categoria.isEmpty() ? null : categoria);

        request.setAttribute("resultados", resultados);
        request.setAttribute("q", texto);
        request.setAttribute("categoria", categoria);
        request.setAttribute("busco", busco);
        request.setAttribute("categoriasDisponibles", TipoCategoria.values());

        request.getRequestDispatcher("/foro.jsp").forward(request, response);
    }
}
