package servicios;

import dominio.*;
import validaciones.*;
import almacenamiento.ReservaStorage;

import java.time.LocalDate;
import java.util.List;

public class ReservaService {

    private final ReservaStorage storage;

    public ReservaService(ReservaStorage storage) {
        if (storage == null)
            throw new InputException("ReservaStorage no puede ser null.");
        this.storage = storage;
    }

    private Reserva obtenerReserva(int id) {
        if (id <= 0)
            throw new InputException("ID de reserva inválido.");

        Reserva r = storage.findById(id);
        if (r == null)
            throw new BusinessRuleException("Reserva no encontrada.");

        return r;
    }

    // ============================================================
    // Crear reserva
    // ============================================================
    public Reserva crearReserva(Habitacion h, Huesped hu, Factura f,
                                LocalDate desde, LocalDate hasta) {

        if (h == null || hu == null || f == null)
            throw new InputException("Habitación, huésped y factura son obligatorios.");

        if (desde == null || hasta == null) {
            throw new InputException("Las fechas no pueden ser nulas.");
        }
        if (hasta.isBefore(desde)) {
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }

        // validar disponibilidad
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
    // Transiciones de estado
    // ============================================================
    public void confirmar(int idReserva) {
        Reserva r = obtenerReserva(idReserva);
        r.confirmar();
        storage.update(r);
    }

    public void hacerCheckIn(int idReserva) {
        Reserva r = obtenerReserva(idReserva);
        r.hacerCheckIn();
        storage.update(r);
    }

    public void hacerCheckOut(int idReserva) {
        Reserva r = obtenerReserva(idReserva);
        r.hacerCheckOut();
        storage.update(r);
    }

    public void cancelar(int idReserva) {
        Reserva r = obtenerReserva(idReserva);
        r.cancelar();
        storage.update(r);
    }

    // ============================================================
    // Consultas
    // ============================================================
    public Reserva obtenerReservaPorId(int id) {
        return obtenerReserva(id);
    }

    public List<Reserva> listarTodas() {
        return storage.findAll();
    }

    public List<Reserva> listarEntradas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new InputException("Las fechas no pueden ser nulas.");
        }
        if (hasta.isBefore(desde)) {
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }
        return storage.findEntradas(desde, hasta);
    }

    public List<Reserva> listarSalidas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new InputException("Las fechas no pueden ser nulas.");
        }
        if (hasta.isBefore(desde)) {
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }
        return storage.findSalidas(desde, hasta);
    }

    public boolean existeReservaActiva(String dni, int idHabitacion,
                                       LocalDate desde, LocalDate hasta) {

        if (dni == null || dni.isBlank())
            throw new InputException("DNI no puede ser vacío.");

        if (desde == null || hasta == null) {
            throw new InputException("Las fechas no pueden ser nulas.");
        }
        if (hasta.isBefore(desde)) {
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }

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

        if (desde == null || hasta == null) {
            throw new InputException("Las fechas no pueden ser nulas.");
        }
        if (hasta.isBefore(desde)) {
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }

        return storage.findByHabitacion(idHabitacion).stream()
                .filter(Reserva::estaOperativa)
                .noneMatch(r -> r.seSolapaCon(desde, hasta));
    }

    // ============================================================
    // Validación para baja de huéspedes
    // ============================================================
    public boolean poseeReservasOperativasDeHuesped(String dni) {
        if (dni == null || dni.isBlank())
            throw new InputException("DNI inválido.");

        return storage.findAll().stream()
                .filter(Reserva::estaOperativa)
                .anyMatch(r -> dni.equals(r.getHuesped().getDni()));
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


}