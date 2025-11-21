package dto.reserva;

import java.time.LocalDate;

public record CrearReservaDTO(
        int idHuesped,
        int idHabitacion,
        LocalDate desde,
        LocalDate hasta,
        String metodoPago,
        int cuotas
) {}

