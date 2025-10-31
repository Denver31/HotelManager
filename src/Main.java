package src;

import src.classes.Factura;
import src.classes.Habitacion;
import src.classes.Huespede;
import src.classes.Reserva;
import src.storage.FacturaStorage;
import src.storage.HabitacionStorage;
import src.storage.HuespedeStorage;
import src.storage.ReservaStorage;

import java.time.LocalDate;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        test_habitaciones_storage();
//        test_huespedes_storage();
//        test_facturas_storage();
        test_reservas_storage();
    }
    public static void test_reservas_storage() {
        ReservaStorage storage = new ReservaStorage("hotel.db");
        Reserva r1 = new Reserva(1,"111111111",1,LocalDate.now(),LocalDate.now());

        storage.save(r1);
        Map<Integer, Reserva> reservas = storage.getAll();

        for (Integer id : reservas.keySet()) {
            Reserva r = reservas.get(id);
            System.out.println(id + " | " + r.getIdFactura() + r.getIdHabitacion());
        }
        r1 = reservas.get(1);
        if (r1 != null) {
            System.out.println("id 1: " + r1.getDNIHuespede());
        }
    }

    public static void test_facturas_storage() {
        FacturaStorage storage = new FacturaStorage("hotel.db");
        Factura f1 = new Factura(
                100,
                100,
                Factura.tipoDePago.TOTAL,
                Factura.metodoDePago.TARJETA,
                1,
                LocalDate.now()
        );

        storage.save(f1);
        Map<Integer, Factura> facturas = storage.getAll();

        for (Integer id : facturas.keySet()) {
            Factura f = facturas.get(id);
            System.out.println(id + " | " + f.getTotal() + f.getMetodo() + f.getPagarHasta());
        }
        f1 = facturas.get(1);
        if (f1 != null) {
            System.out.println("id 1: " + f1.getTotal());
        }
    }

    public static void test_huespedes_storage() {
        HuespedeStorage storage = new HuespedeStorage("hotel.db");
        Huespede h1 = new Huespede(
                "Daniil Geevich",
                "111111111");

        storage.save(h1);
        Map<String, Huespede> huespedes = storage.getAll();

        for (String dni : huespedes.keySet()) {
            Huespede h = huespedes.get(dni);
            System.out.println(dni + " | " + h.getNombre());
        }
        h1 = huespedes.get("12343211");
        if (h1 != null) {
            System.out.println("dni 12343211: " + h1.getNombre());
        }
    }

    public static void test_habitaciones_storage() {
        HabitacionStorage storage = new HabitacionStorage("hotel.db");
        Habitacion h1 = new Habitacion(
                "Test Arsenii Huilo Deluxe",
                "<3",
                1.0,
                Habitacion.tipoHabitacion.INDIVIDUAL
        );

        storage.save(h1);
        Map<Integer, Habitacion> habitaciones = storage.getAll();

        // Пример доступа по id
        for (Integer id : habitaciones.keySet()) {
            Habitacion h = habitaciones.get(id);
            System.out.println(id + " | " + h.getNombre() + " | " + h.getCapacidad());
        }

        // Доступ к конкретной комнате по id
        h1 = habitaciones.get(1);
        if (h1 != null) {
            System.out.println("Комната с id=1: " + h1.getNombre());
        }
    }
}