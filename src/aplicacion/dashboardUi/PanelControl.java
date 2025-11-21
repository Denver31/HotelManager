package aplicacion.dashboardUi;

import aplicacion.Sistema;
import aplicacion.facturaUi.PanelFacturas;
import aplicacion.facturaUi.presenter.FacturasPresenter;
import aplicacion.habitacionUi.PanelHabitaciones;
import aplicacion.habitacionUi.presenter.HabitacionesPresenter;
import aplicacion.huespedUi.PanelHuespedes;
import aplicacion.huespedUi.presenter.HuespedesPresenter;
import aplicacion.reservaUi.PanelReservas;
import aplicacion.reservaUi.presenter.ReservasPresenter;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel principal del sistema (Panel de Control).
 * Solo layout visual — no se conecta aún con Sistema.
 * Los botones abren los paneles principales.
 */
public class PanelControl extends JPanel {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    // Presenter
    private PanelControlPresenter presenter;

    private final Sistema sistema;

    // Componentes que el presenter actualizará
    private JLabel lblOcupacion;

    private JTable tablaEntradas;
    private JTable tablaSalidas;
    private JTable tablaPendientes;
    private JTable tablaVencidas;

    public PanelControl(Sistema sistema) {
        this.sistema = sistema;

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildTitle(), BorderLayout.NORTH);
        add(buildMainLayout(), BorderLayout.CENTER);
    }

    // ============================================================
    // Inyección MVP
    // ============================================================
    public void setPresenter(PanelControlPresenter presenter) {
        this.presenter = presenter;
        presenter.cargarDashboard();
    }

    // ============================================================
    // UI
    // ============================================================
    private JComponent buildTitle() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        p.setOpaque(false);

        JLabel titulo = new JLabel("Panel de Control");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);

        p.add(titulo);
        return p;
    }

    private JPanel buildMainLayout() {

        JPanel base = new JPanel(new BorderLayout(20, 0));
        base.setOpaque(false);

        base.add(buildLeftPanels(), BorderLayout.CENTER);
        base.add(buildButtonsColumn(), BorderLayout.EAST);

        return base;
    }

    private JPanel buildLeftPanels() {

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        // ---- FILA 1 ----
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weighty = 0.25;
        left.add(createCardOcupacion(), gbc);

        gbc.gridx = 1;
        left.add(createCardEntradas(), gbc);

        // ---- FILA 2 ----
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weighty = 0.25;
        left.add(createCardSalidas(), gbc);

        gbc.gridx = 1;
        left.add(createCardPendientes(), gbc);

        // ---- FILA 3 ----
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        left.add(createCardVencidas(), gbc);

        return left;
    }

    // ============================================================
    // PANEL OCUPACIÓN
    // ============================================================
    private JPanel createCardOcupacion() {
        JPanel card = buildCard("Ocupación");

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        lblOcupacion = new JLabel("— %");
        lblOcupacion.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblOcupacion.setForeground(ACCENT);

        center.add(lblOcupacion);
        card.add(center, BorderLayout.CENTER);

        return card;
    }

    private JPanel createCardEntradas() {
        JPanel card = buildCard("Próximas entradas");

        tablaEntradas = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Fecha", "ID Reserva", "Huésped", "Hab."}
        ));

        card.add(new JScrollPane(tablaEntradas), BorderLayout.CENTER);
        return card;
    }

    private JPanel createCardSalidas() {
        JPanel card = buildCard("Próximas salidas");

        tablaSalidas = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Fecha", "ID Reserva", "Huésped", "Hab."}
        ));

        card.add(new JScrollPane(tablaSalidas), BorderLayout.CENTER);
        return card;
    }

    private JPanel createCardPendientes() {
        JPanel card = buildCard("Reservas pendientes de cobro");

        tablaPendientes = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"ID Reserva", "Huésped", "Factura", "Monto"}
        ));

        card.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);
        return card;
    }

    private JPanel createCardVencidas() {
        JPanel card = buildCard("Facturas vencidas");

        tablaVencidas = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"ID Reserva", "Huésped", "Factura", "Monto"}
        ));

        card.add(new JScrollPane(tablaVencidas), BorderLayout.CENTER);
        return card;
    }

    // ============================================================
    // BOTONES DERECHA
    // ============================================================
    private JPanel buildButtonsColumn() {

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10, 10, 10, 30));

        right.add(createBigButton("Reservas", () -> presenter.abrirReservas()));
        right.add(Box.createVerticalStrut(20));

        right.add(createBigButton("Habitaciones", () -> presenter.abrirHabitaciones()));
        right.add(Box.createVerticalStrut(20));

        right.add(createBigButton("Huéspedes", () -> presenter.abrirHuespedes()));
        right.add(Box.createVerticalStrut(20));

        right.add(createBigButton("Facturas", () -> presenter.abrirFacturas()));

        return right;
    }

    private JButton createBigButton(String text, Runnable action) {
        JButton btn = new JButton(text);

        btn.setPreferredSize(new Dimension(200, 120));
        btn.setMaximumSize(new Dimension(200, 120));
        btn.setMinimumSize(new Dimension(200, 120));

        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);

        btn.addActionListener(e -> action.run());
        return btn;
    }

    // ============================================================
    // MÉTODOS PARA ABRIR PANELES (CORRECTOS)
    // ============================================================
    private void abrirReservas() {
        PanelReservas panel = new PanelReservas();
        ReservasPresenter presenter =
                new ReservasPresenter(panel, sistema.reservaService,  sistema.huespedService, sistema.habitacionService, sistema.facturaService);

        panel.setPresenter(presenter);
        presenter.cargarListado();

        abrirVentana(panel, "Reservas");
    }

    private void abrirHabitaciones() {
        PanelHabitaciones panel = new PanelHabitaciones();
        HabitacionesPresenter presenter =
                new HabitacionesPresenter(sistema.habitacionService, panel);

        panel.setPresenter(presenter);
        presenter.cargarListado();

        abrirVentana(panel, "Habitaciones");
    }

    private void abrirHuespedes() {
        PanelHuespedes panel = new PanelHuespedes();
        HuespedesPresenter presenter =
                new HuespedesPresenter(sistema.huespedService, panel);

        panel.setPresenter(presenter);
        presenter.cargarListado();

        abrirVentana(panel, "Huéspedes");
    }

    private void abrirFacturas() {
        PanelFacturas panel = new PanelFacturas();
        FacturasPresenter presenter =
                new FacturasPresenter(panel, sistema.facturaService);

        panel.setPresenter(presenter);
        presenter.cargarFacturas();

        abrirVentana(panel, "Facturas");
    }

    public void abrirVentana(JPanel panel, String titulo) {
        JFrame f = new JFrame(titulo);
        f.setSize(1000, 700);
        f.setLocationRelativeTo(null);
        f.setContentPane(panel);
        f.setVisible(true);
    }

    // ============================================================
    // UTILIDADES
    // ============================================================
    private JPanel buildCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(CARD_TITLE_FONT);
        lbl.setForeground(new Color(40, 40, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(lbl, BorderLayout.WEST);

        card.add(header, BorderLayout.NORTH);
        return card;
    }

    // ============================================================
    // GETTERS PARA PRESENTER
    // ============================================================
    public JLabel getLblOcupacion() { return lblOcupacion; }
    public JTable getTablaEntradas() { return tablaEntradas; }
    public JTable getTablaSalidas() { return tablaSalidas; }
    public JTable getTablaPendientes() { return tablaPendientes; }
    public JTable getTablaVencidas() { return tablaVencidas; }

}