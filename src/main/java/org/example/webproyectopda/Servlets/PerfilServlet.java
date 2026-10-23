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
import java.util.ArrayList;
import java.util.List;

// Perfil de un estudiante: el propio se puede consultar y editar; el de un
// compañero solo se puede consultar si ambos comparten al menos un curso.
@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    private static final int MIN_ANIO = 2008;
    private static final int MAX_ANIO = 2027;
    private static final int MIN_PASSWORD = 6;
    private static final int MAX_PASSWORD = 255;
    private static final int MAX_NOMBRE = 100;
    private static final int MAX_CORREO = 100;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Sin id se asume el perfil propio (por ejemplo, al entrar desde "Mi perfil").
        String idParam = request.getParameter("id");
        int estudianteId;
        try {
            estudianteId = (idParam == null || idParam.isBlank())
                    ? usuario.getId()
                    : Integer.parseInt(idParam);
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

        // El perfil propio siempre se puede ver; el ajeno exige un curso en común.
        if (estudiante.getId() != usuario.getId()
                && !InscripcionDAO.existeCursoEnComun(usuario.getId(), estudiante.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No compartís ninguna materia con este estudiante");
            return;
        }

        HttpSession sesion = request.getSession(false);
        Object exito = sesion.getAttribute("mensajeExito");
        if (exito != null) {
            request.setAttribute("mensajeExito", exito);
            sesion.removeAttribute("mensajeExito");
        }

        mostrarPerfil(request, response, usuario, estudiante);
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

        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String anio = request.getParameter("anioDeGeneracion");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");

        List<String> errores = validar(nombre, correo, anio, password, password2);

        if (errores.isEmpty()) {
            try {
                Usuario existente = UsuarioDAO.buscarPorCorreo(correo.trim());
                if (existente != null && existente.getId() != usuario.getId()) {
                    errores.add("Ya existe un usuario con ese correo.");
                }
            } catch (RuntimeException e) {
                log("Error al verificar el correo del usuario " + usuario.getId(), e);
                errores.add("No se pudieron guardar los cambios. Intentá nuevamente.");
            }
        }

        if (!errores.isEmpty()) {
            volverConErrores(request, response, usuario, errores, nombre, correo, anio);
            return;
        }

        // Si no se ingresó contraseña nueva, se conserva la actual.
        String contrasenia = (password == null || password.isEmpty())
                ? null
                : password;

        Usuario actualizado = new Usuario(usuario.getId(), nombre.trim(), correo.trim(),
                Integer.parseInt(anio.trim()), usuario.getRol(), contrasenia, usuario.isActivo());

        try {
            if (!UsuarioDAO.actualizarPerfil(actualizado)) {
                throw new IllegalStateException("No se actualizó ninguna fila del usuario " + usuario.getId());
            }
        } catch (RuntimeException e) {
            log("Error al actualizar el perfil del usuario " + usuario.getId(), e);
            errores.add("No se pudieron guardar los cambios. Intentá nuevamente.");
            volverConErrores(request, response, usuario, errores, nombre, correo, anio);
            return;
        }

        HttpSession sesion = request.getSession(false);
        sesion.setAttribute("usuarioLogueado", actualizado);
        sesion.setAttribute("mensajeExito", "Tus datos se actualizaron correctamente.");

        response.sendRedirect(request.getContextPath() + "/perfil?id=" + actualizado.getId());
    }

    private void volverConErrores(HttpServletRequest request, HttpServletResponse response,
                                  Usuario usuario, List<String> errores,
                                  String nombre, String correo, String anio)
            throws ServletException, IOException {

        request.setAttribute("errores", errores);
        request.setAttribute("valorNombre", nombre);
        request.setAttribute("valorCorreo", correo);
        request.setAttribute("valorAnio", anio);
        request.setAttribute("abrirFormulario", true);
        mostrarPerfil(request, response, usuario, usuario);
    }

    private List<String> validar(String nombre, String correo, String anio,
                                 String password, String password2) {

        List<String> errores = new ArrayList<>();

        if (nombre == null || nombre.isBlank()) {
            errores.add("El nombre no puede estar vacío.");
        } else if (nombre.trim().length() > MAX_NOMBRE) {
            errores.add("El nombre no puede superar los " + MAX_NOMBRE + " caracteres.");
        }

        if (correo == null || correo.isBlank()) {
            errores.add("El correo no puede estar vacío.");
        } else if (correo.trim().length() > MAX_CORREO) {
            errores.add("El correo no puede superar los " + MAX_CORREO + " caracteres.");
        } else if (!correoValido(correo.trim())) {
            errores.add("Ingresá un correo válido.");
        }

        if (anio == null || anio.isBlank()) {
            errores.add("El año de generación es obligatorio.");
        } else {
            try {
                int valor = Integer.parseInt(anio.trim());
                if (valor < MIN_ANIO || valor > MAX_ANIO) {
                    errores.add("El año de generación debe estar entre " + MIN_ANIO
                            + " y " + MAX_ANIO + ".");
                }
            } catch (NumberFormatException e) {
                errores.add("El año de generación debe ser un número.");
            }
        }

        String nueva = password == null ? "" : password;
        String repetida = password2 == null ? "" : password2;

        if (!nueva.isEmpty() && nueva.length() < MIN_PASSWORD) {
            errores.add("La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres.");
        } else if (nueva.length() > MAX_PASSWORD) {
            errores.add("La contraseña no puede superar los " + MAX_PASSWORD + " caracteres.");
        }

        if (nueva.isEmpty() && !repetida.isEmpty()) {
            errores.add("Completá la contraseña nueva para confirmarla.");
        } else if (!nueva.equals(repetida)) {
            errores.add("Las contraseñas no coinciden.");
        }

        return errores;
    }

    private boolean correoValido(String correo) {
        return correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private void mostrarPerfil(HttpServletRequest request, HttpServletResponse response,
                               Usuario usuario, Usuario estudiante)
            throws ServletException, IOException {

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

    private Usuario obtenerUsuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        return (Usuario) sesion.getAttribute("usuarioLogueado");
    }
}
