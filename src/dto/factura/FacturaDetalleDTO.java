package dto;

public record FacturaDetalleDTO(
        int idFactura,
        int idReserva,
        int idHuesped,
        String nombreCompleto,
        String fechaAlta,
        String fechaVencimiento,
        String estado,
        String total,
        String metodoPago,
        int cuotas,
        String fechaPago
) {}