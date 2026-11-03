
package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.webproyectopda.DAOS.ReporteDAO;
import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.DAOS.ComentarioDAO;
import org.example.webproyectopda.DAOS.NotificacionDAO;

import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.ENUMS.TipoNotificacion;

import org.example.webproyectopda.Modelos.Reporte;
import org.example.webproyectopda.Modelos.Usuario;
import org.example.webproyectopda.Modelos.Notificacion;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/reportes")
public class ReporteServlet extends HttpServlet {

    private boolean esAdministrador(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario.getRol() != TipoRol.Admin) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esAdministrador(request, response)) {
            return;
        }

        List<Reporte> reportes = ReporteDAO.listarReportesAbiertos();
        request.setAttribute("reportes", reportes);

        HttpSession session = request.getSession();
        request.setAttribute("mensaje", session.getAttribute("mensajeReportes"));
        session.removeAttribute("mensajeReportes");

        request.getRequestDispatcher("/reportes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!esAdministrador(request, response)) {
            return;
        }

        HttpSession session = request.getSession();

        try {
            int reporteId = Integer.parseInt(request.getParameter("reporteId"));
            String accion = request.getParameter("accion");
            String motivo = request.getParameter("motivo");

            Reporte reporte = ReporteDAO.buscarPorId(reporteId);

            if (reporte == null || reporte.getFechaResolucion() != null) {
                session.setAttribute("mensajeReportes", "El reporte no existe o ya fue procesado.");
                response.sendRedirect(request.getContextPath() + "/reportes");
                return;
            }

            boolean procesado = false;

            if ("rechazar".equals(accion)) {

                String resolucion = (motivo == null || motivo.isBlank())
                        ? "Reporte rechazado por el administrador"
                        : "Reporte rechazado. Motivo: " + motivo;

                procesado = ReporteDAO.resolver(reporteId, resolucion);

                if (procesado) {
                    Notificacion notificacion = new Notificacion(LocalDate.now(),
                            TipoNotificacion.Reporte,
                            "Tu reporte #" + reporteId + " fue rechazado. " + resolucion,
                            reporte.getUsuarioId(),
                            reporte.getPublicacionId());

                    NotificacionDAO.crear(notificacion);
                }

            } else if ("aprobar".equals(accion)) {

                if (motivo == null || motivo.isBlank()) {
                    session.setAttribute("mensajeReportes", "Debés indicar el motivo de la baja.");
                    response.sendRedirect(request.getContextPath() + "/reportes");
                    return;
                }

                if (reporte.getPublicacionId() != null) {
                    procesado = PublicacionDAO.darDeBaja(reporte.getPublicacionId(), motivo);

                } else if (reporte.getComentarioId() != null) {
                    procesado = ComentarioDAO.darDeBaja(reporte.getComentarioId(), motivo);
                }

                if (procesado) {
                    Notificacion notificacion = new Notificacion(LocalDate.now(),
                            TipoNotificacion.Reporte,
                            "Tu reporte #" + reporteId + " fue aprobado. El contenido denunciado fue dado de baja.",
                            reporte.getUsuarioId(),
                            reporte.getPublicacionId());

                    NotificacionDAO.crear(notificacion);
                }

            } else {
                session.setAttribute("mensajeReportes", "La acción seleccionada no es válida.");
                response.sendRedirect(request.getContextPath() + "/reportes");
                return;
            }
            session.setAttribute("mensajeReportes", procesado
                            ? "El reporte fue procesado correctamente."
                            : "No se pudo procesar el reporte.");

        } catch (NumberFormatException e) {
            session.setAttribute("mensajeReportes", "El identificador del reporte no es válido.");
        }
        response.sendRedirect(request.getContextPath() + "/reportes");
    }
}
