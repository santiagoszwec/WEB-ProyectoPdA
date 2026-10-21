package org.example.webproyectopda.Servlets;

import org.example.webproyectopda.DAOS.*;
import org.example.webproyectopda.ENUMS.*;
import org.example.webproyectopda.Modelos.*;
import org.example.webproyectopda.Utilidades.Validaciones;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/curso")
public class CursoServlet extends HttpServlet {

    private static final int MAX_MENSAJE = Validaciones.MAX_MENSAJE;

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

            if (usuario.getRol() != TipoRol.Admin
                    && !InscripcionDAO.existeInscripcion(usuario.getId(), curso.getId())) {
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

            String archivoUrl = request.getParameter("archivoUrl");
            String tipoMaterialTexto = request.getParameter("tipoMaterial");
            String tipoArchivoTexto = request.getParameter("tipoArchivo");
            String tema = request.getParameter("tema");

            List<String> errores = validar(usuario, mensaje, adjuntar, imagenUrl, tipo,
                    categoriaTexto, archivoUrl, tipoMaterialTexto, tipoArchivoTexto, tema);

            if (!errores.isEmpty()) {

                request.setAttribute("errores", errores);
                request.setAttribute("valorMensaje", mensaje);
                request.setAttribute("valorAdjuntar", adjuntar);
                request.setAttribute("valorImagenUrl", imagenUrl);
                request.setAttribute("valorTipo", tipo);
                request.setAttribute("valorCategoria", categoriaTexto);
                request.setAttribute("valorArchivoUrl", archivoUrl);
                request.setAttribute("valorTipoMaterial", tipoMaterialTexto);
                request.setAttribute("valorTipoArchivo", tipoArchivoTexto);
                request.setAttribute("valorTema", tema);
                mostrarCurso(request, response, curso, usuario);
                return;
            }

            String urlFinal = adjuntar ? imagenUrl.trim() : null;

            Publicacion publicacion;
            boolean guardada;
            TipoNotificacion tipoNotificacion;
            String etiquetaNotificacion;

            if ("Duda".equals(tipo)) {

                Duda duda = new Duda(EstadoDuda.Abierta,
                        TipoCategoria.valueOf(categoriaTexto), usuario.getId(), curso.getId());
                completarPublicacion(duda, mensaje, urlFinal);
                guardada = DudaDAO.crear(duda);
                publicacion = duda;
                tipoNotificacion = TipoNotificacion.Duda;
                etiquetaNotificacion = "Nueva duda";

            } else if ("Material".equals(tipo)) {

                Material material = new Material(archivoUrl.trim(),
                        TipoMaterial.valueOf(tipoMaterialTexto),
                        TipoArchivo.valueOf(tipoArchivoTexto),
                        tema.trim(), usuario.getId(), curso.getId());
                completarPublicacion(material, mensaje, urlFinal);
                guardada = MaterialDAO.crear(material);
                publicacion = material;
                tipoNotificacion = TipoNotificacion.Material;
                etiquetaNotificacion = "Nuevo material";

            } else {

                Mensaje m = new Mensaje(TipoCategoria.valueOf(categoriaTexto), usuario.getId(), curso.getId());
                completarPublicacion(m, mensaje, urlFinal);
                guardada = MensajeDAO.crear(m);
                publicacion = m;
                tipoNotificacion = TipoNotificacion.Mensaje;
                etiquetaNotificacion = "Nuevo mensaje";
            }

            if (!guardada) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo guardar la publicación");
                return;
            }

            notificarCompanieros(publicacion, tipoNotificacion, etiquetaNotificacion, usuario, curso);

