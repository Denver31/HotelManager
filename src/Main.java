package src;

import src.classes.Habitacion;
import src.storage.HabitacionStorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        HabitacionStorage storage = new HabitacionStorage("hotel.db");
//        Habitacion h1 = new Habitacion(
//                "Suite Deluxe",
//                "Vista al mar y jacuzzi",
//                120.0,
//                Habitacion.tipoHabitacion.MATRIMONIAL
//        );
//
//        storage.save(h1);
//
        HabitacionStorage storage = new HabitacionStorage("hotel.db");

        Map<Integer, Habitacion> habitaciones = storage.getAll();

        // Пример доступа по id
        for (Integer id : habitaciones.keySet()) {
            Habitacion h = habitaciones.get(id);
            System.out.println(id + " | " + h.getNombre() + " | " + h.getCapacidad());
        }

        // Доступ к конкретной комнате по id
        Habitacion h1 = habitaciones.get(1);
        if (h1 != null) {
            System.out.println("Комната с id=1: " + h1.getNombre());
        }

    }
}