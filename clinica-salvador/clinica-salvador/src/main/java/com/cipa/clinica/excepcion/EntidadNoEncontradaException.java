package com.cipa.clinica.excepcion;

/** No existe el paciente o el médico solicitado. */
public class EntidadNoEncontradaException extends ClinicaException {
    public EntidadNoEncontradaException(String entidad, String id) {
        super(entidad + " no encontrado: " + id);
    }
}
