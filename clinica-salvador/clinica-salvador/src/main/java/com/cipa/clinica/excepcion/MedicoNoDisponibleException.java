package com.cipa.clinica.excepcion;

/** El médico no atiende en el día u hora solicitados. */
public class MedicoNoDisponibleException extends ClinicaException {
    public MedicoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
