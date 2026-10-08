package org.example.webproyectopda.Modelos;

import org.example.webproyectopda.ENUMS.TipoCategoria;

import java.time.LocalDate;

public class Mensaje extends org.example.webproyectopda.Modelos.Publicacion {

    private TipoCategoria categoria;

    public Mensaje(TipoCategoria categoria, int usuarioId, int cursoId) {
        this.categoria = categoria;
        this.setUsuarioId(usuarioId);
        this.setCursoId(cursoId);
    }

    public Mensaje(int id, String mensaje, String imagenUrl, LocalDate fechaPublicacion, boolean dadaDeBaja, int usuarioId, int cursoId, TipoCategoria categoria) {
        super(id, mensaje, imagenUrl, fechaPublicacion, dadaDeBaja, usuarioId, cursoId);
        this.categoria = categoria;
    }

    public TipoCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(TipoCategoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "id=" + getId() +
                ", mensaje='" + getMensaje() + '\'' +
                ", fechaPublicacion=" + getFechaPublicacion() +
                ", usuarioId=" + getUsuarioId() +
                ", cursoId=" + getCursoId() +
                ", categoria=" + categoria +
                '}';
    }
}