package com.cipa.clinica.dominio;

import com.cipa.clinica.util.Validaciones;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Médico con su agenda semanal de atención. */
public record Medico(String id, String nombre, String especialidad,
                     Map<DayOfWeek, List<FranjaHoraria>> agenda) {

    public Medico {
        id = Validaciones.texto(id, "El id del médico");
        nombre = Validaciones.texto(nombre, "El nombre del médico");
        especialidad = Validaciones.texto(especialidad, "La especialidad");
        agenda = Map.copyOf(Validaciones.obligatorio(agenda, "La agenda"));
    }

    public List<FranjaHoraria> franjasDelDia(DayOfWeek dia) {
        return agenda.getOrDefault(dia, List.of());
    }

    /** True si la cita completa (inicio + duración) cae dentro de una franja de atención. */
    public boolean atiende(LocalDateTime inicio, Duration duracion) {
        LocalDateTime fin = inicio.plus(duracion);
        if (!fin.toLocalDate().equals(inicio.toLocalDate())) {
            return false;
        }
        return franjasDelDia(inicio.getDayOfWeek()).stream()
                .anyMatch(f -> f.contiene(inicio.toLocalTime(), fin.toLocalTime()));
    }
}
