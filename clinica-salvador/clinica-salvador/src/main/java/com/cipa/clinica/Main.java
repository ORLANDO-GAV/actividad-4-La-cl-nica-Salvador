package com.cipa.clinica;

import com.cipa.clinica.dominio.Cita;
import com.cipa.clinica.dominio.FranjaHoraria;
import com.cipa.clinica.dominio.Medico;
import com.cipa.clinica.dominio.Paciente;
import com.cipa.clinica.excepcion.ClinicaException;
import com.cipa.clinica.repositorio.CitaRepositorioMemoria;
import com.cipa.clinica.repositorio.MedicoRepositorioMemoria;
import com.cipa.clinica.repositorio.PacienteRepositorioMemoria;
import com.cipa.clinica.servicio.CitaService;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Demostración del incremento del Sprint 1: el recepcionista registra citas. */
@SuppressWarnings("unused")
public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        PacienteRepositorioMemoria pacientes = new PacienteRepositorioMemoria();
        MedicoRepositorioMemoria medicos = new MedicoRepositorioMemoria();
        CitaService servicio = new CitaService(pacientes, medicos, new CitaRepositorioMemoria());
        cargarDatosDemo(pacientes, medicos);

        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        System.out.println("=== Clínica Salvador - Registro de citas (Sprint 1 + mantenimiento v1.1) ===");

        String pacienteAna = pacientes.buscarPorDocumento("1045000111").orElseThrow().id();
        System.out.println("Horarios libres del Dr. Pérez el " + lunes + " (primeros 4): "
                + servicio.horariosLibres("M-1", lunes).stream().limit(4).map(h -> h.toLocalTime()).toList());

        intentar("1) Cita válida", () -> servicio.registrarCita(pacienteAna, "M-1", lunes.atTime(9, 0)));
        intentar("2) Mismo horario, otro paciente", () -> servicio.registrarCita("P-2", "M-1", lunes.atTime(9, 0)));
        intentar("3) Cita consecutiva (9:30)", () -> servicio.registrarCita("P-2", "M-1", lunes.atTime(9, 30)));
        intentar("4) Médico en hora de almuerzo", () -> servicio.registrarCita("P-2", "M-1", lunes.atTime(13, 0)));
        intentar("5) Médico no atiende domingo", () -> servicio.registrarCita("P-2", "M-1",
                lunes.minusDays(1).atTime(9, 0)));
        intentar("6) Paciente inexistente", () -> servicio.registrarCita("P-99", "M-1", lunes.atTime(10, 0)));
        intentar("7) Cita en el pasado (v1.1)", () -> servicio.registrarCita("P-2", "M-1",
                LocalDate.now().minusDays(7).with(DayOfWeek.MONDAY).atTime(9, 0)));
    }

    private static void intentar(String titulo, java.util.function.Supplier<Cita> accion) {
        try {
            Cita c = accion.get();
            System.out.printf("[OK]    %-34s -> %s | %s con %s | %s%n", titulo, c.id(),
                    c.paciente().nombre(), c.medico().nombre(), c.inicio());
        } catch (ClinicaException e) {
            System.out.printf("[ERROR] %-34s -> %s%n", titulo, e.getMessage());
        }
    }

    private static void cargarDatosDemo(PacienteRepositorioMemoria pacientes, MedicoRepositorioMemoria medicos) {
        pacientes.guardar(new Paciente("P-1", "Ana Pérez", "1045000111", "3001112233"));
        pacientes.guardar(new Paciente("P-2", "Luis Gómez", "1045000222", "3004445566"));

        Map<DayOfWeek, List<FranjaHoraria>> agenda = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
            agenda.put(d, List.of(new FranjaHoraria(LocalTime.of(8, 0), LocalTime.of(12, 0)),
                    new FranjaHoraria(LocalTime.of(14, 0), LocalTime.of(17, 0))));
        }
        medicos.guardar(new Medico("M-1", "Dr. Carlos Pérez", "Medicina general", agenda));
    }
}
