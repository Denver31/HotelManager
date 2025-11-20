package aplicacion.habitacionUi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PanelHabitaciones extends JPanel {

    // Estilos compartidos con PanelReservas
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Tabla
    private JTable tablaHabitaciones;
    private HabitacionesTableModel habitacionesTableModel;

    // Búsqueda y filtros
    private JTextField txtBuscar;
    private JSpinner spDesde;
    private JSpinner spHasta;

    // Botones
    private JButton btnBuscarTexto;
    private JButton btnFiltrarFechas;
    private JButton btnLimpiar;
    private JButton btnNuevaHabitacion;

    public PanelHabitaciones() {

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        add(buildMainPanel(), BorderLayout.CENTER);
        initListeners();
        cargarHabitacionesDummy();
    }

    private void initComponents() {

        habitacionesTableModel = new HabitacionesTableModel();
        tablaHabitaciones = new JTable(habitacionesTableModel);
        tablaHabitaciones.setRowHeight(26);
        tablaHabitaciones.setAutoCreateRowSorter(true);

        txtBuscar = new JTextField(15);
        txtBuscar.setFont(LABEL_FONT);

        spDesde = new JSpinner(new SpinnerDateModel());
        spDesde.setEditor(new JSpinner.DateEditor(spDesde, "dd/MM/yyyy"));

        spHasta = new JSpinner(new SpinnerDateModel());
        spHasta.setEditor(new JSpinner.DateEditor(spHasta, "dd/MM/yyyy"));

        btnBuscarTexto = new JButton("🔎 Buscar");
        btnBuscarTexto.setBackground(ACCENT);
        btnBuscarTexto.setForeground(Color.WHITE);

        btnFiltrarFechas = new JButton("🔍 Filtrar disponibilidad");
        btnFiltrarFechas.setBackground(ACCENT);
        btnFiltrarFechas.setForeground(Color.WHITE);

        btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.setBackground(new Color(200, 200, 200));

        btnNuevaHabitacion = new JButton("➕ Nueva habitación");
        btnNuevaHabitacion.setBackground(ACCENT);
        btnNuevaHabitacion.setForeground(Color.WHITE);
    }

    private JPanel buildMainPanel() {

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setOpaque(false);

        JLabel title = new JLabel("Gestión de Habitaciones");
        title.setFont(TITLE_FONT);
        title.setForeground(ACCENT);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        main.add(title, BorderLayout.NORTH);
        main.add(createContent(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createContent() {

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setOpaque(false);

        content.add(createFilterCard(), BorderLayout.NORTH);
        content.add(createTableCard(), BorderLayout.CENTER);

        return content;
    }

    private JPanel createFilterCard() {

        JPanel card = createCard("Filtros y acciones");
        JPanel inside = new JPanel(new GridBagLayout());
        inside.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // ===============================
        // FILA 1 - Filtros por fecha
        // ===============================
        gbc.gridy = 0;

        gbc.gridx = 0; inside.add(new JLabel("Desde:"), gbc);
        gbc.gridx = 1; inside.add(spDesde, gbc);

        gbc.gridx = 2; inside.add(new JLabel("Hasta:"), gbc);
        gbc.gridx = 3; inside.add(spHasta, gbc);

        gbc.gridx = 4; inside.add(btnFiltrarFechas, gbc);
        gbc.gridx = 5; inside.add(btnLimpiar, gbc);

        // ===============================
        // FILA 2 - Búsqueda y nueva habitación
        // ===============================
        gbc.gridy = 1;

        gbc.gridx = 0; inside.add(new JLabel("Buscar (ID / Nombre):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inside.add(txtBuscar, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        inside.add(btnBuscarTexto, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        inside.add(btnNuevaHabitacion, gbc);

        card.add(inside, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableCard() {

        JPanel card = createCard("Listado de habitaciones");

        JScrollPane scrollPane = new JScrollPane(tablaHabitaciones);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCard(String title) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(CARD_TITLE_FONT);
        lbl.setForeground(new Color(60, 60, 60));
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));

        card.add(lbl, BorderLayout.NORTH);
        return card;
    }

    private void initListeners() {

        // Doble click → abrir detalle
        tablaHabitaciones.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2 && tablaHabitaciones.getSelectedRow() != -1) {

                    int row = tablaHabitaciones.convertRowIndexToModel(
                            tablaHabitaciones.getSelectedRow()
                    );
                    int id = (Integer) habitacionesTableModel.getValueAt(row, 0);

                    DialogDetalleHabitacion dialog = new DialogDetalleHabitacion(
                            SwingUtilities.getWindowAncestor(PanelHabitaciones.this),
                            id
                    );

                    dialog.setLocationRelativeTo(PanelHabitaciones.this);
                    dialog.setVisible(true);
                }
            }
        });

        btnBuscarTexto.addActionListener(e -> aplicarBusquedaTexto());
        btnFiltrarFechas.addActionListener(e -> aplicarFiltroFechas());
        btnLimpiar.addActionListener(e -> limpiarFiltros());

        btnNuevaHabitacion.addActionListener(e -> {
            DialogCrearHabitacion dialog = new DialogCrearHabitacion(
                    SwingUtilities.getWindowAncestor(this)
            );
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });
    }

    // --------------------
    // Utilidades fechas
    // --------------------
    private LocalDate getLocalDate(JSpinner spinner) {

        Date date = (Date) spinner.getValue();

        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // --------------------
    // Acciones
    // --------------------
    private void aplicarBusquedaTexto() {
        String filtro = txtBuscar.getText().trim();
        System.out.println("[BUSCAR HABITACIÓN]: " + filtro);
    }

    private void aplicarFiltroFechas() {
        System.out.println("[FILTRO FECHAS]");
    }

    private void limpiarFiltros() {
        txtBuscar.setText("");
        spDesde.setValue(new Date());
        spHasta.setValue(new Date());
        cargarHabitacionesDummy();
    }

    private void cargarHabitacionesDummy() {

        habitacionesTableModel.setHabitaciones(List.of(
                new Object[]{1, "Suite 12", "MATRIMONIAL", 2, 25000.0, "ACTIVA"},
                new Object[]{2, "Doble 7", "INDIVIDUAL", 1, 15000.0, "ACTIVA"},
                new Object[]{3, "Compartida 1", "COMPARTIDA", 4, 8000.0, "BAJA"}
        ));
    }

    // --------------------
    // Table model
    // --------------------
    private static class HabitacionesTableModel extends AbstractTableModel {

        private final String[] columnas = {
                "ID", "Nombre", "Tipo", "Capacidad", "Precio", "Estado"
        };

        private List<Object[]> filas = new ArrayList<>();

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        @Override public Object getValueAt(int row, int col) { return filas.get(row)[col]; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0 -> Integer.class;
                case 3 -> Integer.class;
                case 4 -> Double.class;
                default -> String.class;
            };
        }

        public void setHabitaciones(List<Object[]> datos) {
            this.filas = new ArrayList<>(datos);
            fireTableDataChanged();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Test Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 700);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new PanelHabitaciones());
            frame.setVisible(true);
        });
    }
}