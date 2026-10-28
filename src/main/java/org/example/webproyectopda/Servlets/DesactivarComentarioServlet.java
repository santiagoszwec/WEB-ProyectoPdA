package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.webproyectopda.DAOS.ComentarioDAO;
import org.example.webproyectopda.DAOS.DudaDAO;
import org.example.webproyectopda.DAOS.InscripcionDAO;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Comentario;
import org.example.webproyectopda.Modelos.Duda;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/comentario/desactivar")
public class DesactivarComentarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        int comentarioId;
        int dudaId;

        try {
            comentarioId = Integer.parseInt(request.getParameter("comentarioId"));
            dudaId = Integer.parseInt(request.getParameter("dudaId"));
        } catch (NumberFormatException | NullPointerException e) {
            sesion.setAttribute("dudaError", "Los datos del comentario no son válidos");
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }

        try {
            Duda duda = DudaDAO.buscarPorId(dudaId);
            Comentario comentario = ComentarioDAO.buscarPorId(comentarioId);

            if (duda == null || duda.isDadaDeBaja()
                    || comentario == null
                    || comentario.getPublicacionId() != dudaId
                    || !comentario.isActiva()) {

                sesion.setAttribute("dudaError", "El comentario no existe o no pertenece a esta duda");
                response.sendRedirect(request.getContextPath() + "/duda?id=" + dudaId);
                return;
            }

            boolean puedeVerCurso = usuario.getRol() == TipoRol.Admin
                    || InscripcionDAO.existeInscripcion(usuario.getId(), duda.getCursoId());

            if (!puedeVerCurso) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            if (comentario.getUsuarioId() != usuario.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            boolean eliminado = ComentarioDAO.desactivarPropio(
                    comentarioId, usuario.getId());

            if (eliminado) {
                sesion.setAttribute("dudaMensaje", "Comentario eliminado correctamente ");
            } else {
                sesion.setAttribute("dudaError", "No se pudo eliminar el comentario ");
            }

        } catch (SQLException | RuntimeException e) {
            log("Error al eliminar el comentario " + comentarioId, e);
            sesion.setAttribute("dudaError", "Ocurrió un error al eliminar el comentario ");
        }

        response.sendRedirect(request.getContextPath() + "/duda?id=" + dudaId);
    }
}
