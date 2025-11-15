package aplicacion;

import dominio.Habitacion;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class SeleccionarHabitacionDialog extends JDialog {

    private JTable table;
    private HabitacionesTableModel tableModel;
    private Integer selectedHabitacionId = null;

    public SeleccionarHabitacionDialog(Window owner, Sistema sistema, LocalDate desde, LocalDate hasta) {
        super(owner, "Seleccionar habitación", ModalityType.APPLICATION_MODAL);
        setSize(640, 420);
        setLocationRelativeTo(owner);

        Color fondo = new Color(245, 247, 250);
        Color acento = new Color(0, 120, 215);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setBackground(fondo);

        JLabel title = new JLabel("🛏️ Habitaciones disponibles (" + desde + " → " + hasta + ")", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(acento);
        content.add(title, BorderLayout.NORTH);

        // ✅ Obtenemos solo las habitaciones realmente disponibles
        List<Habitacion> disponibles = sistema.listarHabitacionesDisponibles(desde, hasta);

        // Si no hay disponibles, mostramos aviso y no abrimos el listado
        if (disponibles == null || disponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay habitaciones disponibles entre " + desde + " y " + hasta + ".",
                    "Sin disponibilidad", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        // Creamos la tabla con las habitaciones disponibles
        tableModel = new HabitacionesTableModel(disponibles);
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<HabitacionesTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.naturalOrder());
        sorter.setComparator(2, Comparator.comparingDouble(a -> (Double) a));
        sorter.setComparator(3, Comparator.naturalOrder());
        table.setRowSorter(sorter);

        // Doble click = seleccionar
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
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

        setContentPane(content);
    }

    private void confirmarSeleccion() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná una habitación de la lista.",
                    "Nada seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        selectedHabitacionId = tableModel.getIdAt(modelRow);
        dispose();
    }

    public Integer getSelectedHabitacionId() {
        return selectedHabitacionId;
    }

    // ==============================================================
    // MODELO DE TABLA
    // ==============================================================

    private static class HabitacionesTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Nombre", "Precio ($)", "Capacidad"};
        private final List<Habitacion> data;

        HabitacionesTableModel(List<Habitacion> habitaciones) {
            this.data = habitaciones;
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            Habitacion h = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return h.getId();
                case 1: return h.getNombre();
                case 2: return h.getPrecio();
                case 3: return h.getCapacidad();
                default: return null;
            }
        }

        public Integer getIdAt(int modelRow) {
            return data.get(modelRow).getId();
        }
    }
}