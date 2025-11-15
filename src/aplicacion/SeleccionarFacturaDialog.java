package aplicacion;

import dominio.Factura;
import dominio.Reserva;
import aplicacion.Sistema;

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

public class SeleccionarFacturaDialog extends JDialog {

    private JTable table;
    private FacturasTableModel tableModel;
    private Integer selectedFacturaId = null;

    public SeleccionarFacturaDialog(Window owner, Sistema sistema) {
        super(owner, "Seleccionar factura", ModalityType.APPLICATION_MODAL);
        setSize(640, 420);
        setLocationRelativeTo(owner);

        Color fondo = new Color(245, 247, 250);
        Color acento = new Color(0, 120, 215);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setBackground(fondo);

        JLabel title = new JLabel("💳 Seleccioná una factura pendiente");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(acento);
        content.add(title, BorderLayout.NORTH);

        // Modelo de tabla
        tableModel = new FacturasTableModel(sistema);
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<FacturasTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.naturalOrder()); // ID
        sorter.setComparator(1, Comparator.comparingDouble(a -> (Double) a)); // Total
        sorter.setComparator(3, Comparator.naturalOrder()); // Fecha vencimiento
        table.setRowSorter(sorter);

        // Doble clic → selección directa
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

        // Botones inferiores
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

        // Mensaje si no hay facturas pendientes
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay facturas pendientes de pago.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
        }

        setContentPane(content);
    }

    private void confirmarSeleccion() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná una factura de la lista.",
                    "Nada seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        selectedFacturaId = tableModel.getIdAt(modelRow);
        dispose();
    }

    public Integer getSelectedFacturaId() {
        return selectedFacturaId;
    }

    // ===================== TABLE MODEL ===================== //
    private static class FacturasTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Cliente", "Total", "Método", "Vence"};
        private final List<Fila> data = new ArrayList<>();

        private static class Fila {
            int id;
            String cliente;
            double total;
            String metodo;
            LocalDate vence;

            Fila(int id, String cliente, double total, String metodo, LocalDate vence) {
                this.id = id;
                this.cliente = cliente;
                this.total = total;
                this.metodo = metodo;
                this.vence = vence;
            }
        }

        FacturasTableModel(Sistema sistema) {
            try {
                List<Reserva> reservas = sistema.listarTodasLasReservas(); // ✅ obtener desde sistema
                for (Reserva r : reservas) {
                    Factura f = r.getFactura();
                    if (f != null && !f.isPagada()) {
                        data.add(new Fila(
                                f.getId(),
                                r.getHuesped().getNombreCompleto(),
                                f.getTotal(),
                                f.getMetodo().name(),
                                f.getVencimiento()
                        ));
                    }
                }
                data.sort(Comparator.comparingInt(o -> o.id));
            } catch (Exception e) {
                System.err.println("Error cargando facturas: " + e.getMessage());
            }
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Fila f = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return f.id;
                case 1: return f.cliente;
                case 2: return String.format("$%.2f", f.total);
                case 3: return f.metodo;
                case 4: return f.vence;
                default: return null;
            }
        }

        public Integer getIdAt(int modelRow) {
            return data.get(modelRow).id;
        }
    }
}