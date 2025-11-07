package dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Habitacion {

    public enum TipoHabitacion {
        INDIVIDUAL,
        MATRIMONIAL,
        COMPARTIDA
    }

    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private TipoHabitacion tipo;
    private int capacidad;
    private List<Reserva> reservas;

    public Habitacion(String nombre, String descripcion, double precio, TipoHabitacion tipo, int capacidad) {
        if (nombre == null || nombre.isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser vacío.");
        if (precio <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        if (tipo == null)
            throw new IllegalArgumentException("El tipo de habitación no puede ser null.");
        if (capacidad <= 0)
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.reservas = new ArrayList<>();
    }

    // Constructor alternativo (para reconstrucción desde BD)
    public Habitacion(int id, String nombre, String descripcion, double precio, TipoHabitacion tipo, int capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.reservas = new ArrayList<>();
    }

    // Constructor placeholder (solo para reconstrucción parcial)
    public Habitacion(int id) {
        this.id = id;
        this.nombre = "";
        this.descripcion = "";
        this.precio = 0;
        this.tipo = TipoHabitacion.INDIVIDUAL;
        this.capacidad = 1;
        this.reservas = new ArrayList<>();
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser vacío.");
        this.nombre = nombre;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) {
        if (precio <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        this.precio = precio;
    }

    public TipoHabitacion getTipo() { return tipo; }
    public void setTipo(TipoHabitacion tipo) { this.tipo = tipo; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) {
        if (capacidad <= 0)
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        this.capacidad = capacidad;
    }

    public List<Reserva> getReservas() { return reservas; }

    // === Métodos de negocio ===

    /** Verifica si la habitación está disponible entre dos fechas */
    public boolean estaDisponible(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Las fechas no pueden ser null.");
        for (Reserva r : reservas) {
            if (r.seSolapaCon(desde, hasta))
                return false;
        }
        return true;
    }

    /** Calcula el precio total para una estadía */
    public double calcularPrecio(LocalDate desde, LocalDate hasta) {
        long dias = java.time.temporal.ChronoUnit.DAYS.between(desde, hasta);
        return precio * dias;
    }

    /** Registra la reserva en la lista local */
    public void agregarReserva(Reserva reserva) {
        if (reserva == null) throw new IllegalArgumentException("Reserva no puede ser null.");
        reservas.add(reserva);
    }

    public void eliminarReserva(Reserva reserva) {
        reservas.remove(reserva);
    }

    @Override
    public String toString() {
        return String.format("Habitacion[id=%d, tipo=%s, nombre=%s, capacidad=%d, precio=%.2f]",
                id, tipo, nombre, capacidad, precio);
    }
}