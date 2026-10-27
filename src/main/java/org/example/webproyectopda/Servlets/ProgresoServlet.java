package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.CursoDAO;
import org.example.webproyectopda.DAOS.InscripcionDAO;
import org.example.webproyectopda.DAOS.TareaDAO;
import org.example.webproyectopda.ENUMS.TipoEstado;
import org.example.webproyectopda.ENUMS.TipoNotificacion;
import org.example.webproyectopda.Modelos.Curso;
import org.example.webproyectopda.Modelos.Inscripcion;
import org.example.webproyectopda.Modelos.Notificacion;
import org.example.webproyectopda.Modelos.Progreso;
import org.example.webproyectopda.Modelos.Tarea;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/progreso")
public class ProgresoServlet extends HttpServlet {

    private static final int MAX_TITULO = 150;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");

        moverMensaje(sesion, request, "progresoMensaje");
        moverMensaje(sesion, request, "progresoError");

        int cursoId;
        try {
            cursoId = Integer.parseInt(request.getParameter("curso"));
        } catch (NumberFormatException e) {
            mostrarError(request, response, "El curso indicado no es válido.");
            return;
        }

        try {
            Curso curso = CursoDAO.buscarPorId(cursoId);
            Inscripcion inscripcion = InscripcionDAO.buscarPorUsuarioYCurso(usuario.getId(), cursoId);
            String problema = validarAcceso(curso, inscripcion);
            if (problema != null) {
                mostrarError(request, response, problema);
                return;
            }

            mostrarProgreso(request, response, curso, inscripcion);

        } catch (SQLException | RuntimeException e) {
            log("Error al mostrar el progreso del curso " + cursoId, e);
            mostrarError(request, response, "No se pudo cargar tu progreso. Intentá de nuevo más tarde.");
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

        int cursoId;
        try {
            cursoId = Integer.parseInt(request.getParameter("curso"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/misCursos");
            return;
        }
        String volver = request.getContextPath() + "/progreso?curso=" + cursoId;

        try {
            Curso curso = CursoDAO.buscarPorId(cursoId);
            Inscripcion inscripcion = InscripcionDAO.buscarPorUsuarioYCurso(usuario.getId(), cursoId);
            String problema = validarAcceso(curso, inscripcion);
            if (problema != null) {
                mostrarError(request, response, problema);
                return;
            }

            String accion = request.getParameter("accion");
            if ("agregar".equals(accion)) {
                agregarTarea(request, response, sesion, curso, inscripcion, volver);
            } else if ("completar".equals(accion)) {
                completarTarea(request, response, sesion, usuario, curso, inscripcion, volver);
            } else {
                response.sendRedirect(volver);
            }

        } catch (SQLException | RuntimeException e) {
            log("Error al actualizar el progreso del curso " + cursoId, e);
            sesion.setAttribute("progresoError", "No se pudo guardar el cambio. Intentá nuevamente.");
            response.sendRedirect(volver);
        }
    }

    private void agregarTarea(HttpServletRequest request, HttpServletResponse response, HttpSession sesion,
                              Curso curso, Inscripcion inscripcion, String volver)
            throws ServletException, IOException, SQLException {

        String titulo = texto(request.getParameter("titulo")).strip();
        String fechaTexto = texto(request.getParameter("fechaLimite")).strip();
        request.setAttribute("valorTitulo", titulo);
        request.setAttribute("valorFecha", fechaTexto);

        if (titulo.isEmpty()) {
            request.setAttribute("errorTitulo", "El título de la tarea es obligatorio.");
        } else if (titulo.length() > MAX_TITULO) {
            request.setAttribute("errorTitulo", "El título no puede superar los " + MAX_TITULO + " caracteres.");
        }

        LocalDate fechaLimite = null;
        if (!fechaTexto.isEmpty()) {
            try {
                fechaLimite = LocalDate.parse(fechaTexto);
                if (fechaLimite.isBefore(LocalDate.now())) {
                    request.setAttribute("errorFecha", "La fecha límite no puede ser anterior a hoy.");
                }
            } catch (DateTimeParseException e) {
                request.setAttribute("errorFecha", "La fecha límite no es válida.");
            }
        }

        if (request.getAttribute("errorTitulo") != null || request.getAttribute("errorFecha") != null) {
            mostrarProgreso(request, response, curso, inscripcion);
            return;
        }

        TareaDAO.crear(new Tarea(titulo, fechaLimite, inscripcion.getId()));
        sesion.setAttribute("progresoMensaje", "Tarea agregada correctamente.");
        response.sendRedirect(volver);
    }

    private void completarTarea(HttpServletRequest request, HttpServletResponse response, HttpSession sesion,
                                Usuario usuario, Curso curso, Inscripcion inscripcion, String volver)
            throws IOException, SQLException {

        int tareaId;
        try {
            tareaId = Integer.parseInt(request.getParameter("tarea"));
        } catch (NumberFormatException e) {
            sesion.setAttribute("progresoError", "La tarea indicada no es válida.");
            response.sendRedirect(volver);
            return;
        }

        List<Tarea> tareas = TareaDAO.listarPorInscripcion(inscripcion.getId());
        Tarea tarea = null;
        for (Tarea t : tareas) {
            if (t.getId() == tareaId) {
                tarea = t;
            }
        }
        if (tarea == null || tarea.isCompletada()) {
            sesion.setAttribute("progresoError", "La tarea no existe o ya estaba completada.");
            response.sendRedirect(volver);
            return;
        }

        tarea.setCompletada(true);
        Progreso nuevoProgreso = new Progreso(tareas);

        Notificacion notificacion = new Notificacion(LocalDate.now(), TipoNotificacion.Progreso,
                "Completaste \"" + tarea.getTitulo() + "\" en " + curso.getNombre()
                        + ". Tu progreso es del " + nuevoProgreso.getPorcentaje() + "%.",
                usuario.getId(), null);

        if (!TareaDAO.marcarCompletada(tareaId, inscripcion.getId(), notificacion)) {
            sesion.setAttribute("progresoError", "La tarea no existe o ya estaba completada.");
            response.sendRedirect(volver);
            return;
        }

        sesion.setAttribute("progresoMensaje", "Tarea completada. Tu progreso en "
                + curso.getNombre() + " es del " + nuevoProgreso.getPorcentaje() + "%.");
        response.sendRedirect(volver);
    }

    private String validarAcceso(Curso curso, Inscripcion inscripcion) {
        if (curso == null || !curso.isActivo()) {
            return "El curso no existe o no está activo.";
        }
        if (inscripcion == null) {
            return "No estás inscripto en este curso.";
        }
        if (inscripcion.getEstado() != TipoEstado.Cursando) {
            return "Solo podés actualizar el progreso de las materias que estás cursando.";
        }
        return null;
    }

    private void mostrarProgreso(HttpServletRequest request, HttpServletResponse response,
                                 Curso curso, Inscripcion inscripcion)
            throws ServletException, IOException, SQLException {

        List<Tarea> tareas = TareaDAO.listarPorInscripcion(inscripcion.getId());

        request.setAttribute("curso", curso);
        request.setAttribute("tareas", tareas);
        request.setAttribute("progreso", new Progreso(tareas));
        request.setAttribute("hoy", LocalDate.now());
        request.getRequestDispatcher("/progreso.jsp").forward(request, response);
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher("/progreso.jsp").forward(request, response);
    }

    private void moverMensaje(HttpSession sesion, HttpServletRequest request, String nombre) {
        Object valor = sesion.getAttribute(nombre);
        if (valor != null) {
            request.setAttribute(nombre, valor);
            sesion.removeAttribute(nombre);
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }
}