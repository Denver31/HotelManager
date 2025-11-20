package servicios;

import almacenamiento.HuespedStorage;
import dominio.Huesped;
import validaciones.InputException;
import validaciones.BusinessRuleException;

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
    // Consultas
    // ============================================================
    public Huesped obtenerPorDni(String dni) {
        if (dni == null || !dni.matches("\\d{7,9}")) {
            throw new InputException("El DNI ingresado no es válido.");
        }
        return storage.findByDni(dni);
    }

    public Huesped obtenerPorId(int id) {
        if (id <= 0) throw new InputException("ID inválido.");
        return storage.findById(id);
    }

    public List<Huesped> listarTodos() {
        return storage.findAll();
    }

    public List<Huesped> buscarPorNombreApellido(String filtro) {
        if (filtro == null || filtro.isBlank()) return listarTodos();
        String f = filtro.toLowerCase();

        return listarTodos().stream()
                .filter(h -> h.getNombreCompleto().toLowerCase().contains(f) ||
                        h.getDni().contains(f))
                .collect(Collectors.toList());
    }

    // ============================================================
    // Crear / Actualizar
    // ============================================================
    public void registrarHuesped(Huesped h) {
        if (h == null) throw new InputException("Huésped inválido.");

        if (h.getDni() == null || !h.getDni().matches("\\d{7,9}")) {
            throw new InputException("El DNI ingresado no es válido.");
        }
        if (h.getEmail() == null || !h.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InputException("El formato del correo electrónico no es válido.");
        }

        if (storage.findByDni(h.getDni()) != null)
            throw new BusinessRuleException("Ya existe un huésped con ese DNI.");

        storage.save(h);
    }

    public void actualizar(int id, String nombre, String apellido, String email) {
        Huesped h = obtenerPorId(id);
        if (h == null) throw new BusinessRuleException("Huésped no encontrado.");

        h.modificarDatos(nombre, apellido, email);
        storage.update(h);
    }

    // ============================================================
    // Baja lógica
    // ============================================================
    public void darDeBaja(int id) {
        Huesped h = obtenerPorId(id);
        if (h == null) throw new BusinessRuleException("Huésped no encontrado.");

        // validar que no tenga reservas operativas
        if (reservaService.poseeReservasOperativasDeHuesped(h.getDni()))
            throw new BusinessRuleException("No puedes dar de baja un huésped con reservas activas.");

        h.darDeBaja();
        storage.update(h);
    }

    public void activar(int id) {
        Huesped h = obtenerPorId(id);
        if (h == null) throw new BusinessRuleException("Huésped no encontrado.");

        h.activar();
        storage.update(h);
    }
}
