package dto;

import dominio.Habitacion.TipoHabitacion;

public record HabitacionDetalleDTO (
    int id,
    String nombre,
    TipoHabitacion tipo,
    int capacidad,
    String descripcion,
    double precio,
    String estado
) {}
