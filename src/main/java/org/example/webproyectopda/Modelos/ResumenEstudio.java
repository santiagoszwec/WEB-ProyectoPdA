package org.example.webproyectopda.Modelos;

import org.example.webproyectopda.DAOS.RegistroDeHorasDAO;

import java.util.ArrayList;
import java.util.List;

public class ResumenEstudio {

    public static class Materia {
        private final String nombre;
        private final double horas;
        private final int porcentaje;

        public Materia(String nombre, double horas, int porcentaje) {
            this.nombre = nombre;
            this.horas = horas;
            this.porcentaje = porcentaje;
        }

        public String getNombre() {
            return nombre;
        }

        public double getHoras() {
            return horas;
        }

        public int getPorcentaje() {
            return porcentaje;
        }
    }

    private final double totalHoras;
    private final List<Materia> materias = new ArrayList<>();

    public ResumenEstudio(List<RegistroDeHorasDAO.TotalMateria> totales, int maximoMaterias) {
        List<RegistroDeHorasDAO.TotalMateria> conHoras = new ArrayList<>();
        double suma = 0;
        for (RegistroDeHorasDAO.TotalMateria total : totales) {
            if (total.getTotalHoras() > 0) {
                conHoras.add(total);
                suma += total.getTotalHoras();
            }
        }
        conHoras.sort((a, b) -> Double.compare(b.getTotalHoras(), a.getTotalHoras()));
        this.totalHoras = suma;

        if (conHoras.isEmpty()) {
            return;
        }
        double maximo = conHoras.get(0).getTotalHoras();
        for (int i = 0; i < conHoras.size() && i < maximoMaterias; i++) {
            RegistroDeHorasDAO.TotalMateria total = conHoras.get(i);
            int porcentaje = (int) Math.round(total.getTotalHoras() * 100 / maximo);
            materias.add(new Materia(total.getCurso(), total.getTotalHoras(), porcentaje));
        }
    }

    public double getTotalHoras() {
        return totalHoras;
    }

    public List<Materia> getMaterias() {
        return materias;
    }

    public Materia getMasEstudiada() {
        return materias.isEmpty() ? null : materias.get(0);
    }
}