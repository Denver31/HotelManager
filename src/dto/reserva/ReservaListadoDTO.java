package dto.reserva;

import java.time.LocalDate;

public record ReservaListadoDTO(
        int idReserva,
        int idHuesped,
        String nombreCompletoHuesped,
        int idHabitacion,
        String nombreHabitacion,
        LocalDate desde,
        LocalDate hasta,
        String estado
) {}

