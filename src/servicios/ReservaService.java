package servicios;

import almacenamiento.ReservaStorage;
import dominio.*;
import validaciones.*;

import dto.reserva.ReservaListadoDTO;
import dto.reserva.ReservaDetalleDTO;
import dto.reserva.CrearReservaDTO;
import dto.reserva.ReservaDashboardDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaService {

    private final ReservaStorage storage;

    public ReservaService(ReservaStorage storage) {
        if (storage == null)
            throw new InputException("ReservaStorage no puede ser null.");
        this.storage = storage;
    }

    // ============================================================
    // Helpers internos
    // ============================================================
    private Reserva getReservaOrThrow(int id) {
        if (id <= 0)
            throw new InputException("ID de reserva inválido.");

        Reserva r = storage.findById(id);
        if (r == null)
            throw new BusinessRuleException("Reserva no encontrada.");

        return r;
    }

    private void validarRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null)
            throw new InputException("Las fechas no pueden ser nulas.");

        if (hasta.isBefore(desde))
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");
    }

    private void validarHuespedYHabitacionActivos(Habitacion h, Huesped hu) {
        if (h == null || hu == null)
            throw new InputException("Habitación y huésped son obligatorios.");

        if (!hu.estaActivo())
            throw new BusinessRuleException("No se puede crear una reserva para un huésped dado de baja.");

        if (!h.estaActiva())
            throw new BusinessRuleException("No se puede crear una reserva para una habitación dada de baja.");
    }

    // ============================================================
    // Conversión a DTOs
    // ============================================================
    private ReservaListadoDTO convertirAListadoDTO(Reserva r) {
        return new ReservaListadoDTO(
                r.getId(),
                r.getHuesped().getId(),
                r.getHuesped().getNombreCompleto(),
                r.getHabitacion().getId(),
                r.getHabitacion().getNombre(),
                r.getDesde(),
                r.getHasta(),
                r.getEstado().name()
        );
    }

    private ReservaDetalleDTO convertirADetalleDTO(Reserva r) {
        return new ReservaDetalleDTO(
                r.getId(),
                r.getHabitacion().getId(),
                r.getHuesped().getId(),
                r.getFactura().getId(),
                r.getHuesped().getNombreCompleto(),
                r.getHabitacion().getNombre(),
                r.getDesde(),
                r.getHasta(),
                r.getEstado().name(),
                r.getFactura().getTotal()
        );
    }

    public ReservaDetalleDTO crearReservaDesdeUI(
            CrearReservaDTO dto,
            HuespedService huespedService,
            HabitacionService habitacionService,
            FacturaService facturaService
    ) {

        Huesped h = huespedService.obtenerPorId(dto.idHuesped());
        Habitacion hab = habitacionService.obtenerPorId(dto.idHabitacion());

        if (!h.estaActivo())
            throw new BusinessRuleException("El huésped está dado de baja.");

        if (!hab.estaActiva())
            throw new BusinessRuleException("La habitación está dada de baja.");

        // 1) Reserva de preview (NO SE GUARDA)
        Reserva preview = new Reserva(hab, h, null, dto.desde(), dto.hasta());
        double total = preview.calcularTotal();

        // 2) Crear factura real
        Factura factura = facturaService.crearFacturaReserva(
                total,
                Factura.MetodoPago.valueOf(dto.metodoPago()),
                dto.cuotas()
        );

        // 3) Crear reserva REAL (esta sí tiene factura)
        Reserva real = new Reserva(hab, h, factura, dto.desde(), dto.hasta());

        // 4) Guardar en Storage
        storage.save(real);

        return convertirADetalleDTO(real);
    }




    // ============================================================
    // Crear reserva (dominio)
    // ============================================================
    /**
     * Crear reserva a nivel dominio.
     * Se asume que Factura ya fue creada (por FacturaService) y que
     * Habitacion y Huesped fueron obtenidos desde sus servicios.
     *
     * Este método se usa desde la capa de aplicación (por ejemplo Sistema)
     * y no directamente desde la UI.
     */
    public Reserva crearReserva(Habitacion h, Huesped hu, Factura f,
                                LocalDate desde, LocalDate hasta) {

        if (f == null)
            throw new InputException("Factura obligatoria para crear la reserva.");

        validarHuespedYHabitacionActivos(h, hu);
        validarRangoFechas(desde, hasta);

        // validar disponibilidad: no permitir reservar habitación ocupada en el rango
        List<Reserva> reservasHab = storage.findByHabitacion(h.getId());

        boolean disponible = reservasHab.stream()
                .filter(Reserva::estaOperativa)
                .noneMatch(r -> r.seSolapaCon(desde, hasta));

        if (!disponible)
            throw new BusinessRuleException("La habitación ya está reservada en ese período.");

        Reserva r = new Reserva(h, hu, f, desde, hasta);
        storage.save(r);
        return r;
    }

    // ============================================================
    // Métodos orientados a UI
    // ============================================================
    public List<ReservaListadoDTO> obtenerListado() {
        return storage.findAll()
                .stream()
                .map(this::convertirAListadoDTO)
                .collect(Collectors.toList());
    }

    public ReservaDetalleDTO obtenerDetalle(int id) {
        Reserva r = getReservaOrThrow(id);
        return convertirADetalleDTO(r);
    }

    /**
     * Búsqueda flexible por texto:
     * - ID
     * - nombre huésped
     * - habitación
     * - DNI huésped
     * - estado
     */
    public List<ReservaListadoDTO> buscarPorTexto(String filtro) {
        if (filtro == null || filtro.isBlank())
            return obtenerListado();

        String f = filtro.trim().toLowerCase();

        return storage.findAll().stream()
                .filter(r -> {
                    String idStr = String.valueOf(r.getId());
                    String huesped = r.getHuesped().getNombreCompleto().toLowerCase();
                    String habitacion = r.getHabitacion().getNombre().toLowerCase();
                    String dni = r.getHuesped().getDni();
                    String estado = r.getEstado().name().toLowerCase();

                    return idStr.contains(f)
                            || huesped.contains(f)
                            || habitacion.contains(f)
                            || dni.contains(f)
                            || estado.contains(f);
                })
                .map(this::convertirAListadoDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaListadoDTO> buscarPorRango(LocalDate desde, LocalDate hasta) {
        validarRangoFechas(desde, hasta);

        return storage.findAll().stream()
                .filter(r -> r.seSolapaCon(desde, hasta))
                .map(this::convertirAListadoDTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // Transiciones de estado (orientadas a UI)
    // ============================================================
    public ReservaDetalleDTO confirmar(int idReserva) {
        Reserva r = getReservaOrThrow(idReserva);
        r.confirmar();
        storage.update(r);
        return convertirADetalleDTO(r);
    }

    public ReservaDetalleDTO hacerCheckIn(int idReserva) {
        Reserva r = getReservaOrThrow(idReserva);

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(r.getDesde())) {
            throw new BusinessRuleException("No se puede hacer check-in antes de la fecha de entrada.");
        }

        r.hacerCheckIn();
        storage.update(r);
        return convertirADetalleDTO(r);
    }

    public ReservaDetalleDTO hacerCheckOut(int idReserva) {
        Reserva r = getReservaOrThrow(idReserva);
        r.hacerCheckOut();
        storage.update(r);
        return convertirADetalleDTO(r);
    }

    public ReservaDetalleDTO cancelar(int idReserva) {
        Reserva r = getReservaOrThrow(idReserva);

        // Regla: no permitir cancelar reservas ACTIVAS (con check-in hecho)
        if (r.getEstado() == Reserva.EstadoReserva.ACTIVA) {
            throw new BusinessRuleException("No se puede cancelar una reserva activa (ya tiene check-in).");
        }

        r.cancelar();
        storage.update(r);
        return convertirADetalleDTO(r);
    }

    // ============================================================
    // Consultas dominio (para otros servicios / dashboard)
    // ============================================================
    public Reserva obtenerReservaPorId(int id) {
        return getReservaOrThrow(id);
    }

    public List<Reserva> listarTodas() {
        return storage.findAll();
    }

    public boolean existeReservaActiva(String dni, int idHabitacion,
                                       LocalDate desde, LocalDate hasta) {

        if (dni == null || dni.isBlank())
            throw new InputException("DNI no puede ser vacío.");

        validarRangoFechas(desde, hasta);

        return storage.findByHabitacion(idHabitacion).stream()
                .filter(r -> dni.equals(r.getHuesped().getDni()))
                .filter(Reserva::estaOperativa)
                .anyMatch(r -> r.seSolapaCon(desde, hasta));
    }

    // ============================================================
    // Métodos auxiliares para HabitacionService
    // ============================================================
    public boolean poseeReservasOperativas(int idHabitacion) {
        return storage.findByHabitacion(idHabitacion).stream()
                .anyMatch(Reserva::estaOperativa);
    }

    public boolean noTieneSolapamientos(int idHabitacion,
                                        LocalDate desde, LocalDate hasta) {

        validarRangoFechas(desde, hasta);

        return storage.findByHabitacion(idHabitacion).stream()
                .filter(Reserva::estaOperativa)
                .noneMatch(r -> r.seSolapaCon(desde, hasta));
    }

    // ============================================================
    // Validación para baja de huéspedes (por ID)
    // ============================================================
    public boolean poseeReservasOperativasDeHuesped(int idHuesped) {
        if (idHuesped <= 0)
            throw new InputException("ID inválido.");

        return storage.findAll().stream()
                .filter(Reserva::estaOperativa)
                .anyMatch(r -> r.getHuesped().getId() == idHuesped);
    }

    // ============================================================
    // Facturas → Reservas
    // ============================================================

    /**
     * Cuando una factura se paga, si la reserva asociada estaba pendiente,
     * debe pasar a CONFIRMADA.
     */
    public void confirmarReservasConFactura(int idFactura) {
        Reserva r = storage.findByFacturaId(idFactura);
        if (r == null) return;

        if (r.estaPendiente()) {
            r.setEstado(Reserva.EstadoReserva.CONFIRMADA);
            storage.update(r);
        }
    }

    /**
     * Cuando una factura vence, la reserva asociada debe cancelarse.
     * Esto solo aplica si la reserva estaba PENDIENTE o CONFIRMADA.
     */
    public void cancelarReservaPorFacturaVencida(int idFactura) {
        Reserva r = storage.findByFacturaId(idFactura);
        if (r == null) return;

        if (r.estaPendiente() || r.estaConfirmada()) {
            r.cancelar();
            storage.update(r);
        }
    }

    public Reserva obtenerPorFactura(int idFactura) {
        if (idFactura <= 0)
            throw new InputException("ID de factura inválido.");

        Reserva r = storage.findByFacturaId(idFactura);
        if (r == null)
            throw new BusinessRuleException("No existe ninguna reserva asociada a esa factura.");

        return r;
    }

    public Huesped obtenerHuespedPorFactura(int idFactura) {
        Reserva r = obtenerPorFactura(idFactura);
        Huesped h = r.getHuesped();

        if (h == null)
            throw new BusinessRuleException("La reserva asociada no tiene un huésped asignado.");

        return h;
    }

    public int contarHabitacionesTotales() {
        return (int) storage.findAll().stream()
                .map(Reserva::getHabitacion)
                .map(Habitacion::getId)
                .distinct()
                .count();
    }

    public int contarHabitacionesOcupadasHoy() {

        LocalDate hoy = LocalDate.now();

        return (int) storage.findAll().stream()
                .filter(Reserva::estaActiva)
                .filter(r -> !hoy.isBefore(r.getDesde()) && !hoy.isAfter(r.getHasta()))
                .map(r -> r.getHabitacion().getId())
                .distinct()
                .count();
    }

    public List<ReservaDashboardDTO> listarEntradasUI() {

        LocalDate hoy = LocalDate.now();
        LocalDate futuro = hoy.plusDays(7);

        return storage.findEntradas(hoy, futuro).stream()
                .map(r -> new ReservaDashboardDTO(
                        r.getDesde(),
                        r.getId(),
                        r.getHuesped().getNombreCompleto(),
                        r.getHabitacion().getId(),
                        null,
                        null
                ))
                .toList();
    }

    public List<ReservaDashboardDTO> listarSalidasUI() {

        LocalDate hoy = LocalDate.now();
        LocalDate futuro = hoy.plusDays(7);

        return storage.findSalidas(hoy, futuro).stream()
                .map(r -> new ReservaDashboardDTO(
                        r.getHasta(),
                        r.getId(),
                        r.getHuesped().getNombreCompleto(),
                        r.getHabitacion().getId(),
                        null,
                        null
                ))
                .toList();
    }

    public List<ReservaDashboardDTO> listarPendientesDeCobroUI() {

        return storage.findAll().stream()
                .filter(r -> r.getFactura() != null)
                .filter(r -> r.getFactura().estaPendiente())
                .filter(r -> r.estaPendiente() || r.estaConfirmada())
                .map(r -> new ReservaDashboardDTO(
                        r.getDesde(),
                        r.getId(),
                        r.getHuesped().getNombreCompleto(),
                        r.getHabitacion().getId(),
                        r.getFactura().getId(),
                        r.getFactura().getTotal()
                ))
                .toList();
    }


}