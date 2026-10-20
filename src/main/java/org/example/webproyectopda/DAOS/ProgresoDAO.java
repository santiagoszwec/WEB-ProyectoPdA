package org.example.webproyectopda.DAOS;

import java.util.List;

// Progreso público de un estudiante, calculado a partir de sus horas de estudio.
// Las consultas concretas viven en RegistroDeHorasDAO; acá se expone la noción de progreso.
public class ProgresoDAO {

    // Horas estudiadas agrupadas por materia.
    public static List<RegistroDeHorasDAO.TotalMateria> horasPorMateria(int usuarioId) {
        return RegistroDeHorasDAO.totalesPorMateria(usuarioId);
    }

    // Suma de las horas estudiadas en todas las materias.
    public static double totalHoras(int usuarioId) {
        double total = 0;
        for (RegistroDeHorasDAO.TotalMateria materia : horasPorMateria(usuarioId)) {
            total += materia.getTotalHoras();
        }
        return total;
    }
}
