package com.cipa.clinica.excepcion;

/** Se intentó registrar una cita en una fecha/hora que ya pasó (CR-01, mantenimiento correctivo). */
public class FechaPasadaException extends ClinicaException {
    public FechaPasadaException(String mensaje) {
        super(mensaje);
    }
}
