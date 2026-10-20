package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.CursoDAO;
import org.example.webproyectopda.DAOS.InscripcionDAO;
import org.example.webproyectopda.DAOS.RegistroDeHorasDAO;
import org.example.webproyectopda.ENUMS.TipoEstado;
import org.example.webproyectopda.Modelos.Curso;
import org.example.webproyectopda.Modelos.Inscripcion;
import org.example.webproyectopda.Modelos.RegistroDeHoras;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/misHoras")
public class HorasServlet extends HttpServlet {

    private static final float MAX_HORAS = 24f;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        mostrarPagina(request, response, usuario);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Usuario usuario = obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String materiaParam = request.getParameter("materia");
        String fechaParam = request.getParameter("fecha");
        String horasParam = request.getParameter("horas");

        Map<String, String> valores = new LinkedHashMap<>();
        valores.put("fecha", fechaParam == null ? "" : fechaParam);
        valores.put("horas", horasParam == null ? "" : horasParam);

        List<String> errores = new ArrayList<>();

        Integer materiaId = null;
        try {
            materiaId = Integer.parseInt(materiaParam);
        } catch (NumberFormatException e) {
            errores.add("Seleccioná una materia.");
        }

        LocalDate fecha = null;
        if (fechaParam == null || fechaParam.isBlank()) {
            errores.add("Ingresá la fecha del registro.");
        } else {
            try {
                fecha = LocalDate.parse(fechaParam);
                if (fecha.isAfter(LocalDate.now())) {
                    errores.add("La fecha no puede ser futura.");
                }
            } catch (DateTimeParseException e) {
                errores.add("La fecha ingresada no es válida.");
                fecha = null;
            }
        }

        Float horas = null;
        if (horasParam == null || horasParam.isBlank()) {
            errores.add("Ingresá las horas estudiadas.");
        } else {
            try {
                horas = Float.parseFloat(horasParam.trim().replace(',', '.'));
                if (horas <= 0) {
                    errores.add("Las horas estudiadas deben ser mayores a 0.");
                    horas = null;
                } else if (horas > MAX_HORAS) {
                    errores.add("No se pueden registrar más de " + (int) MAX_HORAS + " horas en un solo registro.");
                    horas = null;
                }
            } catch (NumberFormatException e) {
                errores.add("Las horas ingresadas no son válidas.");
            }
        }

        if (errores.isEmpty()) {
            Inscripcion inscripcion = InscripcionDAO.buscarPorId(materiaId);
            if (inscripcion == null || inscripcion.getUsuarioId() != usuario.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "No estás inscripto en esa materia");
                return;
            }
            if (inscripcion.getEstado() != TipoEstado.Cursando) {
                errores.add("Solo podés registrar horas en materias que estés cursando.");
            }
        }

        if (!errores.isEmpty()) {
            request.setAttribute("errores", errores);
            request.setAttribute("valores", valores);
            if (materiaId != null) {
                request.setAttribute("materiaSeleccionada", materiaId);
            }
            mostrarPagina(request, response, usuario);
            return;
        }

        boolean guardada = RegistroDeHorasDAO.crear(new RegistroDeHoras(horas, fecha, materiaId));
        if (!guardada) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo guardar el registro de horas");
            return;
        }

        request.getSession(false).setAttribute("mensajeExito",
                "Registro de horas guardado correctamente.");
        response.sendRedirect(request.getContextPath() + "/misHoras");
    }

    private void mostrarPagina(HttpServletRequest request, HttpServletResponse response,
                               Usuario usuario) throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        Object exito = sesion.getAttribute("mensajeExito");
        if (exito != null) {
            request.setAttribute("mensajeExito", exito);
            sesion.removeAttribute("mensajeExito");
        }

        // Materias habilitadas: inscripciones en estado Cursando con curso activo.
        Map<Integer, String> nombresPorCurso = new HashMap<>();
        for (Curso curso : CursoDAO.listarPorUsuario(usuario.getId())) {
            if ("Cursando".equals(curso.getEstadoInscripcion())) {
                nombresPorCurso.put(curso.getId(), curso.getNombre());
            }
        }

        Map<Integer, String> materias = new LinkedHashMap<>();
        for (Inscripcion inscripcion : InscripcionDAO.listarPorUsuario(usuario.getId())) {
            if (inscripcion.getEstado() == TipoEstado.Cursando) {
                String nombreCurso = nombresPorCurso.get(inscripcion.getCursoId());
                if (nombreCurso != null) {
                    materias.put(inscripcion.getId(), nombreCurso);
                }
            }
        }

        request.setAttribute("materias", materias);
        request.setAttribute("hoy", LocalDate.now().toString());

        if (materias.isEmpty()) {
            request.getRequestDispatcher("/misHoras.jsp").forward(request, response);
            return;
        }

        List<RegistroDeHorasDAO.RegistroConMateria> registros =
                RegistroDeHorasDAO.listarPorUsuario(usuario.getId());
        List<RegistroDeHorasDAO.TotalMateria> totales =
                RegistroDeHorasDAO.totalesPorMateria(usuario.getId());

        double totalGeneral = 0;
        for (RegistroDeHorasDAO.TotalMateria total : totales) {
            totalGeneral += total.getTotalHoras();
        }

        request.setAttribute("registros", registros);
        request.setAttribute("totales", totales);
        request.setAttribute("totalGeneral", totalGeneral);
        request.setAttribute("totalesChart", jsonTotales(totales));
        request.setAttribute("serieChart", jsonSerie(
                RegistroDeHorasDAO.seriePorFecha(usuario.getId())));

        request.getRequestDispatcher("/misHoras.jsp").forward(request, response);
    }

    private String jsonTotales(List<RegistroDeHorasDAO.TotalMateria> totales) {
        StringBuilder etiquetas = new StringBuilder();
        StringBuilder datos = new StringBuilder();

        for (RegistroDeHorasDAO.TotalMateria total : totales) {
            if (etiquetas.length() > 0) {
                etiquetas.append(',');
                datos.append(',');
            }
            etiquetas.append('"').append(escapar(total.getCurso())).append('"');
            datos.append(total.getTotalHoras());
        }
        return "{\"labels\":[" + etiquetas + "],\"data\":[" + datos + "]}";
    }

    private String jsonSerie(List<RegistroDeHorasDAO.PuntoSerie> puntos) {
        StringBuilder etiquetas = new StringBuilder();
        StringBuilder datos = new StringBuilder();

        for (RegistroDeHorasDAO.PuntoSerie punto : puntos) {
            if (etiquetas.length() > 0) {
                etiquetas.append(',');
                datos.append(',');
            }
            etiquetas.append('"').append(punto.getFecha()).append('"');
            datos.append(punto.getHoras());
        }
        return "{\"labels\":[" + etiquetas + "],\"data\":[" + datos + "]}";
    }

    // Evita romper el JSON/HTML con nombres de curso o datos no confiables.
    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("<", "\\u003c")
                .replace(">", "\\u003e")
                .replace("&", "\\u0026");
    }

    private Usuario obtenerUsuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        return (Usuario) sesion.getAttribute("usuarioLogueado");
    }
}
