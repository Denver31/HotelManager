package aplicacion.reservaUi;

import dto.reserva.ReservaListadoDTO;
import aplicacion.reservaUi.presenter.ReservasPresenter;

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

public class PanelReservas extends JPanel {

    // ============================================================
    // ESTILOS
    // ============================================================
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // ============================================================
    // MVP
    // ============================================================
    private ReservasPresenter presenter;

    public void setPresenter(ReservasPresenter presenter) {
        this.presenter = presenter;
    }

    // ============================================================
    // COMPONENTES
    // ============================================================
    private JTable tablaReservas;
    private ReservasTableModel reservasTableModel;

    private JTextField txtFiltroTexto;
    private JComboBox<String> cboEstado;

    private JSpinner spDesde;
    private JSpinner spHasta;

    private JButton btnBuscar;
    private JButton btnBuscarTexto;
    private JButton btnLimpiar;
    private JButton btnNuevaReserva;

    public PanelReservas() {

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        add(buildMainPanel(), BorderLayout.CENTER);
        initListeners();
    }

    // ============================================================
    // Inicialización componentes
    // ============================================================
    private void initComponents() {

        reservasTableModel = new ReservasTableModel();
        tablaReservas = new JTable(reservasTableModel);
        tablaReservas.setRowHeight(26);
        tablaReservas.setAutoCreateRowSorter(true);

        txtFiltroTexto = new JTextField(25);
        txtFiltroTexto.setFont(LABEL_FONT);

        cboEstado = new JComboBox<>(new String[]{
                "Todos", "PENDIENTE", "CONFIRMADA", "ACTIVA", "FINALIZADA", "CANCELADA"
        });
        cboEstado.setFont(LABEL_FONT);

        spDesde = new JSpinner(new SpinnerDateModel());
        spDesde.setEditor(new JSpinner.DateEditor(spDesde, "dd/MM/yyyy"));
        spDesde.setFont(LABEL_FONT);

        spHasta = new JSpinner(new SpinnerDateModel());
        spHasta.setEditor(new JSpinner.DateEditor(spHasta, "dd/MM/yyyy"));
        spHasta.setFont(LABEL_FONT);

        btnBuscar = new JButton("🔍 Aplicar filtros");
        btnBuscar.setBackground(ACCENT);
        btnBuscar.setForeground(Color.WHITE);

        btnBuscarTexto = new JButton("🔎 Buscar texto");
        btnBuscarTexto.setBackground(ACCENT);
        btnBuscarTexto.setForeground(Color.WHITE);

        btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.setBackground(new Color(200, 200, 200));

        btnNuevaReserva = new JButton("➕ Nueva reserva");
        btnNuevaReserva.setBackground(ACCENT);
        btnNuevaReserva.setForeground(Color.WHITE);
    }

