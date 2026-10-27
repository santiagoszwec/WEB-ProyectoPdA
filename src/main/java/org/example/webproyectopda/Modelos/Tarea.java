package org.example.webproyectopda.Modelos;

import java.time.LocalDate;

public class Tarea {

    private int id;
    private String titulo;
    private LocalDate fechaLimite;
    private boolean completada;
    private LocalDate fechaCompletada;
    private int inscripcionId;

    public Tarea() {
    }

    public Tarea(String titulo, LocalDate fechaLimite, int inscripcionId) {
        this.titulo = titulo;
        this.fechaLimite = fechaLimite;
        this.inscripcionId = inscripcionId;
    }

    public boolean isVencida() {
        return !completada && fechaLimite != null && fechaLimite.isBefore(LocalDate.now());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public LocalDate getFechaCompletada() {
        return fechaCompletada;
    }

    public void setFechaCompletada(LocalDate fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public int getInscripcionId() {
        return inscripcionId;
    }

    public void setInscripcionId(int inscripcionId) {
        this.inscripcionId = inscripcionId;
    }
}