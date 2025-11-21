package servicios;

import almacenamiento.HuespedStorage;
import dominio.Huesped;
import validaciones.InputException;
import validaciones.BusinessRuleException;

import dto.CrearHuespedDTO;
import dto.HuespedDetalleDTO;
import dto.HuespedListadoDTO;

import java.util.List;
import java.util.stream.Collectors;

public class HuespedService {

    private final HuespedStorage storage;
    private final ReservaService reservaService;

    public HuespedService(HuespedStorage storage, ReservaService reservaService) {
        if (storage == null) throw new InputException("HuespedStorage obligatorio.");
        if (reservaService == null) throw new InputException("ReservaService obligatorio.");
        this.storage = storage;
        this.reservaService = reservaService;
    }

    // ============================================================
    // CREAR
    // ============================================================
    public int crearHuesped(CrearHuespedDTO dto) {
        validarDTO(dto);

        if (storage.findByDni(dto.dni()) != null)
            throw new BusinessRuleException("Ya existe un huésped con ese DNI.");

        Huesped h = new Huesped(
                dto.dni(),
                dto.nombre(),
                dto.apellido(),
                dto.email()
        );

        storage.save(h);
        return h.getId();
    }

    // ============================================================
    // MODIFICAR
    // ============================================================
    public HuespedDetalleDTO modificarHuesped(int id, CrearHuespedDTO dto) {
        validarId(id);
        validarDTO(dto);

        Huesped h = getOrThrow(id);

        h.modificarDatos(dto.nombre(), dto.apellido(), dto.email());
        storage.update(h);

        return convertirADetalleDTO(h);
    }

    // ============================================================
    // DETALLE
    // ============================================================
    public HuespedDetalleDTO obtenerDetalle(int id) {
        validarId(id);
        return convertirADetalleDTO(getOrThrow(id));
    }

    // ============================================================
    // LISTADO
    // ============================================================
    public List<HuespedListadoDTO> obtenerListado() {
        return storage.findAll().stream()
                .map(h -> new HuespedListadoDTO(
                        h.getId(),
                        h.getNombre(),
                        h.getApellido(),
                        h.getDni(),
                        h.getEmail(),
                        h.getEstado().name()
                ))
                .toList();
    }


    // ============================================================
    // BÚSQUEDA
    // ============================================================
    public List<HuespedListadoDTO> buscarPorTexto(String filtro) {
        if (filtro == null || filtro.isBlank()) return obtenerListado();

        String f = filtro.toLowerCase();

        return storage.findAll()
                .stream()
                .filter(h ->
                        h.getNombre().toLowerCase().contains(f) ||
                                h.getApellido().toLowerCase().contains(f) ||
                                h.getDni().contains(f))
                .map(this::convertirAListadoDTO)
                .collect(Collectors.toList());
    }

    public List<HuespedListadoDTO> buscar(String id, String dni, String nombre, String apellido) {

        return storage.findAll().stream()
                .filter(h -> (id == null || id.isBlank() ||
                        String.valueOf(h.getId()).equals(id.trim())))
                .filter(h -> (dni == null || dni.isBlank() ||
                        h.getDni().equalsIgnoreCase(dni.trim())))
                .filter(h -> (nombre == null || nombre.isBlank() ||
                        h.getNombre().toLowerCase().contains(nombre.toLowerCase())))
                .filter(h -> (apellido == null || apellido.isBlank() ||
                        h.getApellido().toLowerCase().contains(apellido.toLowerCase())))
                .map(this::convertirAListadoDTO)
                .toList();
    }

    public Huesped obtenerPorId(int id) {
        if (id <= 0)
            throw new InputException("ID inválido.");
        Huesped h = storage.findById(id);
        if (h == null)
            throw new BusinessRuleException("Huésped no encontrado.");
        return h;
    }


    // ============================================================
    // BAJA LÓGICA
    // ============================================================
    public HuespedDetalleDTO darDeBaja(int id) {
        validarId(id);

        if (reservaService.poseeReservasOperativasDeHuesped(id))
            throw new BusinessRuleException("No puedes dar de baja un huésped con reservas activas.");

        Huesped h = getOrThrow(id);
        h.darDeBaja();
        storage.update(h);

        return convertirADetalleDTO(h);
    }

    public HuespedDetalleDTO activar(int id) {
        validarId(id);

        Huesped h = getOrThrow(id);
        h.activar();
        storage.update(h);

        return convertirADetalleDTO(h);
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private void validarDTO(CrearHuespedDTO dto) {
        if (dto == null) throw new InputException("Datos del huésped obligatorios.");

        if (dto.dni() == null || !dto.dni().matches("\\d{7,9}"))
            throw new InputException("DNI inválido.");

        if (dto.nombre() == null || dto.nombre().isBlank())
            throw new InputException("Nombre obligatorio.");

        if (dto.apellido() == null || dto.apellido().isBlank())
            throw new InputException("Apellido obligatorio.");

        if (dto.email() != null && !dto.email().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            throw new InputException("Formato de correo inválido.");
    }

    private void validarId(int id) {
        if (id <= 0) throw new InputException("ID inválido.");
    }

    private Huesped getOrThrow(int id) {
        Huesped h = storage.findById(id);
        if (h == null)
            throw new BusinessRuleException("Huésped no encontrado.");
        return h;
    }

    private HuespedDetalleDTO convertirADetalleDTO(Huesped h) {
        return new HuespedDetalleDTO(
                h.getId(),
                h.getDni(),
                h.getNombre(),
                h.getApellido(),
                h.getEmail(),
                h.getEstado().name()
        );
    }

    private HuespedListadoDTO convertirAListadoDTO(Huesped h) {
        return new HuespedListadoDTO(
                h.getId(),
                h.getNombre(),
                h.getApellido(),
                h.getDni(),
                h.getEmail(),
                h.getEstado().name()
        );
    }
}