package com.cipa.clinica.dominio;

import com.cipa.clinica.excepcion.DatoInvalidoException;
import java.time.LocalTime;
public record FranjaHoraria(LocalTime inicio, LocalTime fin) {

    public FranjaHoraria {
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new DatoInvalidoException("La franja horaria debe tener fin posterior al inicio.");
        }
    }
    public boolean contiene(LocalTime desde, LocalTime hasta) {
        return !desde.isBefore(inicio) && !hasta.isAfter(fin);
    }
}
