package org.example.webproyectopda.Modelos;

import java.time.LocalDate;

public class Suspension {

    private int id;
    private int usuarioId;
    private String motivo;
    private LocalDate fechaSuspension;
    private LocalDate fechaFin;
    private boolean activa;

    public Suspension(int id, int usuarioId, String motivo, LocalDate fechaSuspension, LocalDate fechaFin, boolean activa) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.motivo = motivo;
        this.fechaSuspension = fechaSuspension;
        this.fechaFin = fechaFin;
        this.activa = activa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDate getFechaSuspension() {
        return fechaSuspension;
    }

    public void setFechaSuspension(LocalDate fechaSuspension) {
        this.fechaSuspension = fechaSuspension;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return "Suspension{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", motivo='" + motivo + '\'' +
                ", fechaSuspension=" + fechaSuspension +
                ", fechaFin=" + fechaFin +
                ", activa=" + activa +
                '}';
    }
}