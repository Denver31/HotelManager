package dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import validaciones.InputException;
import validaciones.BusinessRuleException;

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

    // ============================================================
    // Constructor para creación de Reserva (caso de uso "Crear reserva")
    // ============================================================
    public Reserva(Habitacion habitacion, Huesped huesped, Factura factura,
                   LocalDate desde, LocalDate hasta) {

        if (habitacion == null) throw new InputException("La habitación es obligatoria.");
        if (huesped == null)    throw new InputException("El huésped es obligatorio.");

        if (desde == null || hasta == null)
            throw new InputException("Las fechas son obligatorias.");

        if (hasta.isBefore(desde))
            throw new InputException("La fecha de salida no puede ser anterior a la fecha de entrada.");

        if (!habitacion.estaActiva())
            throw new BusinessRuleException("La habitación no está activa.");

        if (!huesped.estaActivo())
            throw new BusinessRuleException("El huésped no está activo.");

        this.habitacion = habitacion;
        this.huesped = huesped;
        this.factura = factura;
        this.desde = desde;
        this.hasta = hasta;
        this.estado = EstadoReserva.PENDIENTE;
    }

    // ============================================================
    // Constructor para reconstrucción desde la BD
    // ============================================================
    public Reserva(int id, Habitacion habitacion, Huesped huesped, Factura factura,
                   LocalDate desde, LocalDate hasta, EstadoReserva estado) {

        this(habitacion, huesped, factura, desde, hasta);
        this.id = id;
        this.estado = estado;
    }

    // ============================================================
    // Getters
    // ============================================================
    public int getId() { return id; }
    public EstadoReserva getEstado() { return estado; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public Habitacion getHabitacion() { return habitacion; }
    public Huesped getHuesped() { return huesped; }
    public Factura getFactura() { return factura; }

    // Setter solo para persistencia
    public void setId(int id) { this.id = id; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    // ============================================================
    // Reglas de disponibilidad
    // ============================================================
    public boolean seSolapaCon(LocalDate d, LocalDate h) {
        if (d == null || h == null)
            throw new InputException("Fechas inválidas para solapamiento.");

        return !(h.isBefore(desde) || d.isAfter(hasta));
    }

    public boolean estaOperativa() {
        return estado == EstadoReserva.PENDIENTE ||
                estado == EstadoReserva.CONFIRMADA ||
                estado == EstadoReserva.ACTIVA;
    }

    // ============================================================
    // Reglas de negocio: transiciones de estado
    // ============================================================

    public void hacerCheckIn() {
        if (!estaConfirmada())
            throw new BusinessRuleException("Solo se puede hacer check-in sobre reservas confirmadas.");
        this.estado = EstadoReserva.ACTIVA;
    }

    public void hacerCheckOut() {
        if (!estaActiva())
            throw new BusinessRuleException("Solo reservas activas pueden finalizarse.");
        this.estado = EstadoReserva.FINALIZADA;
    }

    public void cancelar() {
        if (!estaPendiente() && !estaConfirmada())
            throw new BusinessRuleException("Solo reservas pendientes o confirmadas pueden cancelarse.");
        this.estado = EstadoReserva.CANCELADA;
    }

    // Métodos de estado
    public boolean estaPendiente()   { return estado == EstadoReserva.PENDIENTE; }
    public boolean estaConfirmada()  { return estado == EstadoReserva.CONFIRMADA; }
    public boolean estaActiva()      { return estado == EstadoReserva.ACTIVA; }
    public boolean estaFinalizada()  { return estado == EstadoReserva.FINALIZADA; }
    public boolean estaCancelada()   { return estado == EstadoReserva.CANCELADA; }

    // ============================================================
    // Cálculo de total
    // ============================================================
    public double calcularTotal() {
        long dias = ChronoUnit.DAYS.between(desde, hasta);
        if (dias <= 0) dias = 1;
        return habitacion.getPrecio() * dias;
    }
}