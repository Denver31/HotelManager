package src.classes;

import src.storage.HabitacionStorage;
import src.storage.HuespedeStorage;

import java.time.LocalDate;
import java.util.*;

public class Sistema {
    private HabitacionStorage habitacionStorage;
    private HuespedeStorage huespedeStorage;
    private Map<Integer, Reserva> reservas = new HashMap<>();
    private Map<Integer, Habitacion> habitaciones = new HashMap<>();
    private Map<String, Huespede> huespedes = new HashMap<>();
    private Map<Integer, Factura> facturas = new HashMap<>();
    private int maxIdHabitacion;
    private int maxIdHuespede;
    private int maxIdFactura;
    private int maxIdReserva;

    public Sistema(String habitacioneStorageAddress, String huespedeStorageAddress, String facturaStorageAddress, String reservaStorageAddress) {
        this.habitacionStorage = new HabitacionStorage(habitacioneStorageAddress);
        this.huespedeStorage = new HuespedeStorage(huespedeStorageAddress);
        reservas = new HashMap<>();
        habitaciones = habitacionStorage.getAll();
        huespedes = huespedeStorage.getAll();
        facturas = new HashMap<>();
        maxIdHabitacion = 0;
        maxIdHuespede = 0;
        maxIdFactura = 0;
        maxIdReserva = 0;
    }

    public void agregarHabitacion(Habitacion habitacion) {
        Integer id = habitacionStorage.save(habitacion);
        habitaciones.put(id, habitacion);
    }

    public void agregarHuespede(Huespede huespede) {
        if (!huespedes.containsKey(huespede.getDNI())) {
            huespedeStorage.save(huespede);
            huespedes.put(huespede.getDNI(), huespede);
        }
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

    public class Movimiento {
        String huespede;
        LocalDate fecha;

        Movimiento(String huespede, LocalDate fecha) {
            this.huespede = huespede;
            this.fecha = fecha;
        }

        public String getHuespede() {
            return huespede;
        }

        public LocalDate getFecha() {
            return fecha;
        }
    }

    public ArrayList<Movimiento> getEntradas() {
        ArrayList<Movimiento> entradas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(2);

        for (Reserva reserva : reservas.values()) {
            LocalDate fecha = reserva.getDesde();
            if (!fecha.isBefore(hoy) && !fecha.isAfter(limite)) {
                String nombre = huespedes.get(reserva.getDNIHuespede()).getNombre();
                Movimiento entrada = new Movimiento(nombre, fecha);
                entradas.add(entrada);
            }
        }

        entradas.sort(Comparator.comparing(e -> e.fecha));
        return entradas;
    }

    public ArrayList<Movimiento> getSalidas() {
        ArrayList<Movimiento> salidas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(2);

        for (Reserva reserva : reservas.values()) {
            LocalDate fecha = reserva.getHasta();
            if (!fecha.isBefore(hoy) && !fecha.isAfter(limite)) {
                String nombre = huespedes.get(reserva.getDNIHuespede()).getNombre();
                Movimiento salida = new Movimiento(nombre, fecha);
                salidas.add(salida);
            }
        }

        salidas.sort(Comparator.comparing(e -> e.fecha));
        return salidas;
    }
}
