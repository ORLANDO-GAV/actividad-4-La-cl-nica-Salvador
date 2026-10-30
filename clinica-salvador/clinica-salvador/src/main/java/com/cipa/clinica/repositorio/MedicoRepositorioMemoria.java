package com.cipa.clinica.repositorio;

import com.cipa.clinica.dominio.Medico;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Implementación en memoria de médicos. */
public class MedicoRepositorioMemoria implements MedicoRepositorio {

    private final Map<String, Medico> medicos = new LinkedHashMap<>();

    @Override
    public void guardar(Medico medico) {
        medicos.put(medico.id(), medico);
    }

    @Override
    public Optional<Medico> buscarPorId(String id) {
        return Optional.ofNullable(medicos.get(id));
    }

    @Override
    public List<Medico> listar() {
        return List.copyOf(medicos.values());
    }
}
