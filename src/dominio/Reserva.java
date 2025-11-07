package dominio;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public class Reserva {

    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        ACTIVA,
        FINALIZADA,
        CANCELADA
    }

    private int id;
    private EstadoReserva estado;
    private Habitacion habitacion;
    private Huesped huesped;
    private Factura factura;
    private LocalDate desde;
    private LocalDate hasta;

    // Constructor principal (nueva reserva)
    public Reserva(Habitacion habitacion, Huesped huesped, Factura factura, LocalDate desde, LocalDate hasta) {

        if (habitacion == null)
            throw new IllegalArgumentException("La habitación no puede ser null.");
        if (huesped == null)
            throw new IllegalArgumentException("El huésped no puede ser null.");
        if (factura == null)
            throw new IllegalArgumentException("La factura no puede ser null.");
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Las fechas no pueden ser null.");
        if (hasta.isBefore(desde))
            throw new IllegalArgumentException("La fecha de salida no puede ser anterior a la de entrada.");

        this.habitacion = habitacion;
        this.huesped = huesped;
        this.factura = factura;
        this.desde = desde;
        this.hasta = hasta;
        this.estado = EstadoReserva.PENDIENTE;

        habitacion.agregarReserva(this);
    }

    // Constructor alternativo (para reconstrucción desde BD)
    public Reserva(int id, Habitacion habitacion, Huesped huesped, Factura factura,
                   LocalDate desde, LocalDate hasta, EstadoReserva estado) {
        this.id = id;
        this.habitacion = habitacion;
        this.huesped = huesped;
        this.factura = factura;
        this.desde = desde;
        this.hasta = hasta;
        this.estado = estado;
    }

    // === Getters y Setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public Habitacion getHabitacion() { return habitacion; }
    public Huesped getHuesped() { return huesped; }
    public Factura getFactura() { return factura; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }

    // === Métodos de negocio ===

    /** Verifica si se solapa con un rango de fechas dado */
    public boolean seSolapaCon(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Las fechas no pueden ser null.");
        return !(hasta.isBefore(this.desde) || desde.isAfter(this.hasta));
    }

    /** Marca la reserva como activa (check-in) */
    public void hacerCheckIn() {
        if (!factura.isPagada()) {
            throw new validaciones.PagoRequeridoException("Debe pagar la factura antes del check-in.");
        }
        if (estado != EstadoReserva.CONFIRMADA && estado != EstadoReserva.PENDIENTE)
            throw new IllegalStateException("No se puede hacer check-in en una reserva no confirmada.");
        this.estado = EstadoReserva.ACTIVA;
    }

    /** Marca la reserva como finalizada (check-out) */
    public void hacerCheckOut() {
        if (estado != EstadoReserva.ACTIVA)
            throw new IllegalStateException("Solo se puede hacer check-out de una reserva activa.");
        this.estado = EstadoReserva.FINALIZADA;
    }

    /** Cancela la reserva si aún no se realizó */
    public void cancelar() {
        if (estado == EstadoReserva.ACTIVA)
            throw new IllegalStateException("No se puede cancelar una reserva activa.");
        this.estado = EstadoReserva.CANCELADA;
    }

    /** Calcula el total estimado de la estadía */
    public double calcularTotal() {
        long dias = java.time.temporal.ChronoUnit.DAYS.between(desde, hasta);
        return habitacion.getPrecio() * dias;
    }

    @Override
    public String toString() {
        return String.format("Reserva[id=%d, estado=%s, habitacion=%s, huesped=%s, desde=%s, hasta=%s]",
                id, estado, habitacion.getNombre(), huesped.getNombreCompleto(), desde, hasta);
    }

    // ================= SETTERS (solo para reconstrucción interna) =================

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public void setHuesped(Huesped huesped) {
        this.huesped = huesped;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

}
