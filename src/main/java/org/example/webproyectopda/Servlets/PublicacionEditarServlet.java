package org.example.webproyectopda.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.webproyectopda.DAOS.MaterialDAO;
import org.example.webproyectopda.DAOS.PublicacionDAO;
import org.example.webproyectopda.ENUMS.TipoArchivo;
import org.example.webproyectopda.ENUMS.TipoCategoria;
import org.example.webproyectopda.ENUMS.TipoMaterial;
import org.example.webproyectopda.ENUMS.TipoRol;
import org.example.webproyectopda.Modelos.Material;
import org.example.webproyectopda.Modelos.Publicacion;
import org.example.webproyectopda.Modelos.Usuario;
import org.example.webproyectopda.Utilidades.Validaciones;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Edición de publicaciones: el administrador edita cualquier una y sus campos de
// material; el estudiante únicamente las suyas (mensaje, imagen y categoría).
@WebServlet("/publicacion/editar")
public class PublicacionEditarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Publicacion publicacion = publicacionSolicitada(request, response, usuario);
        if (publicacion == null) {
            return;
        }

        prepararFormulario(request, usuario, publicacion);
        request.getRequestDispatcher("/editarPublicacion.jsp").forward(request, response);
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

        Publicacion publicacion = publicacionSolicitada(request, response, usuario);
        if (publicacion == null) {
            return;
        }

        int id = publicacion.getId();
        boolean esMaterial = PublicacionDAO.esMaterial(id);

        String mensaje = texto(request.getParameter("mensaje"));
        String imagenUrl = texto(request.getParameter("imagenUrl")).trim();
        String categoriaTexto = texto(request.getParameter("categoria")).trim();
        String tipoMaterialTexto = texto(request.getParameter("tipoMaterial")).trim();
        String tipoArchivoTexto = texto(request.getParameter("tipoArchivo")).trim();
        String archivoUrl = texto(request.getParameter("archivoUrl")).trim();

        List<String> errores = validar(esMaterial, mensaje, imagenUrl, categoriaTexto,
                tipoMaterialTexto, tipoArchivoTexto, archivoUrl);

        if (!errores.isEmpty()) {
            // Se conserva lo ingresado para que el usuario pueda corregirlo.
            publicacion.setMensaje(mensaje);
            publicacion.setImagenUrl(imagenUrl);

            prepararFormulario(request, usuario, publicacion);
            request.setAttribute("errores", errores);
            if (esMaterial) {
                request.setAttribute("archivoUrlTexto", archivoUrl);
                request.setAttribute("tipoMaterialTexto", tipoMaterialTexto);
                request.setAttribute("tipoArchivoTexto", tipoArchivoTexto);
            } else {
                request.setAttribute("categoriaActualTexto", categoriaTexto);
            }
            request.getRequestDispatcher("/editarPublicacion.jsp").forward(request, response);
            return;
        }

        publicacion.setMensaje(mensaje.trim());
        publicacion.setImagenUrl(imagenUrl.isEmpty() ? null : imagenUrl);

        boolean actualizado = PublicacionDAO.actualizar(publicacion);

        if (actualizado && esMaterial) {
            actualizado = PublicacionDAO.actualizarMaterial(id, archivoUrl,
                    TipoMaterial.valueOf(tipoMaterialTexto), TipoArchivo.valueOf(tipoArchivoTexto));
        } else if (actualizado) {
            actualizado = PublicacionDAO.actualizarCategoria(id,
                    TipoCategoria.valueOf(categoriaTexto));
        }

        if (!actualizado) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo actualizar la publicación");
            return;
        }

        request.getSession(false).setAttribute("mensajeExito",
                "La publicación se actualizó correctamente.");

        if (usuario.getRol() == TipoRol.Admin) {
            response.sendRedirect(request.getContextPath() + "/gestionPublicaciones");
        } else {
            response.sendRedirect(request.getContextPath() + "/curso?id=" + publicacion.getCursoId());
        }
    }

    // Carga la publicación indicada y valida quién puede editarla.
    // Devuelve null (y responde) si no corresponde continuar.
    private Publicacion publicacionSolicitada(HttpServletRequest request, HttpServletResponse response,
                                              Usuario usuario)
            throws ServletException, IOException {

        Integer id = parsearId(request.getParameter("id"));
        if (id == null) {
            mostrarError(request, response, usuario, "La publicación indicada no es válida.");
            return null;
        }

        Publicacion publicacion = PublicacionDAO.buscarPorId(id);
        if (publicacion == null || publicacion.isDadaDeBaja()) {
            mostrarError(request, response, usuario,
                    "La publicación no existe o fue dada de baja.");
            return null;
        }

        boolean esAdmin = usuario.getRol() == TipoRol.Admin;
        if (!esAdmin && publicacion.getUsuarioId() != usuario.getId()) {
            mostrarError(request, response, usuario,
                    "Solo podés editar tus propias publicaciones.");
            return null;
        }
        if (!esAdmin && PublicacionDAO.esMaterial(id)) {
            mostrarError(request, response, usuario,
                    "Solo el administrador puede editar material.");
            return null;
        }

        return publicacion;
    }

    private List<String> validar(boolean esMaterial, String mensaje, String imagenUrl,
                                 String categoria, String tipoMaterial, String tipoArchivo,
                                 String archivoUrl) {
        List<String> errores = new ArrayList<>();

        if (mensaje == null || mensaje.isBlank()) {
            errores.add("El mensaje no puede estar vacío.");
        } else if (mensaje.trim().length() > Validaciones.MAX_MENSAJE) {
            errores.add("El mensaje no puede superar los " + Validaciones.MAX_MENSAJE + " caracteres.");
        }

        if (!imagenUrl.isEmpty() && !Validaciones.urlValida(imagenUrl)) {
            errores.add("La URL de la imagen debe comenzar con http:// o https:// y tener un formato correcto.");
        }

        if (esMaterial) {
            if (archivoUrl.isEmpty()) {
                errores.add("Ingresá la URL del archivo del material.");
            } else if (!Validaciones.urlValida(archivoUrl)) {
                errores.add("La URL del archivo debe comenzar con http:// o https:// y tener un formato correcto.");
            }
            if (!Validaciones.enumValido(TipoMaterial.class, tipoMaterial)) {
                errores.add("Seleccioná un tipo de material válido: Apuntes, Ejercicio, Libro o Video.");
            }
            if (!Validaciones.enumValido(TipoArchivo.class, tipoArchivo)) {
                errores.add("Seleccioná un tipo de archivo válido: JPG, PDF o PNG.");
            }
        } else {
            if (!Validaciones.enumValido(TipoCategoria.class, categoria)) {
                errores.add("Seleccioná una categoría válida: Ejercicio, Examen o Reunión.");
            }
        }

        return errores;
    }

    private void prepararFormulario(HttpServletRequest request, Usuario usuario,
                                    Publicacion publicacion) {
        boolean esAdmin = usuario.getRol() == TipoRol.Admin;
        boolean esMaterial = PublicacionDAO.esMaterial(publicacion.getId());

        request.setAttribute("publicacion", publicacion);
        request.setAttribute("esMaterial", esMaterial);
        request.setAttribute("esAdmin", esAdmin);

        if (esMaterial) {
            Material material = MaterialDAO.buscarPorId(publicacion.getId());
            request.setAttribute("material", material);
            if (material != null) {
                request.setAttribute("tipoMaterialTexto", material.getTipoMaterial().name());
                request.setAttribute("tipoArchivoTexto", material.getTipoArchivo().name());
                request.setAttribute("archivoUrlTexto", material.getArchivoUrl());
            }
        } else {
            TipoCategoria categoria = PublicacionDAO.buscarCategoria(publicacion.getId());
            request.setAttribute("categoriaActualTexto", categoria == null ? "" : categoria.name());
        }

        request.setAttribute("volverUrl", esAdmin
                ? request.getContextPath() + "/gestionPublicaciones"
                : request.getContextPath() + "/curso?id=" + publicacion.getCursoId());

        request.setAttribute("tiposMaterialDisponibles", TipoMaterial.values());
        request.setAttribute("tiposArchivoDisponibles", TipoArchivo.values());
        request.setAttribute("categoriasDisponibles", TipoCategoria.values());
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response,
                              Usuario usuario, String mensaje)
            throws ServletException, IOException {

        request.setAttribute("error", mensaje);
        request.setAttribute("volverUrl", usuario.getRol() == TipoRol.Admin
                ? request.getContextPath() + "/gestionPublicaciones"
                : request.getContextPath() + "/inicio");
        request.getRequestDispatcher("/editarPublicacion.jsp").forward(request, response);
    }

    private Integer parsearId(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private Usuario obtenerUsuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        return (Usuario) sesion.getAttribute("usuarioLogueado");
    }
}
