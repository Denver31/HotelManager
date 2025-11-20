package servicios;

import dominio.Factura;
import dominio.Factura.MetodoPago;
import validaciones.InputException;
import validaciones.BusinessRuleException;
import almacenamiento.FacturaStorage;

import java.time.LocalDate;
import java.util.List;

public class FacturaService {

    private final FacturaStorage storage;
    private final ReservaService reservaService;

    public FacturaService(FacturaStorage storage, ReservaService reservaService) {
        if (storage == null) throw new InputException("FacturaStorage obligatorio.");
        if (reservaService == null) throw new InputException("ReservaService obligatorio.");
        this.storage = storage;
        this.reservaService = reservaService;
    }

    // ============================================================
    // Crear factura
    // ============================================================
    public Factura crearFactura(double total, MetodoPago metodo, int cuotas, LocalDate vencimiento) {

        Factura f = new Factura(total, metodo, cuotas, vencimiento);
        storage.save(f);
        return f;
    }

    public Factura crearFacturaReserva(double total, MetodoPago metodo, int cuotas) {
        LocalDate vencimiento = LocalDate.now().plusDays(15);
        Factura f = new Factura(total, metodo, cuotas, vencimiento);
        storage.save(f);
        return f;
    }

    // ============================================================
    // Pago
    // ============================================================
    public void registrarPago(int idFactura) {
        Factura f = obtenerPorId(idFactura);

        f.registrarPago();
        storage.update(f);

        // Si paga la factura de una reserva pendiente → confirmar reserva
        reservaService.confirmarReservasConFactura(f.getId());
    }

    // ============================================================
    // Cancelación manual (llamada desde ReservaService)
    // ============================================================
    public void cancelarFacturaAsociada(int idFactura) {
        Factura f = obtenerPorId(idFactura);
        if (f == null) return;

        f.cancelar();
        storage.update(f);
    }

    // ============================================================
    // Vencimientos automáticos
    // ============================================================
    public void actualizarFacturasVencidas() {
        for (Factura f : storage.findPendientesHasta(LocalDate.now())) {
            f.evaluarVencimiento();
            storage.update(f);

            // Si factura vence → cancelar reserva asociada
            if (f.estaVencida())
                reservaService.cancelarReservaPorFacturaVencida(f.getId());
        }
    }

    // ============================================================
    // Consultas
    // ============================================================
    public Factura obtenerPorId(int id) {
        if (id <= 0) throw new InputException("ID inválido.");
        Factura f = storage.findById(id);
        if (f == null) throw new BusinessRuleException("Factura no encontrada.");
        return f;
    }

    public List<Factura> listarTodas() {
        return storage.findAll();
    }
}