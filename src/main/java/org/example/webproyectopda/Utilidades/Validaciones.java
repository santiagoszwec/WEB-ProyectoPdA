package org.example.webproyectopda.Utilidades;

import java.net.URI;
import java.net.URISyntaxException;

// Reglas de validación compartidas entre la creación y la edición de publicaciones.
public final class Validaciones {

    public static final int MAX_MENSAJE = 2000;
    public static final int MAX_URL = 255;

    private Validaciones() {
    }

    // URL con esquema http/https y host real, hasta MAX_URL caracteres.
    public static boolean urlValida(String url) {
        if (url == null || url.isEmpty() || url.length() > MAX_URL) {
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

    public static <E extends Enum<E>> boolean enumValido(Class<E> tipoEnum, String valor) {
        if (valor == null || valor.isBlank()) {
            return false;
        }
        for (E constante : tipoEnum.getEnumConstants()) {
            if (constante.name().equals(valor.trim())) {
                return true;
            }
        }
        return false;
    }
}
