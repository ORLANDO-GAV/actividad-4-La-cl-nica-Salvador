package com.cipa.clinica.repositorio;

import com.cipa.clinica.dominio.Paciente;
import java.util.Optional;

/** Abstracción de almacenamiento de pacientes (DIP: el servicio no conoce la base de datos). */
public interface PacienteRepositorio {

    void guardar(Paciente paciente);

    Optional<Paciente> buscarPorId(String id);

    Optional<Paciente> buscarPorDocumento(String documento);
}
