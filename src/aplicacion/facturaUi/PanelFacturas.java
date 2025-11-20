package aplicacion.facturaUi;

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

public class PanelFacturas extends JPanel {

    // Estilos compartidos
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JTable tablaFacturas;
    private FacturasTableModel facturasTableModel;

    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnLimpiar;

    public PanelFacturas() {

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        add(buildMainPanel(), BorderLayout.CENTER);
        initListeners();
        cargarFacturasDummy();
    }

    private void initComponents() {

        facturasTableModel = new FacturasTableModel();
        tablaFacturas = new JTable(facturasTableModel);
        tablaFacturas.setRowHeight(26);
        tablaFacturas.setAutoCreateRowSorter(true);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(LABEL_FONT);

        btnBuscar = new JButton("🔎 Buscar");
        btnBuscar.setBackground(ACCENT);
        btnBuscar.setForeground(Color.WHITE);

        btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.setBackground(new Color(200, 200, 200));
    }

    private JPanel buildMainPanel() {

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setOpaque(false);

        JLabel title = new JLabel("Gestión de Facturas");
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

        JPanel card = createCard("Búsqueda de facturas");

        JPanel inside = new JPanel(new GridBagLayout());
        inside.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;

        gbc.gridx = 0;
        inside.add(new JLabel("Buscar (ID Factura / ID Huésped / Nombre / ID Reserva):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inside.add(txtBuscar, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        inside.add(btnBuscar, gbc);

        gbc.gridx = 3;
        inside.add(btnLimpiar, gbc);

        card.add(inside, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableCard() {

        JPanel card = createCard("Listado de facturas");

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
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

        tablaFacturas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaFacturas.getSelectedRow() != -1) {

                    int row = tablaFacturas.convertRowIndexToModel(
                            tablaFacturas.getSelectedRow()
                    );
                    int idFactura = (Integer) facturasTableModel.getValueAt(row, 0);

                    DialogDetalleFactura dialog = new DialogDetalleFactura(
                            SwingUtilities.getWindowAncestor(PanelFacturas.this),
                            idFactura
                    );
                    dialog.setLocationRelativeTo(PanelFacturas.this);
                    dialog.setVisible(true);
                }
            }
        });

        btnBuscar.addActionListener(e -> aplicarBusqueda());
        btnLimpiar.addActionListener(e -> limpiarBusqueda());
    }

    private void aplicarBusqueda() {
        String filtro = txtBuscar.getText().trim();
        System.out.println("[BUSCAR FACTURA]: " + filtro);
        // TODO: llamar a Controller / Sistema para buscar por ID factura, ID huésped, nombre o ID reserva
    }

    private void limpiarBusqueda() {
        txtBuscar.setText("");
        cargarFacturasDummy();
    }

    private void cargarFacturasDummy() {
        facturasTableModel.setFacturas(List.of(
                new Object[]{1, 55, "Juan Pérez", 77, "2025-11-10", "2025-11-15", "PENDIENTE"},
                new Object[]{2, 56, "Ana Gómez", 78, "2025-10-01", "2025-10-10", "PAGADA"},
                new Object[]{3, 57, "Carlos López", 79, "2025-09-01", "2025-09-10", "VENCIDA"},
                new Object[]{4, 58, "Laura Díaz", 80, "2025-08-05", "2025-08-15", "CANCELADA"}
        ));
    }

    // --------------------
    // Table Model
    // --------------------
    private static class FacturasTableModel extends AbstractTableModel {

        private final String[] columnas = {
                "ID Factura", "ID Huésped", "Nombre completo",
                "ID Reserva", "Fecha alta", "Fecha vencimiento", "Estado"
        };

        private List<Object[]> filas = new ArrayList<>();

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        @Override public Object getValueAt(int row, int col) { return filas.get(row)[col]; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 1, 3 -> Integer.class;
                default -> String.class;
            };
        }

        public void setFacturas(List<Object[]> datos) {
            this.filas = new ArrayList<>(datos);
            fireTableDataChanged();
        }
    }

    // MAIN de prueba opcional
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Panel Facturas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 700);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new PanelFacturas());
            frame.setVisible(true);
        });
    }
}