            request.getSession(false).setAttribute("mensajeExito", "La publicación se creó correctamente.");
            response.sendRedirect(request.getContextPath() + "/curso?id=" + curso.getId());

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de curso inválido");
        }
    }

    private List<String> validar(Usuario usuario, String mensaje, boolean adjuntar, String imagenUrl,
                                 String tipo, String categoria, String archivoUrl,
                                 String tipoMaterial, String tipoArchivo, String tema) {
        List<String> errores = new ArrayList<>();
        boolean esAdmin = usuario.getRol() == TipoRol.Admin;

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

        boolean esDuda = "Duda".equals(tipo);
        boolean esMensaje = "Mensaje".equals(tipo);
        boolean esMaterial = "Material".equals(tipo);

        if (!esDuda && !esMensaje && !esMaterial) {
            errores.add("Seleccioná un tipo de publicación válido: Duda o Mensaje"
                    + (esAdmin ? " o Material." : "."));
        }

        if (esMaterial && !esAdmin) {
            errores.add("Solo el administrador puede publicar material.");
        }

        if (esMaterial) {
            if (archivoUrl == null || archivoUrl.isBlank()) {
                errores.add("Ingresá la URL del archivo del material.");
            } else if (!urlValida(archivoUrl.trim())) {
                errores.add("La URL del archivo debe comenzar con http:// o https:// y tener un formato correcto.");
            }

            if (!esEnumValido(TipoMaterial.class, tipoMaterial)) {
                errores.add("Seleccioná un tipo de material válido: Apuntes, Ejercicio, Libro o Video.");
            }
            if (!esEnumValido(TipoArchivo.class, tipoArchivo)) {
                errores.add("Seleccioná un tipo de archivo válido: JPG, PDF o PNG.");
            }

            if (tema == null || tema.isBlank()) {
                errores.add("Ingresá el tema del material.");
            } else if (tema.trim().length() > 100) {
                errores.add("El tema no puede superar los 100 caracteres.");
            }

        } else if (esDuda || esMensaje) {
            if (!esEnumValido(TipoCategoria.class, categoria)) {
                errores.add("Seleccioná una categoría válida: Ejercicio, Examen o Reunión.");
            }
        }

        return errores;
    }

    private <E extends Enum<E>> boolean esEnumValido(Class<E> tipoEnum, String valor) {
        return Validaciones.enumValido(tipoEnum, valor);
    }

    private boolean urlValida(String url) {
        return Validaciones.urlValida(url);
    }

    private void completarPublicacion(Publicacion publicacion, String mensaje, String imagenUrl) {
        publicacion.setMensaje(mensaje.trim());
        publicacion.setImagenUrl(imagenUrl);
        publicacion.setFechaPublicacion(LocalDate.now());
    }

    private void notificarCompanieros(Publicacion publicacion, TipoNotificacion tipo,
                                      String etiqueta, Usuario autor, Curso curso) {
        String texto = etiqueta + " en " + curso.getNombre() + ", publicado por " + autor.getNombre() + ".";

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
        request.setAttribute("esAdmin", usuario.getRol() == TipoRol.Admin);

        cargarMateriales(request, usuario, curso);

        // El formulario solo aparece para administradores y usuarios inscriptos
        // en un curso del año actual
        boolean delAnioActual = esDelAnioActual(curso);
        boolean inscripto = InscripcionDAO.existeInscripcion(usuario.getId(), curso.getId());
        boolean esAdmin = usuario.getRol() == TipoRol.Admin;
        request.setAttribute("puedePublicar", delAnioActual && (esAdmin || inscripto));
        request.setAttribute("cursoDelAnioActual", delAnioActual);

        // Compañeros que cursan la materia, para acceder a sus perfiles públicos
        if (!esAdmin) {
            request.setAttribute("companieros",
                    InscripcionDAO.listarCompanierosCursando(curso.getId(), usuario.getId()));
        }

        TipoEstado estadoCurso = InscripcionDAO.obtenerEstado(usuario.getId(), curso.getId());
        request.setAttribute("estadoCurso", estadoCurso == null ? null : estadoCurso.name());
        request.getRequestDispatcher("/curso.jsp").forward(request, response);
    }

    // Sección "Material de Estudio": lista filtrada por las etiquetas elegidas en el request.
    private void cargarMateriales(HttpServletRequest request, Usuario usuario, Curso curso) {
        List<TipoMaterial> tiposMaterial =
                parsearEnums(request.getParameterValues("tipoMaterial"), TipoMaterial.class);
        List<TipoArchivo> tiposArchivo =
                parsearEnums(request.getParameterValues("tipoArchivo"), TipoArchivo.class);

        List<Publicacion> materiales;
        if (usuario.getRol() == TipoRol.Admin) {
            materiales = PublicacionDAO.filtrarMaterialesPorEtiquetas(
                    tiposMaterial, tiposArchivo, curso.getId());
        } else {
            materiales = PublicacionDAO.filtrarMaterialesPorEtiquetas(
                    tiposMaterial, tiposArchivo, usuario.getId(), curso.getId());
        }

        request.setAttribute("materiales", materiales);
        request.setAttribute("filtroTipoMaterial", tiposMaterial);
        request.setAttribute("filtroTipoArchivo", tiposArchivo);
        request.setAttribute("tiposMaterialDisponibles", TipoMaterial.values());
        request.setAttribute("tiposArchivoDisponibles", TipoArchivo.values());
    }

    private <E extends Enum<E>> List<E> parsearEnums(String[] valores, Class<E> tipoEnum) {
        List<E> retorno = new ArrayList<>();
        if (valores == null) {
            return retorno;
        }
        for (String valor : valores) {
            if (valor == null || valor.isBlank()) {
                continue;
            }
            try {
                E enumValor = Enum.valueOf(tipoEnum, valor.trim());
                if (!retorno.contains(enumValor)) {
                    retorno.add(enumValor);
                }
            } catch (IllegalArgumentException e) {
                // valor desconocido: se ignora en lugar de romper la consulta
            }
        }
        return retorno;
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