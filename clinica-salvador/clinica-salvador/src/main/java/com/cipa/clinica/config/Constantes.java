package com.cipa.clinica.config;

import java.time.Duration;

/** Reglas de negocio parametrizables. */
public final class Constantes {

    private Constantes() { }

    /** Duración estándar de una cita médica. */
    public static final Duration DURACION_CITA = Duration.ofMinutes(30);
}
