package servicios;

import dominio.Habitacion;
import validaciones.ValidacionException;
import validaciones.Validator;
import almacenamiento.HabitacionStorage;
import java.time.LocalDate;
import java.util.List;

public class HabitacionService {

    private HabitacionStorage storage;

    public HabitacionService(HabitacionStorage storage) {
        this.storage = storage;
    }

    public Habitacion obtenerPorId(int id) {
        return storage.findById(id);
    }

    public List<Habitacion> buscarDisponibles(LocalDate desde, LocalDate hasta) {
        Validator.rangoFechas(desde, hasta);
        return storage.findDisponibles(desde, hasta);
    }

    public void crearHabitacion(Habitacion h) {
        if (h == null)
            throw new ValidacionException("La habitación no puede ser null.");
        Validator.textoNoVacio(h.getNombre(), "Nombre de habitación");
        Validator.numeroPositivo(h.getPrecio(), "Precio");
        Validator.numeroPositivo(h.getCapacidad(), "Capacidad");
        storage.save(h);
    }

    public void actualizar(Habitacion h) {
        if (h == null)
            throw new ValidacionException("La habitación no puede ser null.");
        storage.update(h);
    }

    public void eliminar(int id) {
        storage.delete(id);
    }

    public List<Habitacion> obtenerTodas() {
        return storage.findAll();
    }
}
