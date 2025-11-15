package aplicacion;

import dominio.*;
import dominio.Factura.MetodoPago;
import validaciones.ValidacionException;
import validaciones.Validator;
import servicios.*;
import almacenamiento.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

/**
 * Clase fachada del sistema (GRASP Controller).
 * Coordina las operaciones principales del negocio sin manejar directamente la persistencia.
 */
public class Sistema {

    private final HabitacionService habitacionService;
    private final HuespedService huespedService;
    private final FacturaService facturaService;
    private final ReservaService reservaService;

    // ==============================
    // CONSTRUCTORES
    // ==============================

    public Sistema(Connection conn) {
        HabitacionStorage habitacionStorage = new HabitacionStorage(conn);
        HuespedStorage huespedStorage = new HuespedStorage(conn);
        FacturaStorage facturaStorage = new FacturaStorage(conn);
        ReservaStorage reservaStorage = new ReservaStorage(conn);

        this.habitacionService = new HabitacionService(habitacionStorage);
        this.huespedService = new HuespedService(huespedStorage);
        this.facturaService = new FacturaService(facturaStorage);
        this.reservaService = new ReservaService(reservaStorage);
    }

    public Sistema() {
        this(DatabaseManager.getConnection());
    }

    public void cerrar() {
        DatabaseManager.closeConnection();
    }

    // ==============================
    // FUNCIONALIDADES DE NEGOCIO
    // ==============================

    /**
     * Crea una reserva completa con huésped, habitación y factura.
     */
    public Reserva crearReserva(String dni, String nombre, String apellido, String email,
                                int idHabitacion, LocalDate desde, LocalDate hasta,
                                MetodoPago metodo, int cuotas) {

        Validator.rangoFechas(desde, hasta);

        // Buscar o crear huésped
        Huesped huesped = huespedService.obtenerPorDni(dni);
        if (huesped == null) {
            huesped = new Huesped(dni, nombre, apellido, email);
            huespedService.registrarHuesped(huesped);
        }

        // Obtener habitación
        Habitacion habitacion = habitacionService.obtenerPorId(idHabitacion);
        if (habitacion == null)
            throw new ValidacionException("La habitación con ID " + idHabitacion + " no existe.");

        // Evitar duplicación de reservas
        if (reservaService.existeReservaActiva(dni, idHabitacion, desde, hasta))
            throw new ValidacionException("Ya existe una reserva activa para ese huésped en esas fechas.");

        // Crear factura
        Factura factura = facturaService.crearFactura(habitacion, desde, hasta, metodo, cuotas);

        // Crear reserva (verifica disponibilidad dentro del service)
        Reserva reserva = reservaService.crearReserva(habitacion, huesped, factura, desde, hasta);

        // System.out.println("Reserva creada correctamente -> ID: " + reserva.getId());

        System.out.println("Reserva creada: " + reserva.getHuesped());

        return reserva;
    }

    /**
     * Registrar el pago de una factura.
     */
    public void registrarPago(int idFactura) {
        facturaService.registrarPago(idFactura);
        reservaService.confirmarSiPendientePorFactura(idFactura);
        // System.out.println("Factura pagada correctamente -> ID: " + idFactura);
    }

    /**
     * Hacer check-in (solo si la factura está pagada).
     */
    public void hacerCheckIn(int idReserva) {
        Reserva reserva = reservaService.obtenerPorId(idReserva);
        if (reserva == null)
            throw new ValidacionException("No existe la reserva con ID " + idReserva);

        Factura facturaActual = facturaService.obtenerPorId(reserva.getFactura().getId());
        if (facturaActual == null || !facturaActual.isPagada())
            throw new ValidacionException("La factura asociada no está pagada. No se puede hacer check-in.");

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(reserva.getDesde()))
            throw new ValidacionException("No puede realizar check-in antes de la fecha de inicio.");

