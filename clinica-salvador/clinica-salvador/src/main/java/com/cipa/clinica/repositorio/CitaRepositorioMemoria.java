package com.cipa.clinica.repositorio;

import com.cipa.clinica.dominio.Cita;
import java.util.ArrayList;
import java.util.List;

/** Implementación en memoria de citas. */
public class CitaRepositorioMemoria implements CitaRepositorio {

    private final List<Cita> citas = new ArrayList<>();
    private int contador = 0;

    @Override
    public String siguienteId() {
        contador++;
        return String.format("C-%04d", contador);
    }

    @Override
    public void guardar(Cita cita) {
        citas.add(cita);
    }

    @Override
    public List<Cita> buscarPorMedico(String medicoId) {
        return citas.stream().filter(c -> c.medico().id().equals(medicoId)).toList();
    }

    @Override
    public List<Cita> buscarPorPaciente(String pacienteId) {
        return citas.stream().filter(c -> c.paciente().id().equals(pacienteId)).toList();
    }
}
