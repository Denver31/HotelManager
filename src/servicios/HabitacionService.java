package servicios;

import almacenamiento.HabitacionStorage;
import dominio.Habitacion;
import dominio.Reserva;
import validaciones.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HabitacionService {

    private final HabitacionStorage storage;
    private final ReservaService reservaService;

    public HabitacionService(HabitacionStorage storage, ReservaService reservaService) {
        if (storage == null) throw new InputException("HabitacionStorage es obligatorio.");
        if (reservaService == null) throw new InputException("ReservaService es obligatorio.");

        this.storage = storage;
        this.reservaService = reservaService;
    }

    // ============================================================
    // Consultas
    // ============================================================
    public Habitacion obtenerPorId(int id) {
        if (id <= 0) throw new InputException("ID inválido.");
        return storage.findById(id);
    }

    public List<Habitacion> buscarDisponibles(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new InputException("Las fechas no pueden ser nulas.");
        }
        if (hasta.isBefore(desde)) {
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }

        return storage.findTodasActivas().stream()
                .filter(h -> reservaService.noTieneSolapamientos(h.getId(), desde, hasta))
                .collect(Collectors.toList());
    }

    public List<Habitacion> obtenerTodas() {
        return storage.findAll();
    }

    // ============================================================
    // Crear y modificar
    // ============================================================
    public void crearHabitacion(Habitacion h) {
        if (h == null) throw new InputException("Habitación inválida.");
        storage.save(h);
    }

    public void modificarHabitacion(int id, String nombre, String descripcion,
                                    double precio, Habitacion.TipoHabitacion tipo,
                                    int capacidad) {

        Habitacion h = obtenerPorId(id);
        if (h == null) throw new BusinessRuleException("Habitación no encontrada.");

        h.modificarDatos(nombre, descripcion, precio, tipo, capacidad);
        storage.update(h);
    }

    // ============================================================
    // Dar de baja
    // ============================================================
    public void darDeBaja(int id) {
        Habitacion h = obtenerPorId(id);
        if (h == null) throw new BusinessRuleException("Habitación no encontrada.");

        // Validar que no tenga reservas pendientes / confirmadas / activas
        if (reservaService.poseeReservasOperativas(id))
            throw new BusinessRuleException("No se puede dar de baja: la habitación tiene reservas activas.");

        h.darDeBaja();
        storage.update(h);
    }

    public void activar(int id) {
        Habitacion h = obtenerPorId(id);
        if (h == null) throw new BusinessRuleException("Habitación no encontrada.");

        h.activar();
        storage.update(h);
    }
}
