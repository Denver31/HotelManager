package validaciones;

import java.time.LocalDate;

/**
 * Clase utilitaria para validaciones generales de campos y fechas.
 * No contiene lógica de negocio ni referencias a entidades del dominio.
 */
public class Validator {

    private Validator() {} // Evita instanciación

    /** Valida que un texto no sea nulo ni vacío */
    public static void textoNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo '" + campo + "' no puede estar vacío.");
        }
    }

    /** Valida que un número sea positivo */
    public static void numeroPositivo(double valor, String campo) {
        if (valor <= 0) {
            throw new ValidacionException("El campo '" + campo + "' debe ser mayor a 0.");
        }
    }

    /** Valida que las fechas sean correctas y coherentes */
    public static void rangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ValidacionException("Las fechas no pueden ser nulas.");
        }
        if (hasta.isBefore(desde)) {
            throw new ValidacionException("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }
    }

    /** Valida formato básico de DNI (7 a 9 dígitos) */
    public static void dniValido(String dni) {
        if (dni == null || !dni.matches("\\d{7,9}")) {
            throw new ValidacionException("El DNI ingresado no es válido.");
        }
    }

    /** Valida formato de correo electrónico */
    public static void emailValido(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidacionException("El formato del correo electrónico no es válido.");
        }
    }
}
