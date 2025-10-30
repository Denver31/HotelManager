package src.classes;

import java.time.LocalDate;
import java.util.*;

public class Sistema {
    private Map<Integer, Reserva> reservas = new HashMap<>();
    private Map<Integer, Habitacion> habitaciones = new HashMap<>();
    private Map<String, Huespede> huespedes = new HashMap<>();
    private Map<Integer, Factura> facturas = new HashMap<>();
    private int maxIdHabitacion;
    private int maxIdHuespede;
    private int maxIdFactura;
    private int maxIdReserva;

    public Sistema(String habitacionesFile, String huespedeFile, String facturaFile,
                   String idHabitacionesFile, String idHuespedeFile, String idFacturaFile) {
        reservas = new HashMap<>();
        habitaciones = new HashMap<>();
        huespedes = new HashMap<>();
        facturas = new HashMap<>();
        maxIdHabitacion = 0;
        maxIdHuespede = 0;
        maxIdFactura = 0;
        maxIdReserva = 0;
    }

    public void agregarHabitacion(Habitacion habitacion) {
        maxIdHabitacion++;
        habitaciones.put(maxIdHabitacion, habitacion);
    }

    public void agregarHuespede(Huespede huespede) {
        huespedes.put(huespede.getDNI(), huespede);
    }

    private void agregarFactura(Factura factura) {
        maxIdFactura++;
        facturas.put(maxIdFactura, factura);
    }

    private void borrarFactura(int idFactura) {
        facturas.remove(idFactura);
    }

    public void agregarReserva(int idHabitacion, String dniHuespede, Factura factura,
                               java.time.LocalDate desde, java.time.LocalDate hasta) {
        maxIdReserva++;
        agregarFactura(factura);
        Reserva reserva = new Reserva(idHabitacion, dniHuespede, maxIdFactura, desde, hasta);
        reservas.put(maxIdReserva, reserva);
    }

    public void borrarReserva(int idReserva) {
        Reserva res = reservas.get(idReserva);
        borrarFactura(res.getIdFactura());
        reservas.remove(idReserva);
    }

    public Habitacion getHabitacionById(int idHabitacion) {
        return habitaciones.get(idHabitacion);
    }

    public Map<Integer, Habitacion> getHabitaciones() {
        return Collections.unmodifiableMap(habitaciones);
    }

    public Map<Integer, Factura> getFacturas() {
        return Collections.unmodifiableMap(facturas);
    }
}
