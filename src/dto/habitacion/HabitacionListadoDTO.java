package dto;

public record HabitacionListadoDTO (
    int id,
    String nombre,
    String tipo,
    int capacidad,
    double precio,
    String estado
) {}
