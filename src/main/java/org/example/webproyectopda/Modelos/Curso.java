package org.example.webproyectopda.Modelos;

public class Curso {

    private int id;
    private String nombre;
    private int semestre;
    private int anio;
    private int creditos;
    private String descripcion;
    private boolean activo;

    // Constructor utilizado al obtener un curso desde la base de datos
    public Curso(
            int id,
            String nombre,
            int semestre,
            int anio,
            int creditos,
            String descripcion,
            boolean activo
    ) {
        this.id = id;
        this.nombre = nombre;
        this.semestre = semestre;
        this.anio = anio;
        this.creditos = creditos;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // Constructor utilizado para crear un curso nuevo
    public Curso(
            String nombre,
            int semestre,
            int anio,
            int creditos,
            String descripcion
    ) {
        this.nombre = nombre;
        this.semestre = semestre;
        this.anio = anio;
        this.creditos = creditos;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", semestre=" + semestre +
                ", anio=" + anio +
                ", creditos=" + creditos +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                '}';
    }
}