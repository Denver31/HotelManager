import aplicacion.Sistema;
import dominio.Factura.MetodoPago;
import dominio.Reserva;
import almacenamiento.DatabaseManager;
import java.time.LocalDate;
import validaciones.ValidacionException;
import dominio.Habitacion;
import aplicacion.Sistema;

public class Main {

    public static void main(String[] args) {

        Sistema sistema = new Sistema();

        sistema.listarTodasLasHabitaciones();
        sistema.listarTodasLasReservasDebug();

        LocalDate desde = LocalDate.of(2025, 11, 7);
        LocalDate hasta = LocalDate.of(2025, 11, 10);

        System.out.println("\n🔍 Habitaciones disponibles del " + desde + " al " + hasta + ":");
        var disponibles = sistema.listarHabitacionesDisponibles(desde, hasta);
        for (Habitacion h : disponibles) {
            System.out.printf(" - ID: %d | %s%n", h.getId(), h.getNombre());
        }

        System.out.println("🔍 DEBUG RESERVAS COMPLETAS:");
        sistema.listarTodasLasReservasDebug();


        System.out.println("\nTotal disponibles: " + disponibles.size());
    }
}