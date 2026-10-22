package org.example.webproyectopda.Modelos;
import org.example.webproyectopda.ENUMS.TipoNotificacion;

import java.time.LocalDate;

public class Notificacion {

    private int id;
    private LocalDate fecha;
    private TipoNotificacion tipo;
    private String mensaje;
    private int usuarioId;
    private Integer publicacionId;
    private Integer cursoId;
    private boolean esDuda;
    private boolean publicacionActiva;

    public Notificacion() {
    }

    public Notificacion(LocalDate fecha, TipoNotificacion tipo, String mensaje, int usuarioId, Integer publicacionId) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.usuarioId = usuarioId;
        this.publicacionId = publicacionId;
    }

    public Notificacion(int id, LocalDate fecha, TipoNotificacion tipo, String mensaje, int usuarioId, Integer publicacionId) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.usuarioId = usuarioId;
        this.publicacionId = publicacionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
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

    public Integer getCursoId() {
        return cursoId;
    }

    public void setCursoId(Integer cursoId) {
        this.cursoId = cursoId;
    }
    public boolean isEsDuda() {
        return esDuda;
    }

    public void setEsDuda(boolean esDuda) {
        this.esDuda = esDuda;
    }

    public boolean isPublicacionActiva() {
        return publicacionActiva;
    }

    public void setPublicacionActiva(boolean publicacionActiva) {
        this.publicacionActiva = publicacionActiva;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", tipo=" + tipo +
                ", mensaje='" + mensaje + '\'' +
                ", usuarioId=" + usuarioId +
                ", publicacionId=" + publicacionId +
                '}';
    }
}