        reservaService.hacerCheckIn(idReserva);
        // System.out.println("Check-in realizado correctamente -> ID: " + idReserva);
    }

    /**
     * Hacer check-out y finalizar la reserva.
     */
    public void hacerCheckOut(int idReserva) {
        reservaService.hacerCheckOut(idReserva);
        // System.out.println("Check-out realizado correctamente -> ID: " + idReserva);
    }

    public void cancelarReserva(int idReserva) {
        reservaService.cancelar(idReserva);
        // System.out.println("Cancelación correcta -> ID: " + idReserva);
    }

    // ==============================
    // REPORTES Y CONSULTAS
    // ==============================

    public List<Factura> getFacturasVencidas(LocalDate hoy) {
        return facturaService.obtenerPendientesHasta(hoy);
    }

    /**
     * Devuelve las reservas pendientes de pago (estado PENDIENTE) en un rango de ±30 días.
     */
    public List<Reserva> getReservasPendientesDeCobro(LocalDate ref) {
        List<Reserva> todas = listarTodasLasReservas(); // << reservas completas
        return todas.stream()
                .filter(r -> r.getFactura() != null && !r.getFactura().isPagada())
                // si querés limitar por vencimiento futuro, dejá esta línea; si no, quítala
                .filter(r -> r.getFactura().getVencimiento() == null
                        || !r.getFactura().getVencimiento().isBefore(ref))
                .toList();
    }

    // === Próximas entradas ===
    public List<Reserva> listarProximasEntradas(LocalDate desde, LocalDate hasta) {
        return listarTodasLasReservas().stream()
                .filter(r -> r.getDesde() != null)
                .filter(r -> !r.getDesde().isBefore(desde) && !r.getDesde().isAfter(hasta))
                .toList();
    }

    // === Próximas salidas ===
    public List<Reserva> listarProximasSalidas(LocalDate desde, LocalDate hasta) {
        return listarTodasLasReservas().stream()
                .filter(r -> r.getHasta() != null)
                .filter(r -> !r.getHasta().isBefore(desde) && !r.getHasta().isAfter(hasta))
                .toList();
    }

    /**
     * Calcula el porcentaje de ocupación actual.
     * Cuenta reservas CONFIRMADAS con inicio hoy o EN_CURSO.
     */
    public double getOcupacionHoy() {
        List<Habitacion> todas = habitacionService.obtenerTodas();
        if (todas.isEmpty()) return 0.0;

        LocalDate hoy = LocalDate.now();
        long ocupadas = listarTodasLasReservas().stream()
                .filter(r -> r.getEstado() == Reserva.EstadoReserva.ACTIVA ||
                        (r.getEstado() == Reserva.EstadoReserva.CONFIRMADA && r.getDesde().isEqual(hoy)))
                .map(r -> r.getHabitacion().getId())
                .distinct()
                .count();

        return Math.round((ocupadas * 10000.0) / todas.size()) / 100.0;
    }

    public List<Habitacion> listarHabitacionesDisponibles(LocalDate desde, LocalDate hasta) {
        return habitacionService.buscarDisponibles(desde, hasta);
    }

    public List<Reserva> listarTodasLasReservas() {
        return reservaService.listarTodas(this);
    }

    /**
     * Muestra un resumen del estado del hotel.
     */
    public void mostrarResumenHotel() {
        // Cancelar automáticamente reservas vencidas
        reservaService.cancelarReservasVencidas();

        double ocupacion = getOcupacionHoy();
        int vencidas = getFacturasVencidas(LocalDate.now()).size();
        int pendientes = getReservasPendientesDeCobro(LocalDate.now()).size();

        System.out.println("Estado del Hotel:");
        System.out.println("- Ocupación actual: " + ocupacion + "%");
        System.out.println("- Facturas vencidas: " + vencidas);
        System.out.println("- Reservas pendientes de cobro: " + pendientes);
    }

    // ==============================
// MÉTODOS PÚBLICOS DE ALTA
// ==============================

    /**
     * Crea una nueva habitación en el sistema.
     */
    public void crearHabitacion(String nombre, String descripcion, double precio,
                                Habitacion.TipoHabitacion tipo, int capacidad) {
        Habitacion nueva = new Habitacion(0, nombre, descripcion, precio, tipo, capacidad);
        habitacionService.crearHabitacion(nueva);
    }

    // Lista todas las habitaciones registradas
    public void listarTodasLasHabitaciones() {
        System.out.println("Habitaciones registradas:");
        for (Habitacion h : habitacionService.obtenerTodas()) {
            System.out.printf(" - ID: %d | Nombre: %s | Precio: %.2f | Capacidad: %d%n",
                    h.getId(), h.getNombre(), h.getPrecio(), h.getCapacidad());
        }
    }

    // Lista todas las reservas registradas
    public void listarTodasLasReservasDebug() {
        System.out.println("Reservas registradas:");
        for (Reserva r : listarTodasLasReservas()) {
            System.out.printf(
                    "Reserva #%d | HabitacionID=%d | HuespedID=%d | FacturaID=%d | Habitacion=%s | Cliente=%s | Total=%.2f%n",
                    r.getId(),
                    r.getHabitacion() != null ? r.getHabitacion().getId() : -1,
                    r.getHuesped() != null ? r.getHuesped().getId() : -1,
                    r.getFactura() != null ? r.getFactura().getId() : -1,
                    r.getHabitacion() != null ? r.getHabitacion().getNombre() : "(null)",
                    r.getHuesped() != null ? r.getHuesped().getNombreCompleto() : "(null)",
                    r.getFactura() != null ? r.getFactura().getTotal() : 0.0
            );
        }
    }

    public HabitacionService getHabitacionService() {
        return habitacionService;
    }

    public HuespedService getHuespedService() {
        return huespedService;
    }

    public ReservaService getReservaService() {
        return reservaService;
    }

    public FacturaService getFacturaService() {
        return facturaService;
    }

}