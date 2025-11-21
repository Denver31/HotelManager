package dto;

import dominio.Habitacion.TipoHabitacion;
public record CrearHabitacionDTO(
        String nombre,
        String descripcion,
        double precio,
        dominio.Habitacion.TipoHabitacion tipo,
        int capacidad
) {}
