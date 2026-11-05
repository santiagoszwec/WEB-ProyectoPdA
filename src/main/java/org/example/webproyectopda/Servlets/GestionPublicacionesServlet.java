package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Usuario;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.IOException;

@WebServlet("/gestionPublicaciones")
public class GestionPublicacionesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession sesion = request.getSession(false);
        Usuario usuario = sesion == null
                ? null
                : (Usuario) sesion.getAttribute("usuarioLogueado");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != TipoRol.Admin) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Esta sección es solo para administradores");
            return;
        }

        Object exito = sesion.getAttribute("mensajeExito");
        if (exito != null) {
            request.setAttribute("mensajeExito", exito);
            sesion.removeAttribute("mensajeExito");
        }

        // Parámetros de búsqueda y filtros.
        String busqueda = request.getParameter("busqueda");
        String estado = request.getParameter("estado");
        String tipo = request.getParameter("tipo");

        if (busqueda == null) busqueda = "";
        if (!"activas".equals(estado) && !"inactivas".equals(estado)) {
            estado = "";
        }
        if (!"Duda".equals(tipo) && !"Mensaje".equals(tipo)
                && !"Material".equals(tipo)) {
            tipo = "";
        }

        // Paginación.
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

        if (pagina < 1) pagina = 1;

        int totalPublicaciones = PublicacionDAO.contarTodas(busqueda, estado, tipo);

        int totalPaginas = (int) Math.ceil((double) totalPublicaciones / porPagina);
        if (totalPaginas == 0) totalPaginas = 1;

        if (pagina > totalPaginas) {
            pagina = totalPaginas;
        }

        int offset = (pagina - 1) * porPagina;

        request.setAttribute("publicaciones", PublicacionDAO.listarTodas(porPagina, offset, busqueda, estado, tipo));

        request.setAttribute("paginaActual", pagina);
        request.setAttribute("totalPaginas", totalPaginas);
        request.setAttribute("totalPublicaciones", totalPublicaciones);

        int desde = totalPublicaciones == 0 ? 0 : offset + 1;
        int hasta = Math.min(offset + porPagina, totalPublicaciones);

        request.setAttribute("desde", desde);
        request.setAttribute("hasta", hasta);

        // Mantener los filtros seleccionados en el formulario.
        request.setAttribute("busqueda", busqueda);
        request.setAttribute("estadoSeleccionado", estado);
        request.setAttribute("tipoSeleccionado", tipo);

        request.getRequestDispatcher("/gestionPublicaciones.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        Usuario usuario = sesion == null
                ? null
                : (Usuario) sesion.getAttribute("usuarioLogueado");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (usuario.getRol() != TipoRol.Admin) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Esta sección es solo para administradores");
            return;
        }

        try {
            int publicacionId = Integer.parseInt(request.getParameter("publicacionId"));

            PublicacionDAO.darDeBaja(publicacionId, "Publicación eliminada por un administrador");

            sesion.setAttribute("mensajeExito", "La publicación fue eliminada correctamente.");

        } catch (NumberFormatException e) {
            sesion.setAttribute("mensajeExito", "No se pudo identificar la publicación.");

        } catch (RuntimeException e) {
            sesion.setAttribute("mensajeExito", "Ocurrió un error al eliminar la publicación.");
        }
        String busqueda = request.getParameter("busqueda");
        String estado = request.getParameter("estado");
        String tipo = request.getParameter("tipo");
        String pagina = request.getParameter("pagina");

        String url = request.getContextPath() + "/gestionPublicaciones"
                + "?busqueda=" + URLEncoder.encode(
                busqueda == null ? "" : busqueda, "UTF-8")
                + "&estado=" + URLEncoder.encode(
                estado == null ? "" : estado, "UTF-8")
                + "&tipo=" + URLEncoder.encode(
                tipo == null ? "" : tipo, "UTF-8")
                + "&pagina=" + URLEncoder.encode(
                pagina == null ? "1" : pagina, "UTF-8");

        response.sendRedirect(url);
    }
}