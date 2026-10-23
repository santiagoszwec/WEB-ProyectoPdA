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
import java.time.LocalDate;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false); //solo si la sesion ya existe

        if (sesion != null && sesion.getAttribute("usuarioLogueado") != null) {
            // si ya inicio sesion
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);

    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String password = request.getParameter("password");

        if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
            volverConError(request, response, "Complete el correo y la contraseña.", correo);
            return;
        }
        correo = correo.trim();

        try{

            Usuario usuario = UsuarioDAO.iniciarSesion(correo, password);

            if (usuario == null) {
                volverConError(request, response, "Correo o contraseña incorrectos.", correo);
                return;
            }

            if (!usuario.isActivo()) {
                Suspension suspension = SuspensionDAO.obtenerSuspensionActiva(usuario.getId());

                if (suspension == null) {
                    volverConError(request, response,
                            "Cuenta desactivada. Contacte al administrador.", correo);
                    return;
                }

                boolean vigente = !suspension.getFechaFin().isBefore(LocalDate.now());

                if (vigente) {
                    volverConError(request, response,
                            "Tu cuenta está suspendida hasta el " + suspension.getFechaFin()
                                    + ". Motivo: " + suspension.getMotivo(), correo);
                    return;
                }

                SuspensionDAO.levantar(usuario.getId());
                usuario.setActivo(true);
            }

            usuario.setContrasenia(null);

            HttpSession antigua = request.getSession(false);
            if (antigua != null) {
                antigua.invalidate();
            }
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuarioLogueado", usuario);

            response.sendRedirect(request.getContextPath() + "/inicio");

        } catch (RuntimeException e) {
            log("Error al iniciar sesión con el correo " + correo, e);
            if (!response.isCommitted()) {
                volverConError(request, response,
                        "No se pudo iniciar sesión en este momento. Intentá de nuevo más tarde.", correo);
            }
        }
    }

    private void volverConError(HttpServletRequest request, HttpServletResponse response,
                                String mensaje, String correo)
            throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.setAttribute("correo", correo);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
