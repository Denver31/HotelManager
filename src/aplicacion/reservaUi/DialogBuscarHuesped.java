package aplicacion.reservaUi;

import dto.HuespedListadoDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DialogBuscarHuesped extends JDialog {

    private static final Color BG = new Color(245,247,250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0,120,215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);

    private JTable tabla;
    private HuespedTableModel tableModel;

    private JButton btnSeleccionar;
    private JButton btnCerrar;

    // Resultado elegido
    private HuespedListadoDTO seleccionado;

    // ============================================================
    // Constructor
    // ============================================================
    public DialogBuscarHuesped(Window owner, List<HuespedListadoDTO> lista) {
        super(owner, "Seleccionar Huésped", ModalityType.APPLICATION_MODAL);

        setSize(650, 450);
        setLocationRelativeTo(owner);

        initComponents();
        tableModel.setDatos(lista);
        setContentPane(buildMainPanel());
        initListeners();
    }

    private void initComponents() {
        tableModel = new HuespedTableModel();
        tabla = new JTable(tableModel);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(true);

        btnSeleccionar = new JButton("Seleccionar");
        btnSeleccionar.setEnabled(false);

        btnCerrar = new JButton("Cerrar");
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel(new BorderLayout(10, 10));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Seleccionar Huésped");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        global.add(titulo, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        card.add(new JScrollPane(tabla), BorderLayout.CENTER);

        global.add(card, BorderLayout.CENTER);
        global.add(createBottomButtons(), BorderLayout.SOUTH);

        return global;
    }

    private JPanel createBottomButtons() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(btnSeleccionar);
        bottom.add(btnCerrar);
        return bottom;
    }

    // ============================================================
    // Event Listeners
    // ============================================================
    private void initListeners() {

        btnCerrar.addActionListener(e -> dispose());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnSeleccionar.setEnabled(tabla.getSelectedRow() != -1);
            }
        });

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    seleccionarYSalir();
                }
            }
        });

        btnSeleccionar.addActionListener(e -> seleccionarYSalir());
    }

    private void seleccionarYSalir() {
        int modelRow = tabla.convertRowIndexToModel(tabla.getSelectedRow());
        seleccionado = tableModel.getAt(modelRow);
        dispose();
    }

    public HuespedListadoDTO getSeleccionado() {
        return seleccionado;
    }

    // ============================================================
    // TableModel
    // ============================================================
    private static class HuespedTableModel extends AbstractTableModel {

        private final String[] columnas = {"ID", "DNI", "Nombre", "Apellido", "Email"};
        private List<HuespedListadoDTO> filas = new ArrayList<>();

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            if (rowIndex < 0 || rowIndex >= filas.size()) return "";

            HuespedListadoDTO h = filas.get(rowIndex);
            if (h == null) return "";

            return switch (columnIndex) {
                case 0 -> h.id();
                case 1 -> h.dni();
                case 2 -> h.nombre();
                case 3 -> h.apellido();
                case 4 -> h.email();
                default -> "";
            };
        }

        public void setDatos(List<HuespedListadoDTO> datos) {
            filas = new ArrayList<>(datos);
            fireTableDataChanged();
        }

        public HuespedListadoDTO getAt(int row) {
            return filas.get(row);
        }
    }
}
