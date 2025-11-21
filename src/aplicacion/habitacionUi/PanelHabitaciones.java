package aplicacion.habitacionUi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.sql.Connection;
import almacenamiento.DatabaseManager;
import almacenamiento.FacturaStorage;
import almacenamiento.HuespedStorage;
import almacenamiento.ReservaStorage;


import aplicacion.habitacionUi.presenter.HabitacionesPresenter;

import dto.HabitacionListadoDTO;
import servicios.HabitacionService;
import servicios.ReservaService;
import almacenamiento.HabitacionStorage;



public class PanelHabitaciones extends JPanel {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JTable tablaHabitaciones;
    private HabitacionesTableModel habitacionesTableModel;

    private JTextField txtBuscar;
    private JSpinner spDesde;
    private JSpinner spHasta;

    private JButton btnBuscarTexto;
    private JButton btnFiltrarFechas;
    private JButton btnLimpiar;
    private JButton btnNuevaHabitacion;

    // Presenter
    private HabitacionesPresenter presenter;

    public void setPresenter(HabitacionesPresenter presenter) {
        this.presenter = presenter;
    }

    public PanelHabitaciones() {

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        add(buildMainPanel(), BorderLayout.CENTER);
        initListeners();
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

        gbc.gridy = 0;

        gbc.gridx = 0; inside.add(new JLabel("Desde:"), gbc);
        gbc.gridx = 1; inside.add(spDesde, gbc);

        gbc.gridx = 2; inside.add(new JLabel("Hasta:"), gbc);
        gbc.gridx = 3; inside.add(spHasta, gbc);

        gbc.gridx = 4; inside.add(btnFiltrarFechas, gbc);
        gbc.gridx = 5; inside.add(btnLimpiar, gbc);

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

        tablaHabitaciones.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    presenter.abrirDetalle();
                }
            }
        });

        btnBuscarTexto.addActionListener(e -> presenter.buscarPorTexto());
        btnFiltrarFechas.addActionListener(e -> presenter.filtrarPorFechas());
        btnLimpiar.addActionListener(e -> presenter.limpiar());
        btnNuevaHabitacion.addActionListener(e -> presenter.nuevaHabitacion());

    }

    // ============================================================
    // API del Presenter
    // ============================================================

    public void setListado(List<HabitacionListadoDTO> lista) {
        List<Object[]> filas = new ArrayList<>();
        for (HabitacionListadoDTO h : lista) {
            filas.add(new Object[]{
                    h.id(),
                    h.nombre(),
                    h.tipo(),
                    h.capacidad(),
                    h.precio(),
                    h.estado()
            });
        }
        habitacionesTableModel.setHabitaciones(filas);
    }

    public String getFiltroTexto() {
        return txtBuscar.getText().trim();
    }

    public LocalDate getFechaDesde() {
        Date d = (Date) spDesde.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDate getFechaHasta() {
        Date d = (Date) spHasta.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public int getHabitacionSeleccionada() {
        int row = tablaHabitaciones.getSelectedRow();
        if (row == -1) return -1;

        int modelRow = tablaHabitaciones.convertRowIndexToModel(row);
        return (Integer) habitacionesTableModel.getValueAt(modelRow, 0);
    }

    public void limpiarFiltros() {
        txtBuscar.setText("");
        spDesde.setValue(new Date());
        spHasta.setValue(new Date());
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // Table Model
    // ============================================================
    private static class HabitacionesTableModel extends AbstractTableModel {

        private final String[] columnas = {
                "ID", "Nombre", "Tipo", "Capacidad", "Precio", "Estado"
        };

        private List<Object[]> filas = new ArrayList<>();

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        @Override public Object getValueAt(int row, int col) { return filas.get(row)[col]; }

        public void setHabitaciones(List<Object[]> datos) {
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

            ReservaStorage reservaStorage =
                    new ReservaStorage(conn, habitacionStorage, huespedStorage, facturaStorage);

            ReservaService reservaService = new ReservaService(reservaStorage);
            HabitacionService habitacionService = new HabitacionService(habitacionStorage, reservaService);

            PanelHabitaciones panel = new PanelHabitaciones();

            HabitacionesPresenter presenter =
                    new HabitacionesPresenter(habitacionService, panel);

            panel.setPresenter(presenter);
            presenter.cargarListado();

            JFrame frame = new JFrame("Test Panel Habitaciones");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 700);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }*/

}
