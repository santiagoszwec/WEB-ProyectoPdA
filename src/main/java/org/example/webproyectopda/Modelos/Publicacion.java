package org.example.webproyectopda.Modelos;
import org.example.webproyectopda.ENUMS.TipoCategoria;

import java.time.LocalDate;


public class Publicacion {

    private int id;
    private String mensaje;
    private String imagenUrl;
    private LocalDate fechaPublicacion;
    private boolean dadaDeBaja;
    private int usuarioId;
    private int cursoId;

    public Publicacion() {
    }

    public Publicacion(int id, String mensaje, String imagenUrl, LocalDate fechaPublicacion, boolean dadaDeBaja) {
        this.id = id;
        this.mensaje = mensaje;
        this.imagenUrl = imagenUrl;
        this.fechaPublicacion = fechaPublicacion;
        this.dadaDeBaja = dadaDeBaja;
    }

    public Publicacion(int id, String mensaje, String imagenUrl, LocalDate fechaPublicacion, boolean dadaDeBaja, int usuarioId, int cursoId) {
        this.id = id;
        this.mensaje = mensaje;
        this.imagenUrl = imagenUrl;
        this.fechaPublicacion = fechaPublicacion;
        this.dadaDeBaja = dadaDeBaja;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public boolean isDadaDeBaja() {
        return dadaDeBaja;
    }

    public void setDadaDeBaja(boolean dadaDeBaja) {
        this.dadaDeBaja = dadaDeBaja;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    @Override
    public String toString() {
        return "Publicacion{" +
                "id=" + id +
                ", mensaje='" + mensaje + '\'' +
                ", imagenUrl='" + imagenUrl + '\'' +
                ", fechaPublicacion=" + fechaPublicacion +
                ", dadaDeBaja=" + dadaDeBaja +
                ", usuarioId=" + usuarioId +
                ", cursoId=" + cursoId +
                '}';
    }
}