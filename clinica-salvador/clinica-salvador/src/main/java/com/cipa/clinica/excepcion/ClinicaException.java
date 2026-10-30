package com.cipa.clinica.excepcion;

/** Error base de la aplicación de la clínica. */
public class ClinicaException extends RuntimeException {
    public ClinicaException(String mensaje) {
        super(mensaje);
    }
}
