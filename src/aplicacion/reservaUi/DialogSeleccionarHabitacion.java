package aplicacion.reservaUi;

import dto.HabitacionListadoDTO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DialogSeleccionarHabitacion extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);

    private JTable tabla;
    private HabitacionTableModel tableModel;

    private JButton btnSeleccionar;
    private JButton btnCerrar;

    private HabitacionListadoDTO seleccionada;

    public DialogSeleccionarHabitacion(
            Window owner,
            LocalDate desde,
            LocalDate hasta,
            List<HabitacionListadoDTO> disponibles
    ) {
        super(owner, "Seleccionar Habitación", ModalityType.APPLICATION_MODAL);

        setSize(800, 500);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(750, 450));

        initComponents();
        tableModel.setDatos(disponibles);

        setContentPane(buildMainPanel(desde, hasta));
        initListeners();
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

    private JPanel buildMainPanel(LocalDate desde, LocalDate hasta) {

        JPanel global = new JPanel(new BorderLayout(10, 10));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("Seleccionar Habitación");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        global.add(titulo, BorderLayout.NORTH);

        // Rango fechas
        JPanel infoFechas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoFechas.setBackground(CARD);
        infoFechas.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        infoFechas.add(new JLabel("Desde: " + desde + "  —  Hasta: " + hasta));

        global.add(infoFechas, BorderLayout.BEFORE_FIRST_LINE);

        // Tabla
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
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.setOpaque(false);
        bottom.add(btnSeleccionar);
        bottom.add(btnCerrar);
        return bottom;
    }

    private void initListeners() {

        btnCerrar.addActionListener(e -> dispose());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                btnSeleccionar.setEnabled(tabla.getSelectedRow() != -1);
        });

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() != -1)
                    seleccionarFila();
            }
        });

        btnSeleccionar.addActionListener(e -> seleccionarFila());
    }

    private void seleccionarFila() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow == -1) return;

        int modelRow = tabla.convertRowIndexToModel(viewRow);
        seleccionada = tableModel.getAt(modelRow);

        dispose();
    }

    public HabitacionListadoDTO getSeleccionada() {
        return seleccionada;
    }


    // =============== TABLE MODEL ===============
    private static class HabitacionTableModel extends AbstractTableModel {

        private final String[] columnas = {
                "ID", "Nombre", "Tipo", "Capacidad", "Precio por noche"
        };

        private List<HabitacionListadoDTO> filas = new ArrayList<>();

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            HabitacionListadoDTO h = filas.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> h.id();
                case 1 -> h.nombre();
                case 2 -> h.tipo();
                case 3 -> h.capacidad();
                case 4 -> h.precio();
                default -> "";
            };
        }

        public HabitacionListadoDTO getAt(int row) {
            return filas.get(row);
        }

        public void setDatos(List<HabitacionListadoDTO> datos) {
            filas = new ArrayList<>(datos);
            fireTableDataChanged();
        }
    }
}