package com.cipa.clinica.repositorio;

import com.cipa.clinica.dominio.Paciente;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
public class PacienteRepositorioMemoria implements PacienteRepositorio {

    private final Map<String, Paciente> pacientes = new LinkedHashMap<>();

    @Override
    public void guardar(Paciente paciente) {
        pacientes.put(paciente.id(), paciente);
    }

    @Override
    public Optional<Paciente> buscarPorId(String id) {
        return Optional.ofNullable(pacientes.get(id));
    }

    @Override
    public Optional<Paciente> buscarPorDocumento(String documento) {
        return pacientes.values().stream().filter(p -> p.documento().equals(documento)).findFirst();
    }
}
