package aplicacion.reservaUi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DialogSeleccionarHabitacion extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final Date fechaDesde;
    private final Date fechaHasta;

    private JTable tabla;
    private HabitacionTableModel tableModel;

    private JButton btnSeleccionar;
    private JButton btnCerrar;

    public static class HabitacionSeleccionada {
        public final int id;
        public final String nombre;
        public final String tipo;
        public final int capacidad;
        public final double precioNoche;

        public HabitacionSeleccionada(int id, String nombre, String tipo,
                                      int capacidad, double precioNoche) {
            this.id = id;
            this.nombre = nombre;
            this.tipo = tipo;
            this.capacidad = capacidad;
            this.precioNoche = precioNoche;
        }
    }

    private HabitacionSeleccionada seleccionada;

    public DialogSeleccionarHabitacion(Window owner, Date desde, Date hasta) {
        super(owner, "Seleccionar Habitación", ModalityType.APPLICATION_MODAL);
        this.fechaDesde = desde;
        this.fechaHasta = hasta;

        setSize(800, 500);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(750, 450));

        initComponents();
        setContentPane(buildMainPanel());
        initListeners();

        cargarHabitacionesMock(); // TODO: reemplazar con llamadas a Sistema
    }

    private void initComponents() {

        tableModel = new HabitacionTableModel();
        tabla = new JTable(tableModel);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(true);

        btnSeleccionar = new JButton("Seleccionar");
        btnSeleccionar.setBackground(ACCENT);
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setEnabled(false);

        btnCerrar = new JButton("Cerrar");
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel(new BorderLayout(10, 10));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Seleccionar Habitación");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        titulo.setBorder(new EmptyBorder(0, 0, 8, 0));

        global.add(titulo, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        center.add(createInfoFechasCard(), BorderLayout.NORTH);
        center.add(createTableCard(), BorderLayout.CENTER);

        global.add(center, BorderLayout.CENTER);
        global.add(createBottomButtons(), BorderLayout.SOUTH);

        return global;
    }

    private JPanel createInfoFechasCard() {

        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        JLabel lbl = new JLabel("Rango de fechas:  Desde "
                + sdf.format(fechaDesde) + "  hasta  " + sdf.format(fechaHasta));
        lbl.setFont(LABEL_FONT);

        card.add(lbl);
        return card;
    }

    private JPanel createTableCard() {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(700, 300));

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBottomButtons() {

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.setOpaque(false);

        bottom.add(btnSeleccionar);
        bottom.add(btnCerrar);

        return bottom;
    }

    private void initListeners() {

        btnCerrar.addActionListener(e -> dispose());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnSeleccionar.setEnabled(tabla.getSelectedRow() != -1);
            }
        });

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    seleccionarFilaActualYSalir();
                }
            }
        });

        btnSeleccionar.addActionListener(e -> seleccionarFilaActualYSalir());
    }

    private void cargarHabitacionesMock() {

        // TODO: reemplazar por Sistema.listarHabitacionesDisponibles(desde, hasta)
        List<HabitacionSeleccionada> list = new ArrayList<>();
        list.add(new HabitacionSeleccionada(1, "Suite 12", "MATRIMONIAL", 2, 25000.0));
        list.add(new HabitacionSeleccionada(2, "Doble 7", "INDIVIDUAL", 2, 15000.0));
        list.add(new HabitacionSeleccionada(3, "Compartida 1", "COMPARTIDA", 4, 8000.0));

        tableModel.setDatos(list);
    }

    private void seleccionarFilaActualYSalir() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow == -1) return;

        int row = tabla.convertRowIndexToModel(viewRow);
        HabitacionSeleccionada h = tableModel.getAt(row);
        this.seleccionada = h;
        dispose();
    }

    public HabitacionSeleccionada getSeleccionada() {
        return seleccionada;
    }

    // =======================
    // TABLE MODEL
    // =======================
    private static class HabitacionTableModel extends AbstractTableModel {

        private final String[] columnas = {
                "ID", "Nombre", "Tipo", "Capacidad", "Precio por noche"
        };

        private List<HabitacionSeleccionada> filas = new ArrayList<>();

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HabitacionSeleccionada h = filas.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> h.id;
                case 1 -> h.nombre;
                case 2 -> h.tipo;
                case 3 -> h.capacidad;
                case 4 -> h.precioNoche;
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 3 -> Integer.class;
                case 4 -> Double.class;
                default -> String.class;
            };
        }

        public void setDatos(List<HabitacionSeleccionada> lista) {
            this.filas = new ArrayList<>(lista);
            fireTableDataChanged();
        }

        public HabitacionSeleccionada getAt(int row) {
            return filas.get(row);
        }
    }
}
