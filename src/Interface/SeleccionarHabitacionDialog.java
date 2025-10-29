package src.Interface;

import src.classes.Sistema;
import src.classes.Habitacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SeleccionarHabitacionDialog extends JDialog {

    private JTable table;
    private HabitacionesTableModel tableModel;
    private Integer selectedHabitacionId = null;

    public SeleccionarHabitacionDialog(Window owner, Sistema sistema) {
        super(owner, "Seleccionar habitación", ModalityType.APPLICATION_MODAL);
        setSize(640, 420);
        setLocationRelativeTo(owner);

        Color fondo = new Color(245, 247, 250);
        Color acento = new Color(0, 120, 215);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setBackground(fondo);

        JLabel title = new JLabel("🛏️ Seleccioná una habitación");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(acento);
        content.add(title, BorderLayout.NORTH);

        // Tabla
        tableModel = new HabitacionesTableModel(sistema);
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // сортировка по клику по заголовкам
        TableRowSorter<HabitacionesTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, Comparator.naturalOrder()); // ID
        sorter.setComparator(2, Comparator.comparingDouble(a -> (Double) a)); // Precio
        sorter.setComparator(3, Comparator.naturalOrder()); // Capacidad
        table.setRowSorter(sorter);

        // двойной клик = выбрать
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    confirmarSeleccion();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        content.add(scroll, BorderLayout.CENTER);

        // Кнопки
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

        // Если нет комнат — предупредим
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Aún no hay habitaciones creadas.\nAgregá una habitación antes de seleccionar.",
                    "Sin datos", JOptionPane.INFORMATION_MESSAGE);
        }

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

    /** Возвращает выбранный ID (или null, если пользователь отменил) */
    public Integer getSelectedHabitacionId() {
        return selectedHabitacionId;
    }

    // ---- TableModel ----
    private static class HabitacionesTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Nombre", "Precio ($)", "Capacidad"};
        private final List<Fila> data = new ArrayList<>();

        private static class Fila {
            int id;
            String nombre;
            double precio;
            int capacidad;
            Fila(int id, String nombre, double precio, int capacidad) {
                this.id = id; this.nombre = nombre; this.precio = precio; this.capacidad = capacidad;
            }
        }

        HabitacionesTableModel(Sistema sistema) {
            // Требуется геттер в Sistema: getHabitaciones()
            // Если его ещё нет — см. короткий сниппет ниже.
            for (Map.Entry<Integer, Habitacion> e : sistema.getHabitaciones().entrySet()) {
                Integer id = e.getKey();
                Habitacion h = e.getValue();
                data.add(new Fila(id, safe(h.getNombre()), h.getPrecio(), h.getCapacidad()));
            }
            // Лёгкая сортировка по ID по умолчанию
            data.sort(Comparator.comparingInt(o -> o.id));
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return Integer.class;
                case 1: return String.class;
                case 2: return Double.class;
                case 3: return Integer.class;
                default: return Object.class;
            }
        }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            Fila f = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return f.id;
                case 1: return f.nombre;
                case 2: return f.precio;
                case 3: return f.capacidad;
                default: return null;
            }
        }

        public Integer getIdAt(int modelRow) {
            return data.get(modelRow).id;
        }

        private String safe(String s) { return s == null ? "" : s; }
    }
}
