package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.webproyectopda.DAOS.CursoDAO;
import org.example.webproyectopda.ENUMS.TipoEstado;
import org.example.webproyectopda.Modelos.Curso;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.util.List;

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

        String buscar = request.getParameter("buscar");
        String estadoTexto = request.getParameter("estado");

        TipoEstado estado = null;
        if (estadoTexto != null && !estadoTexto.isBlank()) {
            try {
                estado = TipoEstado.valueOf(estadoTexto);
            } catch (IllegalArgumentException e) {
                // valor inválido. Si llega el valor escrito manualmente en la URL, no se filtra
            }
        }

        //lista filtrada junto al total de cursos que tiene  el usuario
        List<Curso> cursos = CursoDAO.listarPorUsuarioFiltrado(usuario.getId(), buscar, estado);
        int totalCursos = CursoDAO.listarPorUsuario(usuario.getId()).size();

        request.setAttribute("cursos", cursos);
        request.setAttribute("totalCursos", totalCursos);
        request.setAttribute("hayFiltros", (buscar != null && !buscar.isBlank()) || estado != null);
        // el formulario conserva lo que la persona habia elegido
        request.setAttribute("buscar", buscar);
        request.setAttribute("estadoElegido", estado == null ? "" : estado.name());

        request.getRequestDispatcher("/misCursos.jsp").forward(request, response);


    }
}