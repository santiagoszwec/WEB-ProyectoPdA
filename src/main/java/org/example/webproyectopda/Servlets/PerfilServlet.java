package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.InscripcionDAO;
import org.example.webproyectopda.DAOS.ProgresoDAO;
import org.example.webproyectopda.DAOS.RegistroDeHorasDAO;
import org.example.webproyectopda.DAOS.UsuarioDAO;
import org.example.webproyectopda.Modelos.Curso;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.util.List;

// Perfil público de un estudiante: solo se puede consultar si ambos comparten
// al menos un curso (precondición del caso de uso), verificada en el servidor.
@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        int estudianteId;
        try {
            estudianteId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Estudiante no válido");
            return;
        }

        Usuario estudiante = UsuarioDAO.buscarPorId(estudianteId);
        if (estudiante == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "El estudiante no existe o fue desactivado");
            return;
        }

        if (!InscripcionDAO.existeCursoEnComun(usuario.getId(), estudiante.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No compartís ninguna materia con este estudiante");
            return;
        }

        List<Curso> materias = InscripcionDAO.listarEnComun(estudiante.getId(), usuario.getId());
        List<RegistroDeHorasDAO.TotalMateria> progreso =
                ProgresoDAO.horasPorMateria(estudiante.getId());

        request.setAttribute("estudiante", estudiante);
        request.setAttribute("materias", materias);
        request.setAttribute("progreso", progreso);
        request.setAttribute("totalHoras", ProgresoDAO.totalHoras(estudiante.getId()));
        request.setAttribute("esPropio", estudiante.getId() == usuario.getId());

        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }
}
