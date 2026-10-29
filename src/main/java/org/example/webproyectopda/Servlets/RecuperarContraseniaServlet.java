package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.UsuarioDAO;
import org.example.webproyectopda.Modelos.Usuario;

import java.io.IOException;

// CU "Recuperar contraseña". Solo se llega desde el login, después de 2 fallos seguidos con el mismo correo.
@WebServlet("/recuperar")
public class RecuperarContraseniaServlet extends HttpServlet {

    // Entrar escribiendo la URL no sirve: se vuelve al login.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/login");
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

    // Pasos 2, 3 y 4: el usuario confirmó que quiere recuperarla; se busca el correo.
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

        // Paso 4: no existe -> se informa y no se sigue
        if (usuario == null) {
            sesion.removeAttribute("recuperarCorreo");
            request.setAttribute("error", "No existe ningún usuario registrado con el correo ingresado.");
            request.setAttribute("correo", correo);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Existe: se guarda el correo verificado en la sesión para el paso siguiente
        sesion.setAttribute("recuperarCorreo", usuario.getCorreo());
        mostrarFormulario(request, response, usuario.getCorreo(), null);
    }

    // Pasos 5, 6 y 7: contraseña nueva + confirmación.
    private void cambiar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        String correo = sesion == null ? null : (String) sesion.getAttribute("recuperarCorreo");

        // Sin el paso anterior no se puede cambiar nada
        if (correo == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String nueva = request.getParameter("nuevaPassword");
        String repetida = request.getParameter("repetirPassword");

        if (nueva == null || nueva.isBlank() || repetida == null || repetida.isBlank()) {
            mostrarFormulario(request, response, correo, "Escribí la contraseña nueva y repetila para confirmarla.");
            return;
        }

        // Paso 6: no coinciden -> se informa y se vuelven a pedir
        if (!nueva.equals(repetida)) {
            mostrarFormulario(request, response, correo, "Las contraseñas no coinciden.");
            return;
        }

        // Paso 7: coinciden -> se actualiza en la base
        Usuario usuario = UsuarioDAO.buscarPorCorreo(correo);
        if (usuario == null) {
            sesion.removeAttribute("recuperarCorreo");
            request.setAttribute("error", "No existe ningún usuario registrado con el correo ingresado.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        UsuarioDAO.actualizarContrasenia(usuario.getId(), nueva);

        // Se limpia todo lo de la recuperación y se vuelve al login para entrar con la nueva
        sesion.removeAttribute("recuperarCorreo");
        sesion.removeAttribute("intentoCorreo");
        sesion.removeAttribute("intentosFallidos");
        response.sendRedirect(request.getContextPath() + "/login?recuperada=ok");
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   String correo, String error) throws ServletException, IOException {
        request.setAttribute("correoRecuperar", correo);
        request.setAttribute("errorRecuperar", error);
        request.getRequestDispatcher("/recuperarContrasenia.jsp").forward(request, response);
    }
}