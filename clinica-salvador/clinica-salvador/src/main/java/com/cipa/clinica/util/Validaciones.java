package com.cipa.clinica.util;

import com.cipa.clinica.excepcion.DatoInvalidoException;

/** Validaciones reutilizables. */
public final class Validaciones {

    private Validaciones() { }

    public static String texto(String valor, String nombre) {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException(nombre + " no puede estar vacío.");
        }
        return valor.trim();
    }

    public static <T> T obligatorio(T valor, String nombre) {
        if (valor == null) {
            throw new DatoInvalidoException(nombre + " es obligatorio.");
        }
        return valor;
    }
}
