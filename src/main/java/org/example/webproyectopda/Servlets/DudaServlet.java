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
import org.example.webproyectopda.ENUMS.EstadoDuda;
import org.example.webproyectopda.ENUMS.TipoNotificacion;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Comentario;
import org.example.webproyectopda.Modelos.Duda;
import org.example.webproyectopda.Modelos.Notificacion;
import org.example.webproyectopda.Modelos.Usuario;
import org.example.webproyectopda.Utilidades.ConfiguracionCloudinary;
import org.example.webproyectopda.Utilidades.ImagenCloudinary;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/duda")
public class DudaServlet extends HttpServlet {

    private static final int MAX_RESPUESTA = 2000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        moverMensaje(sesion, request, "dudaMensaje");
        moverMensaje(sesion, request, "dudaError");

        int dudaId;
        try {
            dudaId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            mostrarError(request, response, "La duda indicada no es válida.");
            return;
        }
        try {
            Duda duda = DudaDAO.buscarPorId(dudaId);
            if (duda == null || duda.isDadaDeBaja()) {
                mostrarError(request, response, "La duda no existe o fue dada de baja.");
                return;
            }
            if (!puedeVerCurso(usuario, duda.getCursoId())) {
                mostrarError(request, response, "No estás inscripto en el curso de esta duda.");
                return;
            }

            mostrarDuda(request, response, duda, usuario);

        } catch (SQLException | RuntimeException e) {
            log("Error al mostrar la duda " + dudaId, e);
            mostrarError(request, response, "No se pudo cargar la duda. Intentá de nuevo más tarde.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        int dudaId;
        try {
            dudaId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            mostrarError(request, response, "La duda indicada no es válida.");
            return;
        }

        if ("marcarUtil".equals(request.getParameter("accion"))) {
            marcarComoMasUtil(request, response, sesion, usuario, dudaId);
            return;
        }

        String texto = texto(request.getParameter("texto"));
        String imagenUrl = texto(request.getParameter("imagenUrl")).strip();
        String publicId = texto(request.getParameter("imagenPublicId")).strip();
        String comentarParam = request.getParameter("comentar");
        boolean esComentario = comentarParam != null && !comentarParam.isBlank();
        request.setAttribute("texto", texto);

        Duda duda = null;
        try {
            duda = DudaDAO.buscarPorId(dudaId);
            if (duda == null || duda.isDadaDeBaja()) {
                mostrarError(request, response, "La duda no existe o fue dada de baja.");
                return;
            }
            if (!puedeVerCurso(usuario, duda.getCursoId())) {
                mostrarError(request, response, "No estás inscripto en el curso de esta duda.");
                return;
            }

            Comentario respuestaComentada = null;
            if (esComentario) {
                List<Comentario> respuestas = armarRespuestas(ComentarioDAO.listarDeDuda(duda.getId()));
                respuestaComentada = buscarRespuesta(respuestas, comentarParam);
                if (respuestaComentada == null) {
                    throw new IllegalArgumentException("La respuesta que querés comentar no existe.");
                }
            } else if (duda.getEstado() != EstadoDuda.Abierta) {
                throw new IllegalArgumentException("La duda ya está resuelta: no admite nuevas respuestas.");
            }

            String queEs = esComentario ? "El comentario" : "La respuesta";
            if (texto.isBlank()) {
                throw new IllegalArgumentException(queEs + " no puede estar vacío/a.");
            }
            if (texto.length() > MAX_RESPUESTA) {
                throw new IllegalArgumentException(queEs + " no puede superar los " + MAX_RESPUESTA + " caracteres.");
            }
            ImagenCloudinary.validar(imagenUrl, publicId);
            request.setAttribute("imagenUrl", imagenUrl);
            request.setAttribute("imagenPublicId", publicId);

            Integer padreId = esComentario ? respuestaComentada.getId() : null;
            Comentario nuevo = new Comentario(texto.strip(),
                    imagenUrl.isEmpty() ? null : imagenUrl,
                    LocalDate.now(), usuario.getId(), duda.getId(), padreId);
            nuevo.setImagenPublicId(publicId.isEmpty() ? null : publicId);

            Notificacion notificacion = null;
            if (!esComentario && duda.getAutorNombre() != null && duda.getUsuarioId() != usuario.getId()) {
                notificacion = new Notificacion(LocalDate.now(), TipoNotificacion.Respuesta,
                        usuario.getNombre() + " respondió tu duda: \"" + resumir(duda.getMensaje()) + "\"",
                        duda.getUsuarioId(), duda.getId());
            }

            ComentarioDAO.crearRespuesta(nuevo, notificacion);

            if (esComentario) {
                sesion.setAttribute("dudaMensaje", "Comentario publicado correctamente.");
                response.sendRedirect(request.getContextPath() + "/duda?id=" + duda.getId()
                        + "#respuesta-" + respuestaComentada.getId());
            } else {
                sesion.setAttribute("dudaMensaje", "Respuesta publicada correctamente.");
                response.sendRedirect(request.getContextPath() + "/duda?id=" + duda.getId());
            }

        } catch (IllegalArgumentException e) {
            volverAlFormulario(request, response, duda, usuario, e.getMessage());
        } catch (SQLException | RuntimeException e) {
            log("No se pudo guardar en la duda " + dudaId, e);
            if (duda == null) {
                mostrarError(request, response, "No se pudo cargar la duda. Intentá de nuevo más tarde.");
            } else {
                volverAlFormulario(request, response, duda, usuario, "No se pudo guardar. Intentá nuevamente.");
            }
        }
    }
    private void marcarComoMasUtil(HttpServletRequest request, HttpServletResponse response,
                                   HttpSession sesion, Usuario usuario, int dudaId)
            throws ServletException, IOException {

        String error = null;
        try {
            Duda duda = DudaDAO.buscarPorId(dudaId);
            if (duda == null || duda.isDadaDeBaja()) {
                mostrarError(request, response, "La duda no existe o fue dada de baja.");
                return;
            }

            boolean esAutor = duda.getUsuarioId() == usuario.getId();
            boolean esAdmin = usuario.getRol() == TipoRol.Admin;

            if (!esAutor && !esAdmin) {
                error = "Solo el autor de la duda o un administrador puede elegir la respuesta más útil.";
            } else if (duda.getEstado() != EstadoDuda.Abierta) {
                error = "La duda ya está resuelta.";
            } else {
                List<Comentario> respuestas = armarRespuestas(ComentarioDAO.listarDeDuda(dudaId));
                Comentario respuesta = buscarRespuesta(respuestas, texto(request.getParameter("respuesta")));

                if (respuesta == null) {
                    error = "La respuesta elegida no existe o no pertenece a esta duda.";
                } else {
                    Notificacion notificacion = null;
                    if (respuesta.getUsuarioId() != usuario.getId()) {
                        notificacion = new Notificacion(LocalDate.now(), TipoNotificacion.Respuesta,
                                usuario.getNombre() + " marcó tu respuesta como la más útil: \""
                                        + resumir(respuesta.getMensaje()) + "\"",
                                respuesta.getUsuarioId(), dudaId);
                    }

                    if (!DudaDAO.marcarResueltaYDestacarRespuesta(dudaId, respuesta.getId(), notificacion)) {
                        error = "No se pudo marcar: la duda ya fue resuelta o la respuesta no es válida.";
                    }
                }
            }
        } catch (SQLException | RuntimeException e) {
            log("Error al marcar como resuelta la duda " + dudaId, e);
            error = "No se pudo marcar la duda como resuelta. Intentá nuevamente.";
        }

        if (error != null) {
            sesion.setAttribute("dudaError", error);
        } else {
            sesion.setAttribute("dudaMensaje", "La duda quedó marcada como resuelta.");
        }
        response.sendRedirect(request.getContextPath() + "/duda?id=" + dudaId);
    }
    private void mostrarDuda(HttpServletRequest request, HttpServletResponse response, Duda duda, Usuario usuario)
            throws SQLException, ServletException, IOException {

        List<Comentario> respuestas = armarRespuestas(ComentarioDAO.listarDeDuda(duda.getId()));

        Comentario respuestaAComentar = null;
        String comentarParam = request.getParameter("comentar");
        if (comentarParam != null && !comentarParam.isBlank()) {
            respuestaAComentar = buscarRespuesta(respuestas, comentarParam);
            if (respuestaAComentar == null && request.getAttribute("errorRespuesta") == null) {
                request.setAttribute("errorRespuesta", "La respuesta que querés comentar no existe.");
            }
        }
        request.setAttribute("respuestaAComentar", respuestaAComentar);

        request.setAttribute("duda", duda);
        request.setAttribute("respuestas", respuestas);
        request.setAttribute("esAutorDeLaDuda", duda.getUsuarioId() == usuario.getId());
        request.setAttribute("puedeResponder", duda.getEstado() == EstadoDuda.Abierta);
        boolean esAutorOAdmin = duda.getUsuarioId() == usuario.getId() || usuario.getRol() == TipoRol.Admin;
        request.setAttribute("puedeMarcarUtil", esAutorOAdmin && duda.getEstado() == EstadoDuda.Abierta);
        prepararFormulario(request);
        request.getRequestDispatcher("/duda.jsp").forward(request, response);
    }

    private List<Comentario> armarRespuestas(List<Comentario> todos) {
        Map<Integer, Comentario> respuestasPorId = new LinkedHashMap<>();

        for (Comentario c : todos) {
            if (c.getComentarioPadreId() == null) {
                respuestasPorId.put(c.getId(), c);
            }
        }
        for (Comentario c : todos) {
            if (c.getComentarioPadreId() != null) {
                Comentario respuesta = respuestasPorId.get(c.getComentarioPadreId());
                if (respuesta != null) {
                    respuesta.getComentarios().add(c);
                }
            }
        }
        return new ArrayList<>(respuestasPorId.values());
    }

    private Comentario buscarRespuesta(List<Comentario> respuestas, String idTexto) {
        try {
            int id = Integer.parseInt(idTexto.strip());
            for (Comentario respuesta : respuestas) {
                if (respuesta.getId() == id) {
                    return respuesta;
                }
            }
        } catch (NumberFormatException e) {
        }
        return null;
    }

    private boolean puedeVerCurso(Usuario usuario, int cursoId) {
        return usuario.getRol() == TipoRol.Admin
                || InscripcionDAO.existeInscripcion(usuario.getId(), cursoId);
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher("/duda.jsp").forward(request, response);
    }

    private void moverMensaje(HttpSession sesion, HttpServletRequest request, String nombre) {
        Object valor = sesion.getAttribute(nombre);
        if (valor != null) {
            request.setAttribute(nombre, valor);
            sesion.removeAttribute(nombre);
        }
    }

    private void prepararFormulario(HttpServletRequest request) {
        request.setAttribute("cloudName", ConfiguracionCloudinary.getCloudName());
        request.setAttribute("uploadPreset", ConfiguracionCloudinary.getUploadPreset());
        request.setAttribute("cloudinaryConfigurado", ConfiguracionCloudinary.isConfigurada());
    }

    private void volverAlFormulario(HttpServletRequest request, HttpServletResponse response,
                                    Duda duda, Usuario usuario, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("errorRespuesta", mensaje);
        try {
            mostrarDuda(request, response, duda, usuario);
        } catch (SQLException | RuntimeException e) {
            log("Error al volver a mostrar la duda " + duda.getId(), e);
            mostrarError(request, response, "No se pudo cargar la duda. Intentá de nuevo más tarde.");
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private String resumir(String mensaje) {
        return mensaje.length() <= 60 ? mensaje : mensaje.substring(0, 60) + "…";
    }
}