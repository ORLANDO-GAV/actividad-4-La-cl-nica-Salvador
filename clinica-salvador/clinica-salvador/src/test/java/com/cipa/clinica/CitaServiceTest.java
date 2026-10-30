package com.cipa.clinica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cipa.clinica.dominio.Cita;
import com.cipa.clinica.dominio.FranjaHoraria;
import com.cipa.clinica.dominio.Medico;
import com.cipa.clinica.dominio.Paciente;
import com.cipa.clinica.excepcion.DatoInvalidoException;
import com.cipa.clinica.excepcion.EntidadNoEncontradaException;
import com.cipa.clinica.excepcion.HorarioOcupadoException;
import com.cipa.clinica.excepcion.MedicoNoDisponibleException;
import com.cipa.clinica.repositorio.CitaRepositorioMemoria;
import com.cipa.clinica.repositorio.MedicoRepositorioMemoria;
import com.cipa.clinica.repositorio.PacienteRepositorioMemoria;
import com.cipa.clinica.servicio.CitaService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas de la historia HU-01 (registrar cita) y HU-02 (horarios libres). */
class CitaServiceTest {

    /** Lunes fijo en el futuro para que las pruebas no dependan de la fecha actual. */
    private static final LocalDate LUNES = LocalDate.of(2030, 1, 7);

    private PacienteRepositorioMemoria pacientes;
    private CitaRepositorioMemoria citas;
    private CitaService servicio;

    @BeforeEach
    void preparar() {
        pacientes = new PacienteRepositorioMemoria();
        MedicoRepositorioMemoria medicos = new MedicoRepositorioMemoria();
        citas = new CitaRepositorioMemoria();
        servicio = new CitaService(pacientes, medicos, citas);

        pacientes.guardar(new Paciente("P-1", "Ana Pérez", "111", "300"));
        pacientes.guardar(new Paciente("P-2", "Luis Gómez", "222", "301"));

        Map<DayOfWeek, List<FranjaHoraria>> agenda = new EnumMap<>(DayOfWeek.class);
        agenda.put(DayOfWeek.MONDAY, List.of(
                new FranjaHoraria(LocalTime.of(8, 0), LocalTime.of(12, 0)),
                new FranjaHoraria(LocalTime.of(14, 0), LocalTime.of(17, 0))));
        medicos.guardar(new Medico("M-1", "Dr. Pérez", "General", agenda));
        medicos.guardar(new Medico("M-2", "Dra. Ruiz", "Pediatría", agenda));
    }

    @Test
    @DisplayName("registra una cita válida y la asigna al paciente y médico")
    void registraCitaValida() {
        Cita cita = servicio.registrarCita("P-1", "M-1", LUNES.atTime(9, 0));
        assertEquals("C-0001", cita.id());
        assertEquals("Ana Pérez", cita.paciente().nombre());
        assertEquals(LUNES.atTime(9, 30), cita.fin());
        assertEquals(1, citas.buscarPorMedico("M-1").size());
    }

    @Test
    @DisplayName("rechaza un médico fuera de su horario (almuerzo)")
    void medicoEnAlmuerzo() {
        assertThrows(MedicoNoDisponibleException.class,
                () -> servicio.registrarCita("P-1", "M-1", LUNES.atTime(13, 0)));
    }

    @Test
    @DisplayName("rechaza un día en que el médico no atiende")
    void medicoNoAtiendeEseDia() {
        assertThrows(MedicoNoDisponibleException.class,
                () -> servicio.registrarCita("P-1", "M-1", LUNES.plusDays(1).atTime(9, 0)));
    }

    @Test
    @DisplayName("rechaza una cita que terminaría después de la franja (11:45)")
    void citaQueExcedeLaFranja() {
        assertThrows(MedicoNoDisponibleException.class,
                () -> servicio.registrarCita("P-1", "M-1", LUNES.atTime(11, 45)));
    }

