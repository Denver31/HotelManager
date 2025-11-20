package aplicacion.huespedUi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class PanelHuespedes extends JPanel {

    // Estilos (mismos que PanelReservas / PanelHabitaciones)
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JTable tablaHuespedes;
    private HuespedesTableModel huespedesTableModel;

    // Búsqueda
    private JTextField txtBuscar;
    // Botones
    private JButton btnBuscar;
    private JButton btnLimpiar;
    private JButton btnNuevoHuesped;

    public PanelHuespedes() {

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        add(buildMainPanel(), BorderLayout.CENTER);
        initListeners();
        cargarHuespedesDummy();
    }

    private void initComponents() {

        huespedesTableModel = new HuespedesTableModel();
        tablaHuespedes = new JTable(huespedesTableModel);
        tablaHuespedes.setRowHeight(26);
        tablaHuespedes.setAutoCreateRowSorter(true);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(LABEL_FONT);

        btnBuscar = new JButton("🔎 Buscar");
        btnBuscar.setBackground(ACCENT);
        btnBuscar.setForeground(Color.WHITE);

        btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.setBackground(new Color(200, 200, 200));

        btnNuevoHuesped = new JButton("➕ Nuevo huésped");
        btnNuevoHuesped.setBackground(ACCENT);
        btnNuevoHuesped.setForeground(Color.WHITE);
    }

    private JPanel buildMainPanel() {

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setOpaque(false);

        JLabel title = new JLabel("Gestión de Huéspedes");
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

        JPanel card = createCard("Búsqueda y acciones");
        JPanel inside = new JPanel(new GridBagLayout());
        inside.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;

        gbc.gridx = 0;
        inside.add(new JLabel("Buscar (ID / Nombre / Apellido / DNI):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inside.add(txtBuscar, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        inside.add(btnBuscar, gbc);

        gbc.gridx = 3;
        inside.add(btnLimpiar, gbc);

        gbc.gridx = 4;
        gbc.anchor = GridBagConstraints.EAST;
        inside.add(btnNuevoHuesped, gbc);

        card.add(inside, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableCard() {

        JPanel card = createCard("Listado de huéspedes");

        JScrollPane scrollPane = new JScrollPane(tablaHuespedes);
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
        tablaHuespedes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2 && tablaHuespedes.getSelectedRow() != -1) {

                    int row = tablaHuespedes.convertRowIndexToModel(
                            tablaHuespedes.getSelectedRow()
                    );
                    int id = (Integer) huespedesTableModel.getValueAt(row, 0);

                    DialogDetalleHuesped dialog = new DialogDetalleHuesped(
                            SwingUtilities.getWindowAncestor(PanelHuespedes.this),
                            id
                    );
                    dialog.setLocationRelativeTo(PanelHuespedes.this);
                    dialog.setVisible(true);
                }
            }
        });

        btnBuscar.addActionListener(e -> aplicarBusqueda());
        btnLimpiar.addActionListener(e -> limpiarBusqueda());

        btnNuevoHuesped.addActionListener(e -> {
            DialogCrearHuesped dialog = new DialogCrearHuesped(
                    SwingUtilities.getWindowAncestor(this)
            );
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });
    }

    private void aplicarBusqueda() {
        String filtro = txtBuscar.getText().trim();
        System.out.println("[BUSCAR HUESPED]: " + filtro);
        // TODO: llamar a Controller / Sistema para filtrar por ID, nombre, apellido o DNI
    }

    private void limpiarBusqueda() {
        txtBuscar.setText("");
        cargarHuespedesDummy();
    }

    private void cargarHuespedesDummy() {
        huespedesTableModel.setHuespedes(List.of(
                new Object[]{1, "Juan", "Pérez", "30123456", "juan@example.com", "ACTIVO"},
                new Object[]{2, "Ana", "Gómez", "28999888", "ana@example.com", "ACTIVO"},
                new Object[]{3, "Carlos", "López", "31222333", "carlos@example.com", "BAJA"}
        ));
    }

    // --------------------
    // Table Model
    // --------------------
    private static class HuespedesTableModel extends AbstractTableModel {

        private final String[] columnas = {
                "ID", "Nombre", "Apellido", "DNI", "Email", "Estado"
        };

        private List<Object[]> filas = new ArrayList<>();

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        @Override public Object getValueAt(int row, int col) { return filas.get(row)[col]; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0 -> Integer.class; // ID
                case 3 -> String.class;  // DNI (como texto para no perder ceros)
                default -> String.class;
            };
        }

        public void setHuespedes(List<Object[]> datos) {
            this.filas = new ArrayList<>(datos);
            fireTableDataChanged();
        }
    }

    // MAIN de prueba opcional
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Panel Huéspedes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 700);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new PanelHuespedes());
            frame.setVisible(true);
        });
    }
}