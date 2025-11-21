package aplicacion.facturaUi;

import aplicacion.facturaUi.presenter.FacturasPresenter;
import aplicacion.facturaUi.presenter.DetalleFacturaPresenter;
import aplicacion.huespedUi.PanelHuespedes;
import dto.FacturaListadoDTO;
import servicios.FacturaService;

import almacenamiento.*;
import servicios.HabitacionService;
import servicios.HuespedService;
import servicios.ReservaService;
import almacenamiento.DatabaseManager;

import java.sql.Connection;

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

    // Presenter
    private FacturasPresenter presenter;

    // Estilos
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
    }

    // ============================================================
    // Inyección de Presenter
    // ============================================================
    public void setPresenter(FacturasPresenter presenter) {
        this.presenter = presenter;
        presenter.cargarFacturas(); // carga inicial real
    }

    // ============================================================
    // Inicialización UI
    // ============================================================
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

    // ============================================================
    // Listeners (MVP)
    // ============================================================
    private void initListeners() {

        tablaFacturas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaFacturas.getSelectedRow() != -1) {

                    int row = tablaFacturas.convertRowIndexToModel(tablaFacturas.getSelectedRow());
                    int idFactura = (Integer) facturasTableModel.getValueAt(row, 0);

                    presenter.abrirDetalleFactura(idFactura);
                }
            }
        });

        btnBuscar.addActionListener(e ->
                presenter.buscar(txtBuscar.getText().trim())
        );

        btnLimpiar.addActionListener(e ->
                presenter.limpiar()
        );
    }

    // ============================================================
    // Métodos usados por el Presenter
    // ============================================================
    public void mostrarFacturas(List<FacturaListadoDTO> dtos) {

        List<Object[]> filas = dtos.stream()
                .map(dto -> new Object[]{
                        dto.idFactura(),
                        dto.idHuesped(),
                        dto.nombreCompleto(),
                        dto.idReserva(),
                        dto.fechaAlta(),
                        dto.fechaVencimiento(),
                        dto.estado()
                })
                .toList();

        facturasTableModel.setFacturas(filas);
    }

    public void limpiarFiltro() {
        txtBuscar.setText("");
    }

    public void abrirDialogoDetalleFactura(int idFactura) {

        Window owner = SwingUtilities.getWindowAncestor(this);

        DialogDetalleFactura dialog = new DialogDetalleFactura(owner, idFactura);

        // Presenter hijo con callback al presenter padre
        DetalleFacturaPresenter dp = new DetalleFacturaPresenter(
                dialog,
                presenter.getFacturaService(),
                presenter.getReservaService(),
                () -> presenter.cargarFacturas() // REFRESCA TABLA
        );

        dialog.setPresenter(dp);
        dialog.setVisible(true);
    }


    // ============================================================
    // Table Model
    // ============================================================
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
    /*public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Connection conn = DatabaseManager.getConnection();

            HabitacionStorage habitacionStorage = new HabitacionStorage(conn);
            HuespedStorage huespedStorage = new HuespedStorage(conn);
            FacturaStorage facturaStorage = new FacturaStorage(conn);
            ReservaStorage reservaStorage = new ReservaStorage(conn, habitacionStorage, huespedStorage, facturaStorage);

            // Servicios
            ReservaService reservaService = new ReservaService(reservaStorage);
            FacturaService facturaService = new FacturaService(facturaStorage, reservaService);

            // Panel + Presenter
            PanelFacturas panel = new PanelFacturas();
            FacturasPresenter presenter = new FacturasPresenter(panel, facturaService);

            panel.setPresenter(presenter);

            // Frame
            JFrame frame = new JFrame("Test Facturas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }*/

}