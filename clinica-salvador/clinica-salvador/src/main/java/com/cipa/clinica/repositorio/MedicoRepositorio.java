package com.cipa.clinica.repositorio;

import com.cipa.clinica.dominio.Medico;
import java.util.List;
import java.util.Optional;

/** Abstracción de almacenamiento de médicos. */
public interface MedicoRepositorio {

    void guardar(Medico medico);

    Optional<Medico> buscarPorId(String id);

    List<Medico> listar();
}
