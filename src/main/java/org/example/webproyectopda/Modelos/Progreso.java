package org.example.webproyectopda.Modelos;

import java.util.List;

public class Progreso {

    private final int total;
    private final int completadas;

    public Progreso(List<Tarea> tareas) {
        int hechas = 0;
        for (Tarea tarea : tareas) {
            if (tarea.isCompletada()) {
                hechas++;
            }
        }
        this.total = tareas.size();
        this.completadas = hechas;
    }

    public int getTotal() {
        return total;
    }

    public int getCompletadas() {
        return completadas;
    }

    public int getPendientes() {
        return total - completadas;
    }

    public int getPorcentaje() {
        if (total == 0) {
            return 0;
        }
        return completadas * 100 / total;
    }
}