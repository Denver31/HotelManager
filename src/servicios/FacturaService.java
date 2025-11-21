package servicios;

import dominio.Factura;
import dominio.Factura.MetodoPago;
import almacenamiento.FacturaStorage;
import dominio.Reserva;
import dominio.Huesped;
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
    // Crear factura
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
    public void registrarPago(int idFactura) { //ok
        Factura f = obtenerPorId(idFactura);

        f.registrarPago();
        storage.update(f);

        reservaService.confirmarReservasConFactura(f.getId());
    }

    // ============================================================
    // Cancelación manual
    // ============================================================
    public void cancelarFacturaAsociada(int idFactura) { //ok
        Factura f = obtenerPorId(idFactura);
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

            if (f.estaVencida())
                reservaService.cancelarReservaPorFacturaVencida(f.getId());
        }
    }

    // ============================================================
    // Consultas básicas
    // ============================================================
    public Factura obtenerPorId(int id) { //ok
        if (id <= 0) throw new InputException("ID inválido.");

        Factura f = storage.findById(id);
        if (f == null)
            throw new BusinessRuleException("Factura no encontrada.");

        return f;
    }

    // ============================================================
    // Mappers centralizados
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
    // Búsqueda optimizada
    // ============================================================
    public List<FacturaListadoDTO> buscarFacturas(String filtro) { //ok
        if (filtro == null || filtro.isBlank()) {
            return listarFacturas();
        }

        String f = filtro.trim().toLowerCase();

        return storage.findAll().stream()
                // Filtrar primero por criterio barato (ID factura)
                .filter(factura ->
                        String.valueOf(factura.getId()).toLowerCase().contains(f)
                )
                // Luego mapear solo las facturas que pasan el filtro inicial
                .map(this::mapToListadoDTO)
                // Finalmente filtrar por datos derivados
                .filter(dto ->
                        String.valueOf(dto.idFactura()).contains(f) ||
                                String.valueOf(dto.idReserva()).contains(f) ||
                                String.valueOf(dto.idHuesped()).contains(f) ||
                                dto.nombreCompleto().toLowerCase().contains(f)
                )
                .toList();
    }

    // ============================================================
    // Listado completo
    // ============================================================
    public List<FacturaListadoDTO> listarFacturas() { //ok
        return storage.findAll().stream()
                .map(this::mapToListadoDTO)
                .toList();
    }

    // ============================================================
    // Detalle para paneles
    // ============================================================
    public FacturaDetalleDTO obtenerDetalleFactura(int idFactura) { //ok
        Factura f = obtenerPorId(idFactura);
        Reserva r = reservaService.obtenerPorFactura(idFactura);
        Huesped h = reservaService.obtenerHuespedPorFactura(idFactura);
        return toDetalleDTO(f, r, h);
    }

    public List<FacturaListadoDTO> buscarFacturasVencidasUI() {

        return storage.findAll().stream()
                .filter(Factura::estaVencida)
                .map(this::mapToListadoDTO)
                .toList();
    }


    public void update(Factura f) {
        storage.update(f);
    }

}