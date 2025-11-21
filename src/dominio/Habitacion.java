package dominio;

import validaciones.InputException;
import validaciones.BusinessRuleException;

public class Habitacion {

    public enum TipoHabitacion {
        INDIVIDUAL,
        MATRIMONIAL,
        COMPARTIDA
    }

    public enum EstadoHabitacion {
        ACTIVA,
        BAJA
    }

    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private TipoHabitacion tipo;
    private int capacidad;
    private EstadoHabitacion estado;

    // ============================================================
    // Constructor creación
    // ============================================================
    public Habitacion(String nombre, String descripcion, double precio,
                      TipoHabitacion tipo, int capacidad) {

        if (nombre == null || nombre.isBlank())
            throw new InputException("El nombre no puede estar vacío.");

        if (precio <= 0)
            throw new InputException("El precio debe ser mayor que cero.");

        if (tipo == null)
            throw new InputException("El tipo de habitación es obligatorio.");

        if (capacidad <= 0)
            throw new InputException("La capacidad debe ser mayor que cero.");

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipo = tipo;
        this.capacidad = capacidad;

        this.estado = EstadoHabitacion.ACTIVA;
    }

    // ============================================================
    // Constructor reconstrucción BD
    // ============================================================
    public Habitacion(int id, String nombre, String descripcion, double precio,
                      TipoHabitacion tipo, int capacidad, EstadoHabitacion estado) {

        this(nombre, descripcion, precio, tipo, capacidad);
        this.id = id;
        this.estado = estado;
    }

    // ============================================================
    // Getters
    // ============================================================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } // solo Storage

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public TipoHabitacion getTipo() { return tipo; }
    public int getCapacidad() { return capacidad; }
    public EstadoHabitacion getEstado() { return estado; }

    public boolean estaActiva() {
        return estado == EstadoHabitacion.ACTIVA;
    }

    public boolean estaDadaDeBaja() {
        return estado == EstadoHabitacion.BAJA;
    }

    // ============================================================
    // Reglas de negocio: cambiar estado
    // ============================================================
    public void darDeBaja() {
        if (estaDadaDeBaja())
            throw new BusinessRuleException("La habitación ya está dada de baja.");

        this.estado = EstadoHabitacion.BAJA;
    }

    public void activar() {
        if (estaActiva())
            throw new BusinessRuleException("La habitación ya está activa.");

        this.estado = EstadoHabitacion.ACTIVA;
    }

    @Override
    public String toString() {
        return String.format(
                "Habitacion[id=%d, tipo=%s, nombre=%s, capacidad=%d, precio=%.2f, estado=%s]",
                id, tipo, nombre, capacidad, precio, estado
        );
    }
}