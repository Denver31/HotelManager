package servicios;
import dominio.Habitacion;
import dominio.Factura;
import dominio.Factura.MetodoPago;
import validaciones.ValidacionException;
import validaciones.Validator;
import almacenamiento.FacturaStorage;
import java.time.LocalDate;
import java.util.List;

public class FacturaService {

    private FacturaStorage storage;

    public FacturaService(FacturaStorage storage) {
        this.storage = storage;
    }

    public Factura crearFactura(double total, MetodoPago metodo, int cuotas, LocalDate venc) {
        Validator.numeroPositivo(total, "Total factura");
        if (metodo == null)
            throw new ValidacionException("Debe especificar el método de pago.");
        if (venc == null)
            throw new ValidacionException("Debe especificar la fecha de vencimiento.");
        if (cuotas < 1 || cuotas > 3)
            throw new ValidacionException("Las cuotas deben estar entre 1 y 3.");
        Factura factura = new Factura(total, metodo, cuotas, venc);
        storage.save(factura);
        return factura;
    }

    public Factura crearFactura(Habitacion habitacion, LocalDate desde, LocalDate hasta, MetodoPago metodo, int cuotas) {
        double total = habitacion.calcularPrecio(desde, hasta);
        LocalDate venc = LocalDate.now().plusDays(15);
        return crearFactura(total, metodo, cuotas, venc);
    }


    public void registrarPago(int id) {
        Factura factura = storage.findById(id);
        if (factura == null)
            throw new ValidacionException("No existe factura con el ID especificado.");
        if (factura.getVencimiento().isBefore(LocalDate.now())) {
            System.out.println("Advertencia: factura vencida. Pago fuera de término.");
        }
        if (factura.isPagada()) return;
        factura.registrarPago();
        storage.update(factura);

    }


    public Factura obtenerPorId(int id) {
        return storage.findById(id);
    }

    public List<Factura> obtenerPendientesHasta(LocalDate fecha) {
        return storage.findPendientesHasta(fecha);
    }

    public List<Factura> listarTodas() {
        return storage.findAll();
    }
}
