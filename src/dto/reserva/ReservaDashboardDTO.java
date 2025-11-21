package dto.reserva;

import java.time.LocalDate;

public record ReservaDashboardDTO(
        LocalDate fecha,
        int idReserva,
        String nombreCompleto,
        int idHabitacion,
        Integer idFactura,
        Double monto
) {}

