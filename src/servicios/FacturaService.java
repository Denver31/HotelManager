package servicios;

import dominio.Factura;
import dominio.Factura.MetodoPago;
import dominio.Reserva;
import dominio.Huesped;

import almacenamiento.FacturaStorage;

import validaciones.InputException;
import validaciones.BusinessRuleException;

import dto.FacturaDetalleDTO;
import dto.FacturaListadoDTO;

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
    // Crear factura (usado por CrearReservaPresenter)
    // ============================================================
    public Factura crearFacturaReserva(double total, MetodoPago metodo, int cuotas) {
        LocalDate vencimiento = LocalDate.now().plusDays(2);
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
    }

    // ============================================================
    // Cancelación manual
    // ============================================================
    public void cancelarFacturaAsociada(int idFactura) {
        Factura f = obtenerPorId(idFactura);
        f.cancelar();
        storage.update(f);
    }

    // ============================================================
    // Obtener por ID (solo helper interno)
    // ============================================================
    private Factura obtenerPorId(int id) {
        if (id <= 0) throw new InputException("ID inválido.");

        Factura f = storage.findById(id);
        if (f == null)
            throw new BusinessRuleException("Factura no encontrada.");

        return f;
    }

    // ============================================================
    // Mappers (necesarios para todos los listados y detalle)
    // ============================================================
    private FacturaListadoDTO mapToListadoDTO(Factura f) {
        Reserva r = reservaService.obtenerPorFactura(f.getId());
        Huesped h = reservaService.obtenerHuespedPorFactura(f.getId());
        return toListadoDTO(f, r, h);
    }

    private FacturaListadoDTO toListadoDTO(Factura f, Reserva r, Huesped h) {
        return new FacturaListadoDTO(
                f.getId(),
                h.getId(),
                h.getNombreCompleto(),
                r.getId(),
                f.getFechaAlta().toString(),
                f.getVencimiento().toString(),
                f.getEstado().name(),
                f.getTotal()
        );
    }

    private FacturaDetalleDTO toDetalleDTO(Factura f, Reserva r, Huesped h) {
        return new FacturaDetalleDTO(
                f.getId(),
                r.getId(),
                h.getId(),
                h.getNombreCompleto(),
                f.getFechaAlta().toString(),
                f.getVencimiento().toString(),
                f.getEstado().name(),
                String.valueOf(f.getTotal()),
                f.getMetodo().name(),
                f.getCuotas(),
                f.getFechaPago() != null ? f.getFechaPago().toString() : "-"
        );
    }

    // ============================================================
    // Listado
    // ============================================================
    public List<FacturaListadoDTO> listarFacturas() {
        return storage.findAll().stream()
                .map(this::mapToListadoDTO)
                .toList();
    }

    // ============================================================
    // Búsqueda
    // ============================================================
    public List<FacturaListadoDTO> buscarFacturas(String filtro) {
        if (filtro == null || filtro.isBlank()) return listarFacturas();

        String f = filtro.trim().toLowerCase();

        return storage.findAll().stream()
                .filter(factura -> String.valueOf(factura.getId()).contains(f))
                .map(this::mapToListadoDTO)
                .filter(dto ->
                        String.valueOf(dto.idFactura()).contains(f) ||
                                String.valueOf(dto.idReserva()).contains(f) ||
                                String.valueOf(dto.idHuesped()).contains(f) ||
                                dto.nombreCompleto().toLowerCase().contains(f)
                )
                .toList();
    }

    // ============================================================
    // Detalle
    // ============================================================
    public FacturaDetalleDTO obtenerDetalleFactura(int idFactura) { //ok
        Factura f = obtenerPorId(idFactura);
        Reserva r = reservaService.obtenerPorFactura(idFactura);
        Huesped h = reservaService.obtenerHuespedPorFactura(idFactura);
        return toDetalleDTO(f, r, h);
    }

    // ============================================================
    // Facturas vencidas (PanelControl)
    // ============================================================
    public List<FacturaListadoDTO> buscarFacturasVencidasUI() {
        return storage.findAll().stream()
                .filter(Factura::estaVencida)
                .map(this::mapToListadoDTO)
                .toList();
    }
}