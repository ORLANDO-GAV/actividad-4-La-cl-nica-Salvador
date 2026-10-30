package com.cipa.clinica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cipa.clinica.dominio.FranjaHoraria;
import com.cipa.clinica.dominio.Medico;
import com.cipa.clinica.dominio.Paciente;
import com.cipa.clinica.excepcion.FechaPasadaException;
import com.cipa.clinica.repositorio.CitaRepositorioMemoria;
import com.cipa.clinica.repositorio.MedicoRepositorioMemoria;
import com.cipa.clinica.repositorio.PacienteRepositorioMemoria;
import com.cipa.clinica.servicio.CitaService;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** CR-01 (mantenimiento v1.1): una cita no puede registrarse en una fecha/hora pasada. */
class CitaPasadaTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final LocalDate LUNES = LocalDate.of(2030, 1, 7);

    private CitaService servicio;

    /** Reloj fijo: "ahora" es el lunes 2030-01-07 a las 10:00. */
    private static Clock relojA(LocalDateTime ahora) {
        return Clock.fixed(ahora.atZone(ZONA).toInstant(), ZONA);
    }

    @BeforeEach
    void preparar() {
        PacienteRepositorioMemoria pacientes = new PacienteRepositorioMemoria();
        MedicoRepositorioMemoria medicos = new MedicoRepositorioMemoria();
        pacientes.guardar(new Paciente("P-1", "Ana Pérez", "111", "300"));
        medicos.guardar(new Medico("M-1", "Dr. Pérez", "General", Map.of(DayOfWeek.MONDAY,
                List.of(new FranjaHoraria(LocalTime.of(8, 0), LocalTime.of(17, 0))))));
        servicio = new CitaService(pacientes, medicos, new CitaRepositorioMemoria(),
                relojA(LUNES.atTime(10, 0)));
    }

    @Test
    @DisplayName("rechaza una cita anterior a la hora actual")
    void rechazaPasado() {
        assertThrows(FechaPasadaException.class,
                () -> servicio.registrarCita("P-1", "M-1", LUNES.atTime(9, 0)));
    }

    @Test
    @DisplayName("rechaza una cita de un día anterior")
    void rechazaDiaAnterior() {
        assertThrows(FechaPasadaException.class,
                () -> servicio.registrarCita("P-1", "M-1", LUNES.minusWeeks(1).atTime(9, 0)));
    }

    @Test
    @DisplayName("acepta una cita exactamente a la hora actual")
    void aceptaAhoraMismo() {
        assertEquals(LUNES.atTime(10, 0), servicio.registrarCita("P-1", "M-1", LUNES.atTime(10, 0)).inicio());
    }

    @Test
    @DisplayName("acepta una cita futura")
    void aceptaFuturo() {
        assertEquals(LUNES.atTime(15, 0), servicio.registrarCita("P-1", "M-1", LUNES.atTime(15, 0)).inicio());
    }
}
