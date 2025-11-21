package dto.reserva;

import java.time.LocalDate;

public record ReservaDetalleDTO(
        int idReserva,
        int idHabitacion,
        int idHuesped,
        int idFactura,
        String nombreCompletoHuesped,
        String nombreHabitacion,
        LocalDate desde,
        LocalDate hasta,
        String estado,
        double totalFactura
) {}


