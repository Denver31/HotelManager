package aplicacion;

import dominio.Reserva;
import dominio.Factura;
import aplicacion.Sistema;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FacturasPendientesTableModel extends AbstractTableModel {

    private final String[] columnas = {"ID", "Cliente", "Habitación", "Total", "Estado", "Vence"};
    private final List<Reserva> reservas;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FacturasPendientesTableModel(Sistema sistema) {
        this.reservas = sistema.getReservasPendientesDeCobro(java.time.LocalDate.now());
    }

    @Override
    public int getRowCount() {
        return reservas.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnas[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        Reserva r = reservas.get(rowIndex);
        Factura f = r.getFactura();

        return switch (colIndex) {
            case 0 -> r.getId();
            case 1 -> r.getHuesped().getNombre() + " " + r.getHuesped().getApellido();
            case 2 -> r.getHabitacion().getNombre();
            case 3 -> String.format("$ %.2f", f.getTotal());
            case 4 -> f.isPagada() ? "Pagada" : r.getEstado().name(); // “Pendiente” o “Cancelada”
            case 5 -> f.getVencimiento().format(fmt);
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
