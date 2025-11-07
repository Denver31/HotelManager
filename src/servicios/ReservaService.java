package servicios;

import aplicacion.Sistema;
import dominio.*;
import validaciones.DisponibilidadException;
import validaciones.ValidacionException;
import validaciones.Validator;
import almacenamiento.ReservaStorage;
import java.time.LocalDate;
import java.util.List;

public class ReservaService {

    private ReservaStorage storage;

    public ReservaService(ReservaStorage storage) {
        this.storage = storage;
    }

    public Reserva crearReserva(Habitacion h, Huesped hu, Factura f, LocalDate desde, LocalDate hasta) {
        if (h == null || hu == null || f == null)
            throw new ValidacionException("Habitación, huésped y factura son obligatorios.");

        Validator.rangoFechas(desde, hasta);

        // Verificar disponibilidad en la base de datos (no solo en memoria)
        List<Reserva> existentes = storage.findAll();
        boolean disponible = existentes.stream().noneMatch(r ->
                r.getHabitacion().getId() == h.getId() &&
                        !(hasta.isBefore(r.getDesde()) || desde.isAfter(r.getHasta()))
        );

        if (!disponible)
            throw new DisponibilidadException("La habitación ya está reservada en esas fechas.");

        Reserva reserva = new Reserva(h, hu, f, desde, hasta);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        storage.save(reserva);
        return reserva;
    }

    public void hacerCheckIn(int idReserva) {
        Reserva r = storage.findById(idReserva);
        if (r == null)
            throw new ValidacionException("Reserva no encontrada.");
        r.hacerCheckIn();
        storage.update(r);
    }

    public void hacerCheckOut(int idReserva) {
        Reserva r = storage.findById(idReserva);
        if (r == null)
            throw new ValidacionException("Reserva no encontrada.");
        r.hacerCheckOut();
        storage.update(r);
    }

    public void cancelar(int idReserva) {
        Reserva r = storage.findById(idReserva);
        if (r == null)
            throw new ValidacionException("Reserva no encontrada.");
        r.cancelar();
        storage.update(r);
    }

    public Reserva obtenerPorId(int id) {
        return storage.findById(id);
    }

    public List<Reserva> listarProximasEntradas(LocalDate desde, LocalDate hasta) {
        Validator.rangoFechas(desde, hasta);
        return storage.findEntradas(desde, hasta);
    }

    public List<Reserva> listarProximasSalidas(LocalDate desde, LocalDate hasta) {
        Validator.rangoFechas(desde, hasta);
        return storage.findSalidas(desde, hasta);
    }

    public List<Reserva> listarTodas(Sistema sistema) {
        List<Reserva> reservas = storage.findAll();

        for (Reserva r : reservas) {
            r.setHabitacion(sistema.getHabitacionService().obtenerPorId(r.getHabitacion().getId()));
            r.setHuesped(sistema.getHuespedService().obtenerPorId(r.getHuesped().getId()));
            r.setFactura(sistema.getFacturaService().obtenerPorId(r.getFactura().getId()));
        }

        return reservas;
    }

    public void confirmarSiPendientePorFactura(int idFactura) {
        Reserva r = storage.findByFacturaId(idFactura);
        if (r != null && r.getEstado() == Reserva.EstadoReserva.PENDIENTE) {
            r.setEstado(Reserva.EstadoReserva.CONFIRMADA);
            storage.update(r);
        }
    }

    public void cancelarReservasVencidas() {
        List<Reserva> reservas = storage.findAll();
        for (Reserva r : reservas) {
            if (r.getEstado() == Reserva.EstadoReserva.PENDIENTE &&
                    r.getFactura().esVencida()) {
                r.setEstado(Reserva.EstadoReserva.CANCELADA);
                storage.update(r);
            }
        }
    }

    public boolean existeReservaActiva(String dni, int idHabitacion, LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = storage.findAll();
        return reservas.stream().anyMatch(r ->
                r.getHuesped().getDni().equals(dni)
                        && r.getHabitacion().getId() == idHabitacion
                        && (r.getEstado() == Reserva.EstadoReserva.PENDIENTE
                        || r.getEstado() == Reserva.EstadoReserva.CONFIRMADA
                        || r.getEstado() == Reserva.EstadoReserva.ACTIVA)
                        && !(hasta.isBefore(r.getDesde()) || desde.isAfter(r.getHasta()))
        );
    }

}