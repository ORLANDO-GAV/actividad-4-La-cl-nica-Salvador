package com.cipa.clinica.repositorio;

import com.cipa.clinica.dominio.Cita;
import java.util.List;

/** Abstracción de almacenamiento de citas. */
public interface CitaRepositorio {

    String siguienteId();

    void guardar(Cita cita);

    List<Cita> buscarPorMedico(String medicoId);

    List<Cita> buscarPorPaciente(String pacienteId);
}