    @Test
    @DisplayName("acepta una cita que termina justo al cierre de la franja (11:30)")
    void citaQueTerminaAlCierre() {
        assertEquals(LUNES.atTime(12, 0), servicio.registrarCita("P-1", "M-1", LUNES.atTime(11, 30)).fin());
    }

    @Test
    @DisplayName("rechaza el mismo horario con el mismo médico")
    void mismoHorarioMismoMedico() {
        servicio.registrarCita("P-1", "M-1", LUNES.atTime(9, 0));
        assertThrows(HorarioOcupadoException.class,
                () -> servicio.registrarCita("P-2", "M-1", LUNES.atTime(9, 0)));
    }

    @Test
    @DisplayName("rechaza un horario que se solapa parcialmente (9:15)")
    void horarioSolapado() {
        servicio.registrarCita("P-1", "M-1", LUNES.atTime(9, 0));
        assertThrows(HorarioOcupadoException.class,
                () -> servicio.registrarCita("P-2", "M-1", LUNES.atTime(9, 15)));
    }

    @Test
    @DisplayName("permite citas consecutivas sin cruce (9:00 y 9:30)")
    void citasConsecutivas() {
        servicio.registrarCita("P-1", "M-1", LUNES.atTime(9, 0));
        assertEquals(LUNES.atTime(9, 30), servicio.registrarCita("P-2", "M-1", LUNES.atTime(9, 30)).inicio());
    }

    @Test
    @DisplayName("el paciente no puede tener dos citas simultáneas con médicos distintos")
    void pacienteConCitaSimultanea() {
        servicio.registrarCita("P-1", "M-1", LUNES.atTime(10, 0));
        assertThrows(HorarioOcupadoException.class,
                () -> servicio.registrarCita("P-1", "M-2", LUNES.atTime(10, 0)));
    }

    @Test
    @DisplayName("paciente inexistente")
    void pacienteInexistente() {
        assertThrows(EntidadNoEncontradaException.class,
                () -> servicio.registrarCita("P-99", "M-1", LUNES.atTime(9, 0)));
    }

    @Test
    @DisplayName("médico inexistente")
    void medicoInexistente() {
        assertThrows(EntidadNoEncontradaException.class,
                () -> servicio.registrarCita("P-1", "M-99", LUNES.atTime(9, 0)));
    }

    @Test
    @DisplayName("fecha nula o id vacío son datos inválidos")
    void datosInvalidos() {
        assertThrows(DatoInvalidoException.class, () -> servicio.registrarCita("P-1", "M-1", null));
        assertThrows(DatoInvalidoException.class, () -> servicio.registrarCita(" ", "M-1", LUNES.atTime(9, 0)));
    }

    @Test
    @DisplayName("HU-02: lista 14 horarios libres y excluye el ocupado")
    void horariosLibres() {
        assertEquals(14, servicio.horariosLibres("M-1", LUNES).size());
        servicio.registrarCita("P-1", "M-1", LUNES.atTime(9, 0));
        List<LocalDateTime> libres = servicio.horariosLibres("M-1", LUNES);
        assertEquals(13, libres.size());
        assertFalse(libres.contains(LUNES.atTime(9, 0)));
        assertTrue(libres.contains(LUNES.atTime(9, 30)));
    }

    @Test
    @DisplayName("HU-02: un día sin atención no tiene horarios libres")
    void diaSinAtencion() {
        assertTrue(servicio.horariosLibres("M-1", LUNES.plusDays(1)).isEmpty());
    }

    @Test
    @DisplayName("HU-03: busca paciente por documento")
    void buscarPacientePorDocumento() {
        assertEquals("Luis Gómez", pacientes.buscarPorDocumento("222").orElseThrow().nombre());
        assertTrue(pacientes.buscarPorDocumento("000").isEmpty());
    }

    @Test
    @DisplayName("una franja con fin anterior al inicio es inválida")
    void franjaInvalida() {
        assertThrows(DatoInvalidoException.class,
                () -> new FranjaHoraria(LocalTime.of(12, 0), LocalTime.of(8, 0)));
    }
}
