package org.example.webproyectopda.Modelos;

import org.example.webproyectopda.ENUMS.TipoEstado;

public class Inscripcion {

    private int id;
    private TipoEstado estado;
    private int usuarioId;
    private int cursoId;

    public Inscripcion(TipoEstado estado) {
        this.estado = estado;
    }

    public Inscripcion(TipoEstado estado, int usuarioId, int cursoId) {
        this.estado = estado;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
    }

    public Inscripcion(int id, TipoEstado estado, int usuarioId, int cursoId) {
        this.id = id;
        this.estado = estado;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoEstado getEstado() {
        return estado;
    }

    public void setEstado(TipoEstado estado) {
        this.estado = estado;
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
        return "Inscripcion{" +
                "id=" + id +
                ", estado=" + estado +
                ", usuarioId=" + usuarioId +
                ", cursoId=" + cursoId +
                '}';
    }
}