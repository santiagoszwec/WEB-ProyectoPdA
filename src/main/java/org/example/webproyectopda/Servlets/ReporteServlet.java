
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

        int porPagina = 10;
        int pagina = 1;

        try {
            String paginaParam = request.getParameter("pagina");

            if (paginaParam != null) {
                pagina = Integer.parseInt(paginaParam);
            }
        } catch (NumberFormatException e) {
            pagina = 1;
        }

        if (pagina < 1) {
            pagina = 1;
        }

        int totalReportes = ReporteDAO.contarReportesAbiertos();

        int totalPaginas = (int) Math.ceil((double) totalReportes / porPagina);

        if (totalPaginas == 0) {
            totalPaginas = 1;
        }

        if (pagina > totalPaginas) {
            pagina = totalPaginas;
        }

        int offset = (pagina - 1) * porPagina;

        List<Reporte> reportes =
                ReporteDAO.listarReportesAbiertos(porPagina, offset);

        request.setAttribute("reportes", reportes);
        request.setAttribute("paginaActual", pagina);
        request.setAttribute("totalPaginas", totalPaginas);
        request.setAttribute("totalReportes", totalReportes);

        int desde = totalReportes == 0 ? 0 : offset + 1;
        int hasta = Math.min(offset + porPagina, totalReportes);

        request.setAttribute("desde", desde);
        request.setAttribute("hasta", hasta);

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
                redirigirReportes(request, response);
                return;
            }

            boolean procesado = false;

            if ("rechazar".equals(accion)) {

                String resolucion = (motivo == null || motivo.trim().isEmpty())
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

                if (motivo == null || motivo.trim().isEmpty()) {
                    session.setAttribute("mensajeReportes", "Debés indicar el motivo de la baja.");
                    redirigirReportes(request, response);
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
                redirigirReportes(request, response);
                return;
            }
            session.setAttribute("mensajeReportes", procesado
                            ? "El reporte fue procesado correctamente."
                            : "No se pudo procesar el reporte.");

        } catch (NumberFormatException e) {
            session.setAttribute("mensajeReportes", "El identificador del reporte no es válido.");
        }
        redirigirReportes(request, response);
    }
    private void redirigirReportes(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String pagina = request.getParameter("pagina");
        if (pagina == null || !pagina.matches("\\d+") || Integer.parseInt(pagina) < 1) {
            pagina = "1";
        }
        response.sendRedirect(request.getContextPath() + "/reportes?pagina=" + pagina);
    }
}
