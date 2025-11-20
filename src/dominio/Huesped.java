package dominio;

import validaciones.InputException;
import validaciones.BusinessRuleException;

public class Huesped {

    public enum EstadoHuesped {
        ACTIVO,
        BAJA
    }

    private int id;
    private final String dni; // NO cambia nunca
    private String nombre;
    private String apellido;
    private String email; // null permitido
    private EstadoHuesped estado;

    // ============================================================
    // Constructor creación
    // ============================================================
    public Huesped(String dni, String nombre, String apellido, String email) {

        validarCadena(dni, "DNI");
        validarCadena(nombre, "Nombre");
        validarCadena(apellido, "Apellido");

        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.estado = EstadoHuesped.ACTIVO;
    }

    // ============================================================
    // Constructor reconstrucción
    // ============================================================
    public Huesped(int id, String dni, String nombre, String apellido,
                   String email, EstadoHuesped estado) {

        this(dni, nombre, apellido, email);
        this.id = id;
        this.estado = estado;
    }

    // ============================================================
    // Getters
    // ============================================================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } // Solo Storage

    public String getDni() { return dni; }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public EstadoHuesped getEstado() { return estado; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean estaActivo() { return estado == EstadoHuesped.ACTIVO; }
    public boolean estaDadoDeBaja() { return estado == EstadoHuesped.BAJA; }

    // ============================================================
    // Modificaciones
    // ============================================================
    public void modificarDatos(String nombre, String apellido, String email) {
        if (!estaActivo())
            throw new BusinessRuleException("No se puede modificar un huésped dado de baja.");

        validarCadena(nombre, "Nombre");
        validarCadena(apellido, "Apellido");

        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email; // null permitido
    }

    // ============================================================
    // Cambiar estado
    // ============================================================
    public void darDeBaja() {
        if (estaDadoDeBaja())
            throw new BusinessRuleException("El huésped ya está dado de baja.");
        this.estado = EstadoHuesped.BAJA;
    }

    public void activar() {
        if (estaActivo())
            throw new BusinessRuleException("El huésped ya está activo.");
        this.estado = EstadoHuesped.ACTIVO;
    }

    // ============================================================
    // Validaciones internas
    // ============================================================
    private static void validarCadena(String valor, String campo) {
        if (valor == null || valor.isBlank())
            throw new InputException(campo + " no puede ser vacío.");
    }

    @Override
    public String toString() {
        return String.format("Huesped[id=%d, dni=%s, nombre=%s %s, estado=%s]",
                id, dni, nombre, apellido, estado);
    }
}