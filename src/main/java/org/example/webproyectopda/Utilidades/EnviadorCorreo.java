package org.example.webproyectopda.Utilidades;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

// Envío de correos por SMTP de Gmail. Las credenciales NO van en el código:
// se leen de variables de entorno (ANSALUNI_SMTP_USER y ANSALUNI_SMTP_PASS).
public class EnviadorCorreo {

    public static void enviarRecuperacion(String destino, String nombre, String enlace) {
        final String usuario = System.getenv("ANSALUNI_SMTP_USER");
        final String clave = System.getenv("ANSALUNI_SMTP_PASS");
        if (usuario == null || usuario.trim().isEmpty() || clave == null || clave.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Falta configurar las variables de entorno ANSALUNI_SMTP_USER y ANSALUNI_SMTP_PASS.");
        }

        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port", "587");
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.connectiontimeout", "10000");
        propiedades.put("mail.smtp.timeout", "10000");
        propiedades.put("mail.smtp.writetimeout", "10000");

        Session sesion = Session.getInstance(propiedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, clave);
            }
        });

        try {
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(usuario, "ANSALUNI"));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            mensaje.setSubject("Recuperar tu contraseña en ANSALUNI", "UTF-8");
            mensaje.setText("Hola " + nombre + ",\n\n"
                    + "Pediste restablecer tu contraseña. Entrá a este enlace para elegir una nueva "
                    + "(vence en 30 minutos y se puede usar una sola vez):\n\n"
                    + enlace + "\n\n"
                    + "Si no fuiste vos, ignorá este correo: tu contraseña no cambia.", "UTF-8");
            Transport.send(mensaje);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}