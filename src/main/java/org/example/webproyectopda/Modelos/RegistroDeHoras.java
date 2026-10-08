package org.example.webproyectopda.Modelos;
import java.time.LocalDate;

public class RegistroDeHoras {

    private int id;
    private float cantidadDeHoras;
    private LocalDate fecha;
    private int inscripcionId;

    public RegistroDeHoras(float cantidadDeHoras, LocalDate fecha, int inscripcionId) {
        this.cantidadDeHoras = cantidadDeHoras;
        this.fecha = fecha;
        this.inscripcionId = inscripcionId;
    }

    public RegistroDeHoras(int id, float cantidadDeHoras, LocalDate fecha, int inscripcionId) {
        this.id = id;
        this.cantidadDeHoras = cantidadDeHoras;
        this.fecha = fecha;
        this.inscripcionId = inscripcionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getCantidadDeHoras() {
        return cantidadDeHoras;
    }

    public void setCantidadDeHoras(float cantidadDeHoras) {
        this.cantidadDeHoras = cantidadDeHoras;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getInscripcionId() {
        return inscripcionId;
    }

    public void setInscripcionId(int inscripcionId) {
        this.inscripcionId = inscripcionId;
    }

    @Override
    public String toString() {
        return "RegistroDeHoras{" +
                "id=" + id +
                ", cantidadDeHoras=" + cantidadDeHoras +
                ", fecha=" + fecha +
                ", inscripcionId=" + inscripcionId +
                '}';
    }
}