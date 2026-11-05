package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.webproyectopda.DAOS.CursoDAO;
import org.example.webproyectopda.DAOS.ProgresoDAO;
import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.Modelos.Curso;
import org.example.webproyectopda.Modelos.ResumenEstudio;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/inicio", ""})
public class InicioServlet extends HttpServlet {

    private static final int MAX_CURSOS_EN_TARJETA = 5;
    private static final int MAX_MATERIAS_EN_GRAFICO = 5;
    private static final int MAX_BUSQUEDA = 100;

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

        String materia = request.getParameter("materia");
        materia = materia == null ? "" : materia.trim();
        if (materia.length() > MAX_BUSQUEDA) {
            materia = materia.substring(0, MAX_BUSQUEDA);
        }
        request.setAttribute("materiaBuscada", materia);

        try {
            request.setAttribute("publicaciones", PublicacionDAO.listarUltimas(usuario.getId(), materia));
        } catch (RuntimeException e) {
            log("Error al cargar el inicio del usuario " + usuario.getId(), e);
            request.setAttribute("error", "No se pudieron cargar las últimas publicaciones. Intentá de nuevo más tarde.");
        }

        try {
            List<Curso> cursando = new ArrayList<>();
            List<Curso> encontrados = new ArrayList<>();
            for (Curso curso : CursoDAO.listarPorUsuario(usuario.getId())) {
                if ("Cursando".equals(curso.getEstadoInscripcion())) {
                    cursando.add(curso);
                }
                if (!materia.isEmpty() && normalizar(curso.getNombre()).contains(normalizar(materia))) {
                    encontrados.add(curso);
                }
            }
            request.setAttribute("cursosEncontrados", encontrados);
            request.setAttribute("totalCursando", cursando.size());
            request.setAttribute("cursosCursando",
                    cursando.size() > MAX_CURSOS_EN_TARJETA
                            ? cursando.subList(0, MAX_CURSOS_EN_TARJETA)
                            : cursando);
        } catch (RuntimeException e) {
            log("Error al cargar los cursos del inicio del usuario " + usuario.getId(), e);
        }

        try {
            request.setAttribute("estudio",
                    new ResumenEstudio(ProgresoDAO.horasPorMateria(usuario.getId()), MAX_MATERIAS_EN_GRAFICO));
        } catch (RuntimeException e) {
            log("Error al cargar las horas de estudio del inicio del usuario " + usuario.getId(), e);
        }

        request.getRequestDispatcher("/inicio.jsp").forward(request, response);
    }

    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase();
    }
}