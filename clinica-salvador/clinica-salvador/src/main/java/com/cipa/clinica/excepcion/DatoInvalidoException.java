package com.cipa.clinica.excepcion;

/** Un dato de entrada es nulo, vacío o incoherente. */
public class DatoInvalidoException extends ClinicaException {
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
