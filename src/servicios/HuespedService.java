package servicios;

import dominio.Factura;
import dominio.Huesped;
import validaciones.ValidacionException;
import validaciones.Validator;
import almacenamiento.HuespedStorage;
import java.util.List;

public class HuespedService {

    private HuespedStorage storage;

    public HuespedService(HuespedStorage storage) {
        this.storage = storage;
    }

    public Huesped obtenerPorDni(String dni) {
        Validator.dniValido(dni);
        return storage.findByDni(dni);
    }

    public Huesped obtenerPorId(int id) {
        return storage.findById(id);
    }

    public void registrarHuesped(Huesped h) {
        if (h == null)
            throw new ValidacionException("El huésped no puede ser null.");
        Validator.dniValido(h.getDni());
        Validator.textoNoVacio(h.getNombre(), "Nombre");
        Validator.textoNoVacio(h.getApellido(), "Apellido");
        Validator.emailValido(h.getEmail());
        storage.save(h);
    }

    public void actualizar(Huesped h) {
        if (h == null)
            throw new ValidacionException("El huésped no puede ser null.");
        storage.update(h);
    }

    public void eliminar(String dni) {
        Validator.dniValido(dni);
        storage.deleteByDni(dni);
    }

    public List<Huesped> listarTodos() {
        return storage.findAll();
    }
}
