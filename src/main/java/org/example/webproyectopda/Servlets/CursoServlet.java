package org.example.webproyectopda.Servlets;

import org.example.webproyectopda.DAOS.*;
import org.example.webproyectopda.ENUMS.*;
import org.example.webproyectopda.Modelos.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/curso")
public class CursoServlet extends HttpServlet {

    private static final int MAX_MENSAJE = 2000;
    private static final int MAX_URL = 255;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        try {
            int idCurso = Integer.parseInt(request.getParameter("id"));
            Curso curso = CursoDAO.buscarPorId(idCurso);

            if (curso == null || !curso.isActivo()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Curso no encontrado");
                return;
            }

            if (usuario.getRol() != TipoRol.Admin
                    && !InscripcionDAO.existeInscripcion(usuario.getId(), curso.getId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "No estás inscripto en este curso");
                return;
            }

            HttpSession sesion = request.getSession(false);
            Object exito = sesion.getAttribute("mensajeExito");
            if (exito != null) {
                request.setAttribute("mensajeExito", exito);
                sesion.removeAttribute("mensajeExito");
            }

            mostrarCurso(request, response, curso, usuario);

            /*request.setAttribute("curso", curso);
            request.setAttribute("publicaciones", PublicacionDAO.listarPorCurso(idCurso));

            request.getRequestDispatcher("/curso.jsp").forward(request, response);
*/
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "ID de curso inválido");
        }
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

        try {
            Curso curso = CursoDAO.buscarPorId(Integer.parseInt(request.getParameter("id")));

            if (curso == null || !curso.isActivo()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Curso no encontrado");
                return;
            }

            if (!InscripcionDAO.existeInscripcion(usuario.getId(), curso.getId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "No podés publicar en este curso");
                return;
            }

            if (!esDelAnioActual(curso)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Este curso no es del año actual: solo se puede consultar");
                return;
            }

            String mensaje = request.getParameter("mensaje");
            boolean adjuntar = request.getParameter("adjuntarImagen") != null;
            String imagenUrl = request.getParameter("imagenUrl");
            String tipo = request.getParameter("tipo");
            String categoriaTexto = request.getParameter("categoria");

            List<String> errores = validar(mensaje, adjuntar, imagenUrl, tipo, categoriaTexto);

            if (!errores.isEmpty()) {

                request.setAttribute("errores", errores);
                request.setAttribute("valorMensaje", mensaje);
                request.setAttribute("valorAdjuntar", adjuntar);
                request.setAttribute("valorImagenUrl", imagenUrl);
                request.setAttribute("valorTipo", tipo);
                request.setAttribute("valorCategoria", categoriaTexto);
                mostrarCurso(request, response, curso, usuario);
                return;
            }


            TipoCategoria categoria = TipoCategoria.valueOf(categoriaTexto);
            String urlFinal = adjuntar ? imagenUrl.trim() : null;
            boolean esDuda = "Duda".equals(tipo);

            Publicacion publicacion;
            boolean guardada;
            if (esDuda) {

                Duda duda = new Duda(EstadoDuda.Abierta, categoria, usuario.getId(), curso.getId());
                completarPublicacion(duda, mensaje, urlFinal);
                guardada = DudaDAO.crear(duda);
                publicacion = duda;
            } else {
                Mensaje m = new Mensaje(categoria, usuario.getId(), curso.getId());
                completarPublicacion(m, mensaje, urlFinal);
                guardada = MensajeDAO.crear(m);
                publicacion = m;
            }

            if (!guardada) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo guardar la publicación");
                return;
            }

            notificarCompanieros(publicacion, esDuda, usuario, curso);

            request.getSession(false).setAttribute("mensajeExito", "La publicación se creó correctamente.");
            response.sendRedirect(request.getContextPath() + "/curso?id=" + curso.getId());

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de curso inválido");
        }
    }

    private List<String> validar(String mensaje, boolean adjuntar, String imagenUrl,
                                 String tipo, String categoria) {
        List<String> errores = new ArrayList<>();


        if (mensaje == null || mensaje.isBlank()) {
            errores.add("El mensaje no puede estar vacío.");
        } else if (mensaje.trim().length() > MAX_MENSAJE) {
            errores.add("El mensaje no puede superar los " + MAX_MENSAJE + " caracteres.");
        }


        if (adjuntar) {
            if (imagenUrl == null || imagenUrl.isBlank()) {
                errores.add("Ingresá la URL de la imagen o desmarcá la opción de adjuntar.");
            } else if (!urlValida(imagenUrl.trim())) {
                errores.add("La URL de la imagen debe comenzar con http:// o https:// y tener un formato correcto.");
            }
        }

        if (!"Duda".equals(tipo) && !"Mensaje".equals(tipo)) {
            errores.add("Seleccioná un tipo de publicación válido: Duda o Mensaje.");
        }
        boolean categoriaValida = false;
        if (categoria != null) {
            for (TipoCategoria c : TipoCategoria.values()) {
                if (c.name().equals(categoria)) {
                    categoriaValida = true;
                }
            }
        }
        if (!categoriaValida) {
            errores.add("Seleccioná una categoría válida: Ejercicio, Examen o Reunión.");
        }
        return errores;
    }

    private boolean urlValida(String url) {
        if (url.length() > MAX_URL) {
            return false;
        }
        String minusculas = url.toLowerCase();
        if (!minusculas.startsWith("http://") && !minusculas.startsWith("https://")) {
            return false;
        }
        try {
            URI uri = new URI(url);
            return uri.getHost() != null && !uri.getHost().isBlank();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private void completarPublicacion(Publicacion publicacion, String mensaje, String imagenUrl) {
        publicacion.setMensaje(mensaje.trim());
        publicacion.setImagenUrl(imagenUrl);
        publicacion.setFechaPublicacion(LocalDate.now());
    }

    private void notificarCompanieros(Publicacion publicacion, boolean esDuda, Usuario autor, Curso curso) {
        String texto = (esDuda ? "Nueva duda" : "Nuevo mensaje")
                + " en " + curso.getNombre() + ", publicado por " + autor.getNombre() + ".";
        TipoNotificacion tipo = esDuda ? TipoNotificacion.Duda : TipoNotificacion.Mensaje;

        for (int idCompanero : InscripcionDAO.listarUsuariosCursando(curso.getId())) {
            if (idCompanero == autor.getId()) {
                continue; // el autor no se notifica a sí mismo
            }
            try {
                NotificacionDAO.crear(new Notificacion(LocalDate.now(), tipo, texto,
                        idCompanero, publicacion.getId()));
            } catch (RuntimeException e) {
                // La publicación ya está guardada: si falla un aviso no la deshacemos
                e.printStackTrace();
            }
        }
    }

    private void mostrarCurso(HttpServletRequest request, HttpServletResponse response,
                              Curso curso, Usuario usuario) throws ServletException, IOException {
        request.setAttribute("curso", curso);
        request.setAttribute("publicaciones", PublicacionDAO.listarFeedPorCurso(curso.getId()));

        // El formulario solo aparece para usuarios inscriptos en un curso del año actual
        boolean delAnioActual = esDelAnioActual(curso);
        boolean inscripto = InscripcionDAO.existeInscripcion(usuario.getId(), curso.getId());
        request.setAttribute("puedePublicar", delAnioActual && inscripto);
        request.setAttribute("cursoDelAnioActual", delAnioActual);

        TipoEstado estadoCurso = InscripcionDAO.obtenerEstado(usuario.getId(), curso.getId());
        request.setAttribute("estadoCurso", estadoCurso == null ? null : estadoCurso.name());
        request.getRequestDispatcher("/curso.jsp").forward(request, response);
    }

    private boolean esDelAnioActual(Curso curso) {
        return curso.getAnio() == LocalDate.now().getYear();
    }

    private Usuario obtenerUsuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        return (Usuario) sesion.getAttribute("usuarioLogueado");
    }

}