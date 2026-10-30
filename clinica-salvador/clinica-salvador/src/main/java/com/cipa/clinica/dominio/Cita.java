    package com.cipa.clinica.dominio;

    import java.time.Duration;
    import java.time.LocalDateTime;

    /** Cita médica confirmada. */
    public record Cita(String id, Paciente paciente, Medico medico,
                    LocalDateTime inicio, Duration duracion) {

        public LocalDateTime fin() {
            return inicio.plus(duracion);
        }

        /** Dos intervalos se solapan si cada uno empieza antes de que termine el otro. */
        public boolean seSolapaCon(LocalDateTime otroInicio, LocalDateTime otroFin) {
            return inicio.isBefore(otroFin) && otroInicio.isBefore(fin());
        }
    }
