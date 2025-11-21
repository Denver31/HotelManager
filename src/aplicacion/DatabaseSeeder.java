package aplicacion;

import dominio.Habitacion;
import servicios.*;
import dto.CrearHabitacionDTO;
import dto.CrearHuespedDTO;
import dto.reserva.CrearReservaDTO;

import java.time.LocalDate;

public class DatabaseSeeder {

    private final HabitacionService habitacionService;
    private final HuespedService huespedService;
    private final ReservaService reservaService;
    private final FacturaService facturaService;

    public DatabaseSeeder(
            HabitacionService habitacionService,
            HuespedService huespedService,
            ReservaService reservaService,
            FacturaService facturaService
    ) {
        this.habitacionService = habitacionService;
        this.huespedService = huespedService;
        this.reservaService = reservaService;
        this.facturaService = facturaService;
    }

    public void seed() {

        seedHabitaciones();
        seedHuespedes();
        seedReservasIniciales();  // opcional
    }

    // ============================================================
    // HABITACIONES
    // ============================================================
    private void seedHabitaciones() {
        if (!habitacionService.obtenerListado().isEmpty()) return;

        CrearHabitacionDTO[] habs = new CrearHabitacionDTO[]{
                new CrearHabitacionDTO("Suite 101", "Suite doble con vista al parque", 25000, Habitacion.TipoHabitacion.COMPARTIDA, 2),
                new CrearHabitacionDTO("Suite 102", "Suite simple con escritorio", 18000, Habitacion.TipoHabitacion.INDIVIDUAL, 1),
                new CrearHabitacionDTO("Suite 103", "Suite familiar de 4 plazas", 36000, Habitacion.TipoHabitacion.COMPARTIDA, 4),
                new CrearHabitacionDTO("Suite 104", "Vista al mar, cama king", 42000, Habitacion.TipoHabitacion.MATRIMONIAL, 2),
                new CrearHabitacionDTO("Deluxe 201", "Habitación deluxe moderna", 30000, Habitacion.TipoHabitacion.COMPARTIDA, 2),
                new CrearHabitacionDTO("Deluxe 202", "Habitación lujo con jacuzzi", 45000, Habitacion.TipoHabitacion.MATRIMONIAL, 2),
                new CrearHabitacionDTO("Económica 301", "Habitación simple económica", 12000, Habitacion.TipoHabitacion.INDIVIDUAL, 1),
                new CrearHabitacionDTO("Económica 302", "Ideal para viajeros solos", 13000, Habitacion.TipoHabitacion.INDIVIDUAL, 1),
                new CrearHabitacionDTO("Familiar 401", "Ambiente amplio y luminoso", 35000, Habitacion.TipoHabitacion.COMPARTIDA, 4),
                new CrearHabitacionDTO("Familiar 402", "Con balcón y cocina pequeña", 38000, Habitacion.TipoHabitacion.COMPARTIDA, 4)
        };

        for (CrearHabitacionDTO dto : habs)
            habitacionService.crearHabitacion(dto);
    }

    // ============================================================
    // HUESPEDES
    // ============================================================
    private void seedHuespedes() {
        if (!huespedService.obtenerListado().isEmpty()) return;

        CrearHuespedDTO[] huespedes = new CrearHuespedDTO[]{
                new CrearHuespedDTO("12345678", "Juan", "Pérez", "juan@test.com"),
                new CrearHuespedDTO("22333444", "María", "Gómez", "maria@test.com"),
                new CrearHuespedDTO("33444555", "Pedro", "López", "pedro@test.com"),
                new CrearHuespedDTO("44555666", "Ana", "Martínez", "ana@test.com"),
                new CrearHuespedDTO("55666777", "Carlos", "Soria", "carlos@test.com"),
                new CrearHuespedDTO("66777888", "Lucía", "Rivas", "lucia@test.com"),
                new CrearHuespedDTO("77888999", "Diego", "Torres", "diego@test.com"),
                new CrearHuespedDTO("88999000", "Sofía", "Méndez", "sofia@test.com"),
                new CrearHuespedDTO("99000111", "Miguel", "Álvarez", "miguel@test.com"),
                new CrearHuespedDTO("10111222", "Paula", "Flores", "paula@test.com")
        };

        for (CrearHuespedDTO dto : huespedes)
            huespedService.crearHuesped(dto);
    }

    // ============================================================
    // RESERVAS (OPCIONAL)
    // ============================================================
    private void seedReservasIniciales() {

        if (!reservaService.listarTodas().isEmpty()) return;

        // Crear una reserva de ejemplo
        try {
            CrearReservaDTO r = new CrearReservaDTO(
                    1,  // idHuesped
                    1,  // idHabitacion
                    LocalDate.now(),
                    LocalDate.now().plusDays(2),
                    "TRANSFERENCIA",
                    1
            );

            reservaService.crearReservaDesdeUI(
                    r,
                    huespedService,
                    habitacionService,
                    facturaService
            );

        } catch (Exception e) {
            System.out.println("Error creando reserva inicial: " + e.getMessage());
        }
    }
}
