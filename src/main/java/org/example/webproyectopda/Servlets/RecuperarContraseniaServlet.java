package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.RecuperacionDAO;
import org.example.webproyectopda.DAOS.UsuarioDAO;
import org.example.webproyectopda.Modelos.Usuario;
import org.example.webproyectopda.Utilidades.EnviadorCorreo;

import java.io.IOException;

// CU "Recuperar contraseña". Se pide desde el login (tras 2 fallos seguidos con el mismo correo),
// se manda por correo un enlace con token de un solo uso y ese enlace permite elegir la contraseña nueva.
@WebServlet("/recuperar")
public class RecuperarContraseniaServlet extends HttpServlet {

    // El GET abre el enlace del correo. Sin token se vuelve al login.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = texto(request.getParameter("token")).trim();
        if (token.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Integer usuarioId = RecuperacionDAO.usuarioDelToken(token);
            Usuario usuario = usuarioId == null ? null : UsuarioDAO.buscarPorId(usuarioId);
            if (usuario == null) {
                enlaceInvalido(request, response);
                return;
            }
            mostrarFormulario(request, response, usuario.getCorreo(), token, null);

        } catch (RuntimeException e) {
            log("Error al abrir el enlace de recuperación", e);
            request.setAttribute("error", "No se pudo completar la operación. Intentá de nuevo más tarde.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        try {
            if ("confirmar".equals(accion)) {
                confirmar(request, response);
            } else if ("cambiar".equals(accion)) {
                cambiar(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
        } catch (RuntimeException e) {
            log("Error al recuperar la contraseña", e);
            request.setAttribute("error", "No se pudo completar la operación. Intentá de nuevo más tarde.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    // El usuario confirmó que quiere recuperarla: se busca el correo y se manda el enlace.
    private void confirmar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        Integer fallos = sesion == null ? null : (Integer) sesion.getAttribute("intentosFallidos");
        String correo = sesion == null ? null : (String) sesion.getAttribute("intentoCorreo");

        // Precondición: al menos 2 fallos consecutivos con el mismo correo
        if (fallos == null || fallos < 2 || correo == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Usuario usuario = UsuarioDAO.buscarPorCorreo(correo);

        // No existe -> se informa y no se sigue
        if (usuario == null) {
            request.setAttribute("error", "No existe ningún usuario registrado con el correo ingresado.");
            request.setAttribute("correo", correo);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Se genera el token y se manda el enlace por correo
        String token = RecuperacionDAO.crear(usuario.getId());
        String enlace = urlBase(request) + "/recuperar?token=" + token;

        try {
            EnviadorCorreo.enviarRecuperacion(usuario.getCorreo(), usuario.getNombre(), enlace);
        } catch (RuntimeException e) {
            // El enlace NO se escribe en el log: es un secreto
            log("No se pudo enviar el correo de recuperación a " + usuario.getCorreo(), e);
            request.setAttribute("error", "No se pudo enviar el correo. Intentá de nuevo más tarde.");
            request.setAttribute("correo", correo);
            request.setAttribute("ofrecerRecuperar", true); // para que pueda reintentar
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        request.setAttribute("correoRecuperar", usuario.getCorreo());
        request.getRequestDispatcher("/recuperarEnlace.jsp").forward(request, response);
    }

    // Contraseña nueva + confirmación, con el token del enlace.
    private void cambiar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = texto(request.getParameter("token")).trim();
        Integer usuarioId = token.isEmpty() ? null : RecuperacionDAO.usuarioDelToken(token);
        Usuario usuario = usuarioId == null ? null : UsuarioDAO.buscarPorId(usuarioId);
        if (usuario == null) {
            enlaceInvalido(request, response);
            return;
        }

        String nueva = request.getParameter("nuevaPassword");
        String repetida = request.getParameter("repetirPassword");

        if (nueva == null || nueva.trim().isEmpty() || repetida == null || repetida.trim().isEmpty()) {
            mostrarFormulario(request, response, usuario.getCorreo(), token,
                    "Escribí la contraseña nueva y repetila para confirmarla.");
            return;
        }

        if (!nueva.equals(repetida)) {
            mostrarFormulario(request, response, usuario.getCorreo(), token, "Las contraseñas no coinciden.");
            return;
        }

        // El token se gasta antes de cambiar la clave (sirve una sola vez)
        if (!RecuperacionDAO.consumir(token)) {
            enlaceInvalido(request, response);
            return;
        }
        UsuarioDAO.actualizarContrasenia(usuario.getId(), nueva);

        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.removeAttribute("intentoCorreo");
            sesion.removeAttribute("intentosFallidos");
        }
        response.sendRedirect(request.getContextPath() + "/login?recuperada=ok");
    }

    private void enlaceInvalido(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("error",
                "El enlace no es válido o ya venció. Volvé a pedir la recuperación desde el inicio de sesión.");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   String correo, String token, String error)
            throws ServletException, IOException {
        request.setAttribute("correoRecuperar", correo);
        request.setAttribute("tokenRecuperar", token);
        request.setAttribute("errorRecuperar", error);
        request.getRequestDispatcher("/recuperarContrasenia.jsp").forward(request, response);
    }

    private String urlBase(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }
}