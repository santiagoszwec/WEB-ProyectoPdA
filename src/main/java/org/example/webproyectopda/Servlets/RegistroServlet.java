package org.example.webproyectopda.Servlets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.example.webproyectopda.DAOS.UsuarioDAO;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Usuario;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = request.getParameter("correo");
        String nombre = request.getParameter("nombre");
        String password = request.getParameter("password");
        String anio = request.getParameter("anioDeGeneracion");
        String repetirPassword = request.getParameter("repetirPassword");

        if (correo == null || correo.isBlank() || password == null || password.isBlank() ||
                nombre == null || nombre.isBlank() || anio == null || anio.isBlank()) {
            request.setAttribute("error", "Todos los campos son obligatorios");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        int anioDeGeneracion;
        try {
            anioDeGeneracion = Integer.parseInt(anio);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El año de generación debe ser un número");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        try {
            Usuario existe = UsuarioDAO.buscarPorCorreo(correo.trim());

            if (existe != null) {
                request.setAttribute("error", "Ya existe un usuario con ese correo");
                request.getRequestDispatcher("/registro.jsp").forward(request, response);
                return;
            }
            if (anioDeGeneracion < 2008 || anioDeGeneracion > 2027) {
                request.setAttribute("error", "Año inválido");
                request.getRequestDispatcher("/registro.jsp").forward(request, response);
                return;
            }

            if (!password.equals(repetirPassword)) {
                request.setAttribute("error", "Las contraseñas no coinciden");
                request.getRequestDispatcher("/registro.jsp").forward(request, response);
                return;
            }

            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));

            Usuario usuario = new Usuario(nombre.trim(), correo.trim(), anioDeGeneracion, TipoRol.Estudiante, passwordHash);

            UsuarioDAO.crear(usuario);

            response.sendRedirect(request.getContextPath() + "/login?registro=ok");

        } catch (RuntimeException e) {
            log("Error al registrar el usuario", e);

            request.setAttribute("error", "No se pudo completar el registro. Intentá nuevamente.");

            request.getRequestDispatcher("/registro.jsp").forward(request, response);
        }
    }
}
