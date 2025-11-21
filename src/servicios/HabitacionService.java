package servicios;

import almacenamiento.HabitacionStorage;

import dominio.Habitacion;

import validaciones.*;

import dto.CrearHabitacionDTO;
import dto.HabitacionDetalleDTO;
import dto.HabitacionListadoDTO;

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
    // Helpers internos necesarios
    // ============================================================
    private Habitacion getHabitacionOrThrow(int id) {
        if (id <= 0) throw new InputException("ID de habitación inválido.");
        Habitacion h = storage.findById(id);
        if (h == null) throw new BusinessRuleException("Habitación no encontrada.");
        return h;
    }

    private HabitacionDetalleDTO toDetalleDTO(Habitacion h) {
        return new HabitacionDetalleDTO(
                h.getId(),
                h.getNombre(),
                h.getTipo(),
                h.getCapacidad(),
                h.getDescripcion(),
                h.getPrecio(),
                h.getEstado().name()
        );
    }

    private HabitacionListadoDTO toListadoDTO(Habitacion h) {
        return new HabitacionListadoDTO(
                h.getId(),
                h.getNombre(),
                h.getTipo().name(),
                h.getCapacidad(),
                h.getPrecio(),
                h.getEstado().name()
        );
    }

    private void validarRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null)
            throw new InputException("Las fechas no pueden ser nulas.");
        if (hasta.isBefore(desde))
            throw new InputException("La fecha 'hasta' no puede ser anterior a 'desde'.");
    }

    // ============================================================
    // Métodos permitidos (usados por presenters)
    // ============================================================
    public int crearHabitacion(CrearHabitacionDTO dto) {
        if (dto == null)
            throw new InputException("Datos de habitación inválidos.");

        Habitacion h = new Habitacion(
                dto.nombre(),
                dto.descripcion(),
                dto.precio(),
                dto.tipo(),
                dto.capacidad()
        );

        storage.save(h);
        return h.getId();
    }

    public HabitacionDetalleDTO obtenerDetalle(int id) {
        Habitacion h = getHabitacionOrThrow(id);
        return toDetalleDTO(h);
    }

    public List<HabitacionListadoDTO> obtenerListado() {
        return storage.findAll().stream()
                .map(this::toListadoDTO)
                .collect(Collectors.toList());
    }

    public List<HabitacionListadoDTO> buscarDisponibles(LocalDate desde, LocalDate hasta) {
        validarRangoFechas(desde, hasta);

        List<Habitacion> disponibles = storage.findTodasActivas().stream()
                .filter(h -> reservaService.noTieneSolapamientos(h.getId(), desde, hasta))
                .collect(Collectors.toList());

        return disponibles.stream()
                .map(this::toListadoDTO)
                .collect(Collectors.toList());
    }

    public Habitacion obtenerPorId(int id) {
        if (id <= 0)
            throw new InputException("ID de habitación inválido.");

        Habitacion h = storage.findById(id);
        if (h == null)
            throw new BusinessRuleException("Habitación no encontrada.");

        return h;
    }

    public HabitacionDetalleDTO darDeBaja(int id) {
        Habitacion h = getHabitacionOrThrow(id);

        if (reservaService.poseeReservasOperativas(id))
            throw new BusinessRuleException("No se puede dar de baja: la habitación tiene reservas activas.");

        h.darDeBaja();
        storage.update(h);

        return toDetalleDTO(h);
    }

    public HabitacionDetalleDTO activar(int id) {
        Habitacion h = getHabitacionOrThrow(id);

        h.activar();
        storage.update(h);

        return toDetalleDTO(h);
    }
}
