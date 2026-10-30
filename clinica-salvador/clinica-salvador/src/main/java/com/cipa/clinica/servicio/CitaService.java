package com.cipa.clinica.servicio;

import static com.cipa.clinica.config.Constantes.DURACION_CITA;

import com.cipa.clinica.dominio.Cita;
import com.cipa.clinica.dominio.FranjaHoraria;
import com.cipa.clinica.dominio.Medico;
import com.cipa.clinica.dominio.Paciente;
import com.cipa.clinica.excepcion.EntidadNoEncontradaException;
import com.cipa.clinica.excepcion.FechaPasadaException;
import com.cipa.clinica.excepcion.HorarioOcupadoException;
import com.cipa.clinica.excepcion.MedicoNoDisponibleException;
import com.cipa.clinica.repositorio.CitaRepositorio;
import com.cipa.clinica.repositorio.MedicoRepositorio;
import com.cipa.clinica.repositorio.PacienteRepositorio;
import com.cipa.clinica.util.Validaciones;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso del sprint: el recepcionista registra una cita para un paciente
 * con un médico disponible en un horario específico.
 * Depende de interfaces de repositorio, no de implementaciones (Dependency Inversion).
 */
public class CitaService {

    private final PacienteRepositorio pacientes;
    private final MedicoRepositorio medicos;
    private final CitaRepositorio citas;
    private final Clock reloj;

    public CitaService(PacienteRepositorio pacientes, MedicoRepositorio medicos, CitaRepositorio citas) {
        this(pacientes, medicos, citas, Clock.systemDefaultZone());
    }

    /** v1.1 (preventivo): el reloj se inyecta para poder probar reglas de tiempo sin depender de "hoy". */
    public CitaService(PacienteRepositorio pacientes, MedicoRepositorio medicos, CitaRepositorio citas,
                       Clock reloj) {
        this.pacientes = pacientes;
        this.medicos = medicos;
        this.citas = citas;
        this.reloj = reloj;
    }

    /** HU-01: registra una cita validando existencia, disponibilidad y cruces de agenda. */
    public Cita registrarCita(String pacienteId, String medicoId, LocalDateTime inicio) {
        Validaciones.obligatorio(inicio, "La fecha y hora de la cita");
        // v1.1 (correctivo, CR-01): no se permiten citas en el pasado.
        if (inicio.isBefore(LocalDateTime.now(reloj))) {
            throw new FechaPasadaException("No se puede registrar una cita en el pasado: " + inicio + ".");
        }
        Paciente paciente = pacientes.buscarPorId(Validaciones.texto(pacienteId, "El id del paciente"))
                .orElseThrow(() -> new EntidadNoEncontradaException("Paciente", pacienteId));
        Medico medico = medicos.buscarPorId(Validaciones.texto(medicoId, "El id del médico"))
                .orElseThrow(() -> new EntidadNoEncontradaException("Médico", medicoId));

        if (!medico.atiende(inicio, DURACION_CITA)) {
            throw new MedicoNoDisponibleException(
                    medico.nombre() + " no atiende el " + inicio.toLocalDate() + " a las " + inicio.toLocalTime() + ".");
        }

        LocalDateTime fin = inicio.plus(DURACION_CITA);
        if (citas.buscarPorMedico(medico.id()).stream().anyMatch(c -> c.seSolapaCon(inicio, fin))) {
            throw new HorarioOcupadoException(medico.nombre() + " ya tiene una cita que se cruza con ese horario.");
        }
        if (citas.buscarPorPaciente(paciente.id()).stream().anyMatch(c -> c.seSolapaCon(inicio, fin))) {
            throw new HorarioOcupadoException(paciente.nombre() + " ya tiene otra cita en ese horario.");
        }

        Cita cita = new Cita(citas.siguienteId(), paciente, medico, inicio, DURACION_CITA);
        citas.guardar(cita);
        return cita;
    }

    /** HU-02: horarios de inicio libres del médico en una fecha (cada DURACION_CITA). */
    public List<LocalDateTime> horariosLibres(String medicoId, LocalDate fecha) {
        Medico medico = medicos.buscarPorId(medicoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Médico", medicoId));
        List<Cita> ocupadas = citas.buscarPorMedico(medico.id());

        List<LocalDateTime> libres = new ArrayList<>();
        for (FranjaHoraria franja : medico.franjasDelDia(fecha.getDayOfWeek())) {
            LocalDateTime cursor = fecha.atTime(franja.inicio());
            LocalDateTime limite = fecha.atTime(franja.fin());
            while (!cursor.plus(DURACION_CITA).isAfter(limite)) {
                LocalDateTime desde = cursor;
                LocalDateTime hasta = cursor.plus(DURACION_CITA);
                if (ocupadas.stream().noneMatch(c -> c.seSolapaCon(desde, hasta))) {
                    libres.add(desde);
                }
                cursor = hasta;
            }
        }
        return libres;
    }
}
