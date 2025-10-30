package src.classes;

import java.time.LocalDate;

public class Reserva {

    private int idHabitacion;
    private String dniHuespede;
    private int idFactura;
    private LocalDate desde;
    private LocalDate hasta;

    public Reserva(int idHabitacion, String dniHuespede, int idFactura, LocalDate desde, LocalDate hasta) {
        this.idHabitacion = idHabitacion;
        this.dniHuespede = dniHuespede;
        this.idFactura = idFactura;
        this.desde = desde;
        this.hasta = hasta;
    }

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public String getDNIHuespede() {
        return dniHuespede;
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
