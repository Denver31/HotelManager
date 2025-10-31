package src.Interface;

import src.classes.Factura;
import src.classes.Sistema;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FacturasPendientesTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "Total", "Pagado", "Restante", "Pagar hasta"};
    private final List<Fila> data;

    private static class Fila {
        int id;
        Factura f;
        Fila(int id, Factura f) { this.id = id; this.f = f; }
    }

    private final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;

    public FacturasPendientesTableModel(Sistema sistema) {
        Map<Integer, Factura> facturas = sistema.getFacturas();

        this.data = facturas.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().esPagado())
                .sorted(Comparator.comparing(e -> e.getValue().getPagarHasta()))
                .map(e -> new Fila(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int column) { return cols[column]; }

    @Override public Class<?> getColumnClass(int c) {
        switch (c) {
            case 0: return Integer.class; // ID
            case 1: // Total
            case 2: // Pagado
            case 3: return Double.class;  // Restante
            case 4: return String.class;  // Pagar hasta (как текст YYYY-MM-DD)
            default: return Object.class;
        }
    }

    @Override public Object getValueAt(int row, int col) {
        Fila r = data.get(row);
        switch (col) {
            case 0: return r.id;
            case 1: return r.f.getTotal();
            case 2: return r.f.getPagado();
            case 3: return Math.max(0.0, r.f.getTotal() - r.f.getPagado());
            case 4: return r.f.getPagarHasta().format(df);
            case 5: return r.f.esExpirado();
            default: return null;
        }
    }

    public Integer getFacturaIdAt(int modelRow) { return data.get(modelRow).id; }
}
