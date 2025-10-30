package src;

import src.classes.Habitacion;
import src.classes.Huespede;
import src.storage.HabitacionStorage;
import src.storage.HuespedeStorage;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        test_habitaciones_storage();
        test_huespedes_storage();
    }
    public static void test_huespedes_storage(){
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

    public static void test_habitaciones_storage(){
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