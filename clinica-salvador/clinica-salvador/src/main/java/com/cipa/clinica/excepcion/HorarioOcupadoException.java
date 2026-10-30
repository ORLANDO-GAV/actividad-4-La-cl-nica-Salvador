package com.cipa.clinica.excepcion;

/** El médico o el paciente ya tienen una cita que se cruza con el horario solicitado. */
public class HorarioOcupadoException extends ClinicaException {
    public HorarioOcupadoException(String mensaje) {
        super(mensaje);
    }
}
