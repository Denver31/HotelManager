package dto;

public record FacturaListadoDTO(
        int idFactura,
        int idHuesped,
        String nombreCompleto,
        int idReserva,
        String fechaAlta,
        String fechaVencimiento,
        String estado,
        double monto
) {}




