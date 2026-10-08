package org.example.webproyectopda.Modelos;

import java.time.LocalDate;

public class Reporte {

    private int id;
    private String contenido;
    private String motivo;
    private String resolucion;
    private LocalDate fechaReporte;
    private LocalDate fechaResolucion;
    private Integer publicacionId;
    private Integer comentarioId;
    private int usuarioId;

    public Reporte(int id, String contenido, String motivo, String resolucion, LocalDate fechaReporte,
                   LocalDate fechaResolucion, Integer publicacionId, Integer comentarioId, int usuarioId) {
        this.id = id;
        this.contenido = contenido;
        this.motivo = motivo;
        this.resolucion = resolucion;
        this.fechaReporte = fechaReporte;
        this.fechaResolucion = fechaResolucion;
        this.publicacionId = publicacionId;
        this.comentarioId = comentarioId;
        this.usuarioId = usuarioId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public LocalDate getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDate fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public LocalDate getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDate fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getPublicacionId() {
        return publicacionId;
    }

    public void setPublicacionId(Integer publicacionId) {
        this.publicacionId = publicacionId;
    }

    public Integer getComentarioId() {
        return comentarioId;
    }

    public void setComentarioId(Integer comentarioId) {
        this.comentarioId = comentarioId;
    }

    @Override
    public String toString() {
        return "Reporte{" +
                "id=" + id +
                ", contenido='" + contenido + '\'' +
                ", motivo='" + motivo + '\'' +
                ", resolucion='" + resolucion + '\'' +
                ", fechaReporte=" + fechaReporte +
                ", fechaResolucion=" + fechaResolucion +
                ", usuarioId=" + usuarioId +
                ", publicacionId=" + publicacionId +
                ", comentarioId=" + comentarioId +
                '}';
    }
}