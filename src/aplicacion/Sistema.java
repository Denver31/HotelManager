package aplicacion;

import almacenamiento.*;
import servicios.*;
import dominio.*;
import java.sql.Connection;

public class Sistema {

    private final Connection conn;

    // Storages
    private final HabitacionStorage habitacionStorage;
    private final HuespedStorage huespedStorage;
    private final FacturaStorage facturaStorage;
    private final ReservaStorage reservaStorage;

    // Services
    public final HabitacionService habitacionService;
    public final HuespedService huespedService;
    public final FacturaService facturaService;
    public final ReservaService reservaService;

    public Sistema() {

        // 1. Conexión a la DB (única conexión)
        conn = DatabaseManager.getConnection();

        // 2. Inicializar Storages
        habitacionStorage = new HabitacionStorage(conn);
        huespedStorage = new HuespedStorage(conn);
        facturaStorage = new FacturaStorage(conn);

        reservaStorage = new ReservaStorage(
                conn,
                habitacionStorage,
                huespedStorage,
                facturaStorage
        );

        // 3. Inicializar Services
        reservaService = new ReservaService(reservaStorage);
        facturaService = new FacturaService(facturaStorage, reservaService);
        habitacionService = new HabitacionService(habitacionStorage, reservaService);
        huespedService = new HuespedService(huespedStorage, reservaService);

        // 4. Seeder (opcional)
        new DatabaseSeeder(
                habitacionService,
                huespedService,
                reservaService,
                facturaService
        ).seed();
    }

    // ============================================================
    // GETTERS (si los querés usar desde presenters externos)
    // ============================================================
    public HabitacionService getHabitacionService() { return habitacionService; }
    public HuespedService getHuespedService() { return huespedService; }
    public FacturaService getFacturaService() { return facturaService; }
    public ReservaService getReservaService() { return reservaService; }
}

