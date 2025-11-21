package aplicacion.huespedUi;

import aplicacion.huespedUi.presenter.HuespedesPresenter;
import dto.HuespedListadoDTO;

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

import almacenamiento.HuespedStorage;
import servicios.HuespedService;
import servicios.ReservaService;
import almacenamiento.DatabaseManager;
import almacenamiento.HabitacionStorage;
import almacenamiento.FacturaStorage;
import almacenamiento.ReservaStorage;
import java.sql.Connection;


public class PanelHuespedes extends JPanel {

    // Estilos
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // ============================================================
    // MVP
    // ============================================================
    private HuespedesPresenter presenter;

    public void setPresenter(HuespedesPresenter presenter) {
        this.presenter = presenter;
    }

    // ============================================================

    private JTable tablaHuespedes;
    private HuespedesTableModel huespedesTableModel;

    private JTextField txtBuscar;
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

        // ============================================================
        // DOBLE CLICK → Presenter.onSeleccionar(id)
        // ============================================================
        tablaHuespedes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2 && tablaHuespedes.getSelectedRow() != -1) {

                    int row = tablaHuespedes.convertRowIndexToModel(
                            tablaHuespedes.getSelectedRow()
                    );
                    int id = (Integer) huespedesTableModel.getValueAt(row, 0);

                    if (presenter != null)
                        presenter.onSeleccionar(id);
                }
            }
        });

        // ============================================================
        // BÚSQUEDA
        // ============================================================
        btnBuscar.addActionListener(e -> {
            if (presenter != null)
                presenter.onBuscar(getFiltro());
        });

        // ============================================================
        // LIMPIAR
        // ============================================================
        btnLimpiar.addActionListener(e -> {
            if (presenter != null)
                presenter.onLimpiar();
        });

        // ============================================================
        // NUEVO
        // ============================================================
        btnNuevoHuesped.addActionListener(e -> {
            if (presenter != null)
                presenter.onNuevo();
        });
    }

    // ============================================================
    // MÉTODOS PARA EL PRESENTER (VIEW API)
    // ============================================================
    public String getFiltro() {
        return txtBuscar.getText().trim();
    }

    public void limpiarFiltro() {
        txtBuscar.setText("");
    }

    public void mostrarListado(List<HuespedListadoDTO> lista) {

        List<Object[]> rows = new ArrayList<>();

        for (HuespedListadoDTO dto : lista) {
            rows.add(new Object[]{
                    dto.id(),
                    dto.nombre(),
                    dto.apellido(),
                    dto.dni(),
                    dto.email(),
                    dto.estado()
            });
        }

        huespedesTableModel.setHuespedes(rows);
    }

    public Window getParentWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    // ============================================================
    // TABLE MODEL
    // ============================================================
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
                case 0 -> Integer.class;
                case 3 -> String.class;
                default -> String.class;
            };
        }

        public void setHuespedes(List<Object[]> datos) {
            this.filas = new ArrayList<>(datos);
            fireTableDataChanged();
        }
    }

    /* public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Connection conn = DatabaseManager.getConnection();

            // =============================
            // Storages
            // =============================
            HuespedStorage huespedStorage = new HuespedStorage(conn);
            HabitacionStorage habitacionStorage = new HabitacionStorage(conn);
            FacturaStorage facturaStorage = new FacturaStorage(conn);
            ReservaStorage reservaStorage =
                    new ReservaStorage(conn, habitacionStorage, huespedStorage, facturaStorage);

            // =============================
            // Services
            // =============================
            ReservaService reservaService = new ReservaService(reservaStorage);
            HuespedService huespedService = new HuespedService(huespedStorage, reservaService);

            // =============================
            // UI + Presenter
            // =============================
            PanelHuespedes panel = new PanelHuespedes();
            HuespedesPresenter presenter = new HuespedesPresenter(huespedService, panel);

            panel.setPresenter(presenter);
            presenter.cargarListado();

            // =============================
            // Frame
            // =============================
            JFrame frame = new JFrame("Test Panel Huéspedes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 700);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }*/
}
