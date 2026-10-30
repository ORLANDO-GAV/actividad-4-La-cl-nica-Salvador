package com.cipa.clinica.dominio;

import com.cipa.clinica.util.Validaciones;

/** Persona que solicita atención médica. */
public record Paciente(String id, String nombre, String documento, String telefono) {

    public Paciente {
        id = Validaciones.texto(id, "El id del paciente");
        nombre = Validaciones.texto(nombre, "El nombre del paciente");
        documento = Validaciones.texto(documento, "El documento del paciente");
        telefono = Validaciones.texto(telefono, "El teléfono del paciente");
    }
}
