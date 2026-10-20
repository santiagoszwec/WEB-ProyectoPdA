package org.example.webproyectopda.Utilidades;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfiguracionCloudinary {

    private static final Properties PROPIEDADES = cargar();

    private static Properties cargar() {
        Properties propiedades = new Properties();
        try (InputStream archivo = ConfiguracionCloudinary.class.getResourceAsStream("/cloudinary.properties")) {
            if (archivo != null) propiedades.load(new InputStreamReader(archivo, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer cloudinary.properties", e);
        }
        return propiedades;
    }

    public static String getCloudName() {
        return System.getProperty("cloudinary.cloud_name",
                PROPIEDADES.getProperty("cloudinary.cloud_name", "")).strip();
    }

    public static String getUploadPreset() {
        return System.getProperty("cloudinary.upload_preset",
                PROPIEDADES.getProperty("cloudinary.upload_preset", "")).strip();
    }

    public static boolean isConfigurada() {
        return getCloudName().matches("[a-z0-9_-]+")
                && !getUploadPreset().isBlank() && !getUploadPreset().startsWith("TU_");
    }
}