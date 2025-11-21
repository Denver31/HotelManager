package dto;

public record HuespedDetalleDTO(
        int id,
        String dni,
        String nombre,
        String apellido,
        String email,
        String estado
) {}
