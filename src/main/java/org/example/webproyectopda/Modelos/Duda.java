package org.example.webproyectopda.Modelos;
import org.example.webproyectopda.ENUMS.EstadoDuda;
import org.example.webproyectopda.ENUMS.TipoCategoria;

import java.time.LocalDate;

public class Duda extends org.example.webproyectopda.Modelos.Publicacion {

    private EstadoDuda estado;
    private TipoCategoria categoria;

    public Duda(EstadoDuda estado, TipoCategoria categoria, int usuarioId, int cursoId) {
        this.estado = estado;
        this.categoria = categoria;
        this.setUsuarioId(usuarioId);
        this.setCursoId(cursoId);
    }

    public Duda(int id, String mensaje, String imagenUrl, LocalDate fechaPublicacion, boolean dadaDeBaja,
            int usuarioId, int cursoId, EstadoDuda estado, TipoCategoria categoria) {
        super(id, mensaje, imagenUrl, fechaPublicacion, dadaDeBaja, usuarioId, cursoId);
        this.estado = estado;
        this.categoria = categoria;
    }

    public EstadoDuda getEstado() {
        return estado;
    }

    public void setEstado(EstadoDuda estado) {
        this.estado = estado;
    }

    public TipoCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(TipoCategoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Duda{" +
                "id=" + getId() +
                ", mensaje='" + getMensaje() + '\'' +
                ", fechaPublicacion=" + getFechaPublicacion() +
                ", usuarioId=" + getUsuarioId() +
                ", cursoId=" + getCursoId() +
                ", estado=" + estado +
                ", categoria=" + categoria +
                '}';
    }
}