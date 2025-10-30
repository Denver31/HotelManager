package src.classes;

import java.time.LocalDate;

public class Reserva {

    private int idHabitacion;
    private int idHuespede;
    private int idFactura;
    private LocalDate desde;
    private LocalDate hasta;

    public Reserva(int idHabitacion, int idHuespede, int idFactura, LocalDate desde, LocalDate hasta) {
        this.idHabitacion = idHabitacion;
        this.idHuespede = idHuespede;
        this.idFactura = idFactura;
        this.desde = desde;
        this.hasta = hasta;
    }

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public int getIdHuespede() {
        return idHuespede;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }
}
