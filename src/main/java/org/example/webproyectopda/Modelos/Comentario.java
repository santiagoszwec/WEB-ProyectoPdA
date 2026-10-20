package org.example.webproyectopda.Modelos;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Comentario {

    private int id;
    private String mensaje;
    private String imagenUrl;
    private String imagenPublicId;
    private LocalDate fechaPublicacion;
    private int usuarioId;
    private int publicacionId;
    private Integer comentarioPadreId;

    private boolean destacado;
    private boolean activa;
    private String autorNombre;
    private List<Comentario> comentarios = new ArrayList<>();

    // Para crear un comentario nuevo: el id lo genera la base de datos.
    public Comentario(String mensaje, String imagenUrl, LocalDate fechaPublicacion, int usuarioId, int publicacionId, Integer comentarioPadreId) {
        this.mensaje = mensaje;
        this.imagenUrl = imagenUrl;
        this.fechaPublicacion = fechaPublicacion;
        this.usuarioId = usuarioId;
        this.publicacionId = publicacionId;
        this.comentarioPadreId = comentarioPadreId;
        this.destacado = false;
        this.activa = true;
    }

    // Para reconstruir un comentario leído desde la base de datos.
    public Comentario(int id, String mensaje, String imagenUrl, LocalDate fechaPublicacion, int usuarioId, int publicacionId, Integer comentarioPadreId, boolean destacado, boolean activa) {
        this.id = id;
        this.mensaje = mensaje;
        this.imagenUrl = imagenUrl;
        this.fechaPublicacion = fechaPublicacion;
        this.usuarioId = usuarioId;
        this.publicacionId = publicacionId;
        this.comentarioPadreId = comentarioPadreId;
        this.destacado = destacado;
        this.activa = activa;
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

    public String getImagenPublicId() {
        return imagenPublicId;
    }

    public void setImagenPublicId(String imagenPublicId) {
        this.imagenPublicId = imagenPublicId;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getPublicacionId() {
        return publicacionId;
    }

    public void setPublicacionId(int publicacionId) {
        this.publicacionId = publicacionId;
    }

    public Integer getComentarioPadreId() {
        return comentarioPadreId;
    }

    public void setComentarioPadreId(Integer comentarioPadreId) {
        this.comentarioPadreId = comentarioPadreId;
    }
    
    public boolean isDestacado() {
        return destacado;
    }
    
    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getAutorNombre() {
        return autorNombre;
    }

    public void setAutorNombre(String autorNombre) {
        this.autorNombre = autorNombre;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @Override
    public String toString() {
        return "Comentario{" +
                "id=" + id +
                ", mensaje='" + mensaje + '\'' +
                ", imagenUrl='" + imagenUrl + '\'' +
                ", fechaPublicacion=" + fechaPublicacion +
                ", usuarioId=" + usuarioId +
                ", publicacionId=" + publicacionId +
                ", comentarioPadreId=" + comentarioPadreId +
                ", destacado=" + destacado +
                ", activa=" + activa +
                '}';
    }
}
