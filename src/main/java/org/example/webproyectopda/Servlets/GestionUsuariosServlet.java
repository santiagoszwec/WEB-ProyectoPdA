package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.SuspensionDAO;
import org.example.webproyectopda.DAOS.UsuarioDAO;
import org.example.webproyectopda.Modelos.Suspension;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Gestión de usuarios: solo accesible por Administrador.
// GET  -> lista todos los usuarios con su estado de suspensión.
// POST -> accion=suspender  (SuspensionDAO.suspender)
//        accion=reactivar   (SuspensionDAO.levantar)
@WebServlet("/gestionUsuarios")
public class GestionUsuariosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario admin = obtenerUsuarioLogueado(request);
        if (admin == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!admin.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo el administrador puede acceder a la gestión de usuarios.");
            return;
        }

        List<Usuario> usuarios = UsuarioDAO.listarTodos();

        // Mapa de idUsuario -> Suspension activa, para saber quién está suspendido y mostrar datos.
        Map<Integer, Suspension> suspensiones = new HashMap<>();
        for (Usuario u : usuarios) {
            Suspension s = SuspensionDAO.obtenerSuspensionActiva(u.getId());
            if (s != null) {
                suspensiones.put(u.getId(), s);
            }
        }

        request.setAttribute("usuarios", usuarios);
        request.setAttribute("suspensiones", suspensiones);

        // Mensajes de éxito / error persistidos en sesión desde el POST.
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            Object exito = sesion.getAttribute("mensajeExito");
            if (exito != null) {
                request.setAttribute("mensajeExito", exito);
                sesion.removeAttribute("mensajeExito");
            }
            Object error = sesion.getAttribute("mensajeError");
            if (error != null) {
                request.setAttribute("mensajeError", error);
                sesion.removeAttribute("mensajeError");
            }
        }

        request.getRequestDispatcher("/gestionUsuarios.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Usuario admin = obtenerUsuarioLogueado(request);
        if (admin == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!admin.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo el administrador puede gestionar cuentas de usuario.");
            return;
        }

        String accion = request.getParameter("accion");
        String idParam = request.getParameter("usuarioId");

        if (idParam == null || idParam.trim().isEmpty()) {
            sesionMensaje(request, "mensajeError", "No se especificó el usuario.");
            response.sendRedirect(request.getContextPath() + "/gestionUsuarios");
            return;
        }

        int usuarioId;
        try {
            usuarioId = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            sesionMensaje(request, "mensajeError", "ID de usuario no válido.");
            response.sendRedirect(request.getContextPath() + "/gestionUsuarios");
            return;
        }

        if ("suspender".equals(accion)) {
            String motivo = request.getParameter("motivo");
            String diasParam = request.getParameter("dias");

            if (motivo == null || motivo.trim().isEmpty()) {
                sesionMensaje(request, "mensajeError", "El motivo es obligatorio.");
                response.sendRedirect(request.getContextPath() + "/gestionUsuarios");
                return;
            }

            int dias;
            try {
                dias = Integer.parseInt(diasParam == null ? "" : diasParam.trim());
                if (dias <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sesionMensaje(request, "mensajeError", "La duración debe ser un número mayor a 0.");
                response.sendRedirect(request.getContextPath() + "/gestionUsuarios");
                return;
            }

            // SuspensionDAO.suspender() ya hace en una transacción:
            //   UPDATE usuario SET activo = FALSE
            //   INSERT INTO suspension (usuario_id, motivo, fecha_suspension, fecha_fin, activa)
            //   INSERT notificación al usuario (TipoNotificacion.Suspension)
            boolean ok = SuspensionDAO.suspender(usuarioId, motivo, dias);

            if (ok) {
                sesionMensaje(request, "mensajeExito",
                        "La cuenta se suspendió correctamente por " + dias + " día(s).");
            } else {
                sesionMensaje(request, "mensajeError",
                        "No se pudo suspender la cuenta. Intentá nuevamente.");
            }

        } else if ("reactivar".equals(accion)) {
            // SuspensionDAO.levantar() ya hace en una transacción:
            //   UPDATE usuario SET activo = TRUE
            //   UPDATE suspension SET activa = FALSE
            //   INSERT notificación al usuario (TipoNotificacion.Suspension)
            boolean ok = SuspensionDAO.levantar(usuarioId);

            if (ok) {
                sesionMensaje(request, "mensajeExito",
                        "La cuenta se reactivó correctamente. El usuario ya puede iniciar sesión.");
            } else {
                sesionMensaje(request, "mensajeError",
                        "No se pudo reactivar la cuenta. Intentá nuevamente.");
            }

        } else {
            sesionMensaje(request, "mensajeError", "Acción no reconocida.");
        }

        response.sendRedirect(request.getContextPath() + "/gestionUsuarios");
    }

    private void sesionMensaje(HttpServletRequest request, String atributo, String valor) {
        HttpSession sesion = request.getSession(true);
        sesion.setAttribute(atributo, valor);
    }

    private Usuario obtenerUsuarioLogueado(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        return (Usuario) sesion.getAttribute("usuarioLogueado");
    }
}
