package src.Interface;

import src.classes.Factura;
import src.classes.Sistema;
import src.classes.Habitacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SeleccionarFacturaDialog extends JDialog {

    private JTable table;
    private FacturasTableModel tableModel;
    private Integer selectedHabitacionId = null;

    public SeleccionarFacturaDialog(Window owner, Sistema sistema) {
        super(owner, "Seleccionar factura", ModalityType.APPLICATION_MODAL);
        setSize(640, 420);
        setLocationRelativeTo(owner);

        Color fondo = new Color(245, 247, 250);
        Color acento = new Color(0, 120, 215);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setBackground(fondo);

        JLabel title = new JLabel("🛏️ Seleccioná una factura");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(acento);
        content.add(title, BorderLayout.NORTH);

        tableModel = new FacturasTableModel(sistema);
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<FacturasTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.naturalOrder()); // ID
        sorter.setComparator(2, Comparator.comparingDouble(a -> (Double) a)); // Precio
        sorter.setComparator(3, Comparator.naturalOrder()); // Capacidad
        table.setRowSorter(sorter);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    confirmarSeleccion();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        content.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(fondo);

        JButton cancel = new JButton("Cancelar");
        JButton select = new JButton("Seleccionar");
        select.setBackground(acento);
        select.setForeground(Color.WHITE);
        select.setFocusPainted(false);

        cancel.addActionListener(e -> dispose());
        select.addActionListener(e -> confirmarSeleccion());

        buttons.add(cancel);
        buttons.add(select);
        content.add(buttons, BorderLayout.SOUTH);

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Aún no hay pagas creadas.\nAgregá un pago antes de seleccionar.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
        }

        setContentPane(content);
    }

    private void confirmarSeleccion() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná uno pago de la lista.",
                    "Nada seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        selectedHabitacionId = tableModel.getIdAt(modelRow);
        dispose();
    }

    public Integer getSelectedFacturaId() {
        return selectedHabitacionId;
    }

    private static class FacturasTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Total", "Pagado", "Hasta"};
        private final List<Fila> data = new ArrayList<>();

        private static class Fila {
            int id;
            double total;
            double pagado;
            LocalDate hasta;

            Fila(int id, double total, double pagado, LocalDate hasta) {
                this.id = id;
                this.total = total;
                this.pagado = pagado;
                this.hasta = hasta;
            }
        }

        FacturasTableModel(Sistema sistema) {
            for (Map.Entry<Integer, Factura> e : sistema.getFacturas().entrySet()) {
                Integer id = e.getKey();
                Factura f = e.getValue();
                if (!f.esPagado()) {
                    data.add(new Fila(id, f.getTotal(), f.getPagado(), f.getPagarHasta()));
                }
            }
            data.sort(Comparator.comparingInt(o -> o.id));
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int column) {
            return cols[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Integer.class;
                case 1:
                    return String.class;
                case 2:
                    return Double.class;
                case 3:
                    return Integer.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Fila f = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return f.id;
                case 1:
                    return f.total;
                case 2:
                    return f.pagado;
                case 3:
                    return f.hasta;
                default:
                    return null;
            }
        }

        public Integer getIdAt(int modelRow) {
            return data.get(modelRow).id;
        }

        private String safe(String s) {
            return s == null ? "" : s;
        }
    }
}
