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
    // Helpers internos NECESARIOS para los métodos permitidos
    // ============================================================
    private Reserva getReservaOrThrow(int id) {
        if (id <= 0)
            throw new InputException("ID de reserva inválido.");

        Reserva r = storage.findById(id);
        if (r == null)
            throw new BusinessRuleException("Reserva no encontrada.");

        return r;
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

    // ============================================================
    // MÉTODOS PERMITIDOS — usados por presenters
    // ============================================================

    // Usado por CrearReservaPresenter
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

        Reserva preview = new Reserva(hab, h, null, dto.desde(), dto.hasta());
        double total = preview.calcularTotal();

        Factura factura = facturaService.crearFacturaReserva(
                total,
                Factura.MetodoPago.valueOf(dto.metodoPago()),
                dto.cuotas()
        );

        Reserva real = new Reserva(hab, h, factura, dto.desde(), dto.hasta());
        storage.save(real);

        return convertirADetalleDTO(real);
    }

    // Usado por ReservasPresenter
    public List<Reserva> listarTodas() {
        return storage.findAll();
    }

    // Usado por DetalleReservaPresenter
    public ReservaDetalleDTO obtenerDetalle(int id) {
        Reserva r = getReservaOrThrow(id);
        return convertirADetalleDTO(r);
    }

    // Usado por DetalleReservaPresenter
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

    // Usado por DetalleReservaPresenter
    public ReservaDetalleDTO hacerCheckOut(int idReserva) {
        Reserva r = getReservaOrThrow(idReserva);
        r.hacerCheckOut();
        storage.update(r);
        return convertirADetalleDTO(r);
    }

    // Usado por DetalleReservaPresenter
    public ReservaDetalleDTO cancelar(int idReserva) {
        Reserva r = getReservaOrThrow(idReserva);

        if (r.getEstado() == Reserva.EstadoReserva.ACTIVA) {
            throw new BusinessRuleException("No se puede cancelar una reserva activa (ya tiene check-in).");
        }

        r.cancelar();
        storage.update(r);
        return convertirADetalleDTO(r);
    }

    // ============================================================
    // Dashboard (PanelControlPresenter)
    // ============================================================

    // Usado
    public int contarHabitacionesTotales() {
        return (int) storage.findAll().stream()
                .map(Reserva::getHabitacion)
                .map(Habitacion::getId)
                .distinct()
                .count();
    }

    // Usado
    public int contarHabitacionesOcupadasHoy() {
        LocalDate hoy = LocalDate.now();

        return (int) storage.findAll().stream()
                .filter(Reserva::estaActiva)
                .filter(r -> !hoy.isBefore(r.getDesde()) && !hoy.isAfter(r.getHasta()))
                .map(r -> r.getHabitacion().getId())
                .distinct()
                .count();
    }

    // Usado
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

    // Usado
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

    // Usado
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

    // ============================================================
    // METODOS UTILIZADOS EN OTROS SERVICIOS
    // ============================================================

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

    public boolean poseeReservasOperativas(int idHabitacion) {
        return storage.findByHabitacion(idHabitacion).stream()
                .anyMatch(Reserva::estaOperativa);
    }

    public boolean noTieneSolapamientos(int idHabitacion,
                                        LocalDate desde, LocalDate hasta) {

        if (desde == null || hasta == null)
            throw new InputException("Las fechas no pueden ser nulas.");

        if (hasta.isBefore(desde))
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");

        return storage.findByHabitacion(idHabitacion).stream()
                .filter(Reserva::estaOperativa)
                .noneMatch(r -> r.seSolapaCon(desde, hasta));
    }

    public boolean poseeReservasOperativasDeHuesped(int idHuesped) {
        if (idHuesped <= 0)
            throw new InputException("ID inválido.");

        return storage.findAll().stream()
                .filter(Reserva::estaOperativa)
                .anyMatch(r -> r.getHuesped().getId() == idHuesped);
    }

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
}


