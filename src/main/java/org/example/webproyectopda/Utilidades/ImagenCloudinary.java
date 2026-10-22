package org.example.webproyectopda.Utilidades;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class ImagenCloudinary {

    public static void validar(String url, String publicId) {
        if (url.isEmpty() && publicId.isEmpty()) return;               // imagen opcional
        if (!ConfiguracionCloudinary.isConfigurada())
            throw new IllegalArgumentException("La subida de imágenes aún no está configurada.");
        if (url.isEmpty() || publicId.isEmpty())
            throw new IllegalArgumentException("La imagen está incompleta. Volvé a seleccionarla.");
        if (url.length() > 1000 || publicId.length() > 255
                || !publicId.matches("[A-Za-z0-9_-]+(?:/[A-Za-z0-9_-]+)*"))
            throw new IllegalArgumentException("El identificador de la imagen no es válido.");
        try {
            URI uri = new URI(url);
            String ruta = "/" + ConfiguracionCloudinary.getCloudName() + "/image/upload/";
            String patron = Pattern.quote(ruta) + "(?:v[0-9]+/)?"
                    + Pattern.quote(publicId) + "[.](?:jpg|jpeg|png|pdf)";
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || !"res.cloudinary.com".equalsIgnoreCase(uri.getHost())
                    || (uri.getPort() != -1 && uri.getPort() != 443)
                    || uri.getUserInfo() != null || uri.getQuery() != null || uri.getFragment() != null
                    || uri.getRawPath() == null || !uri.getRawPath().matches(patron))
                throw new IllegalArgumentException("La URL no corresponde a una imagen del Cloudinary configurado.");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("La URL de la imagen no es válida.");
        }
    }
}