    // ============================================================
    // UI PRINCIPAL
    // ============================================================
    private JPanel buildMainPanel() {

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setOpaque(false);

        JLabel title = new JLabel("Gestión de Reservas");
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

    // ============================================================
    // Tarjetas (filtros + tabla)
    // ============================================================
    private JPanel createFilterCard() {

        JPanel card = createCard("Filtros de búsqueda");
        JPanel inside = new JPanel(new GridBagLayout());
        inside.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // FILA 1
        gbc.gridy = 0;

        gbc.gridx = 0; inside.add(new JLabel("Desde:"), gbc);
        gbc.gridx = 1; inside.add(spDesde, gbc);

        gbc.gridx = 2; inside.add(new JLabel("Hasta:"), gbc);
        gbc.gridx = 3; inside.add(spHasta, gbc);

        gbc.gridx = 4; inside.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 5; inside.add(cboEstado, gbc);

        gbc.gridx = 6; inside.add(btnBuscar, gbc);
        gbc.gridx = 7; inside.add(btnLimpiar, gbc);

        // FILA 2
        gbc.gridy = 1;

        gbc.gridx = 0;
        inside.add(new JLabel("Buscar (ID / Huésped / Habitación)"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inside.add(txtFiltroTexto, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        inside.add(btnBuscarTexto, gbc);

        gbc.gridx = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        inside.add(btnNuevaReserva, gbc);

        card.add(inside, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableCard() {

        JPanel card = createCard("Listado de reservas");

        JScrollPane scrollPane = new JScrollPane(tablaReservas);
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
    // LISTENERS – Pasan todo al Presenter
    // ============================================================
    private void initListeners() {

        // Doble click → abrir detalle
        tablaReservas.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2 && tablaReservas.getSelectedRow() != -1) {

                    int row = tablaReservas.convertRowIndexToModel(tablaReservas.getSelectedRow());
                    int id = (Integer) reservasTableModel.getValueAt(row, 0);

                    if (presenter != null)
                        presenter.abrirDetalle(id);
                }
            }
        });

        btnBuscar.addActionListener(e -> {
            if (presenter != null)
                presenter.aplicarFiltros(
                        getLocalDate(spDesde),
                        getLocalDate(spHasta),
                        cboEstado.getSelectedItem().toString()
                );
        });

        btnBuscarTexto.addActionListener(e -> {
            if (presenter != null)
                presenter.aplicarBusquedaTexto(txtFiltroTexto.getText().trim());
        });

        btnLimpiar.addActionListener(e -> limpiarFiltros());

        btnNuevaReserva.addActionListener(e -> {
            if (presenter != null)
                presenter.abrirCrearReserva();
        });
    }

    // ============================================================
    // Utilidad: convertir Spinner → LocalDate
    // ============================================================
    private LocalDate getLocalDate(JSpinner spinner) {

        Date date = (Date) spinner.getValue();

        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // ============================================================
    // ACCIONES DE LA VISTA (usadas por el Presenter)
    // ============================================================
    public void mostrarReservas(List<ReservaListadoDTO> lista) {

        List<Object[]> filas = new ArrayList<>();

        for (ReservaListadoDTO dto : lista) {
            filas.add(new Object[]{
                    dto.idReserva(),
                    dto.idHuesped(),
                    dto.nombreCompletoHuesped(),
                    dto.idHabitacion(),
                    dto.nombreHabitacion(),
                    dto.desde(),
                    dto.hasta(),
                    dto.estado()
            });
        }

        reservasTableModel.setReservas(filas);
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void limpiarFiltros() {
        spDesde.setValue(new Date());
        spHasta.setValue(new Date());
        txtFiltroTexto.setText("");
        cboEstado.setSelectedIndex(0);
        presenter.cargarListado();
    }

    // ============================================================
    // TABLE MODEL
    // ============================================================
    private static class ReservasTableModel extends AbstractTableModel {

        private final String[] columnas = {
                "ID", "ID Huésped", "Huésped", "ID Habitación",
                "Nombre Habitación", "Entrada", "Salida", "Estado"
        };

        private List<Object[]> filas = new ArrayList<>();

        @Override
        public int getRowCount() {
            return filas.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnas[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return filas.get(row)[col];
        }

        public void setReservas(List<Object[]> datos) {
            this.filas = datos;
            fireTableDataChanged();
        }
    }



    /* public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Connection conn = DatabaseManager.getConnection();

            // ===== Storages =====
            HabitacionStorage habitacionStorage = new HabitacionStorage(conn);
            HuespedStorage huespedStorage = new HuespedStorage(conn);
            FacturaStorage facturaStorage = new FacturaStorage(conn);
            ReservaStorage reservaStorage = new ReservaStorage(conn, habitacionStorage, huespedStorage, facturaStorage);


            // ===== Services =====
            ReservaService reservaService = new ReservaService(reservaStorage);
            FacturaService facturaService = new FacturaService(facturaStorage, reservaService);
            HuespedService huespedService = new HuespedService(huespedStorage, reservaService);
            HabitacionService habitacionService = new HabitacionService(habitacionStorage, reservaService);


            // ===== UI =====
            PanelReservas panel = new PanelReservas();

            ReservasPresenter presenter = new ReservasPresenter(
                    panel,
                    reservaService,
                    huespedService,
                    habitacionService,
                    facturaService
            );

            panel.setPresenter(presenter);
            presenter.cargarListado();

            JFrame frame = new JFrame("Test Reservas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }*/

}