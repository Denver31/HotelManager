package aplicacion;

import aplicacion.facturaUi.PanelFacturas;
import aplicacion.habitacionUi.PanelHabitaciones;
import aplicacion.huespedUi.PanelHuespedes;
import aplicacion.reservaUi.PanelReservas;

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

    // Tablas
    private JTable tablaOcupacion;
    private JTable tablaEntradas;
    private JTable tablaSalidas;
    private JTable tablaPendientes;
    private JTable tablaVencidas;


    public PanelControl() {

        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildTitle(), BorderLayout.NORTH);
        add(buildMainLayout(), BorderLayout.CENTER);
    }

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

        // Izquierda → paneles informativos
        base.add(buildLeftPanels(), BorderLayout.CENTER);

        // Derecha → botones grandes
        base.add(buildButtonsColumn(), BorderLayout.EAST);

        return base;
    }

    // =====================================================================================
    // IZQUIERDA: PANEL DESPLEGADO CON LOS 5 ITEMS DEL DASHBOARD
    // =====================================================================================
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

        // ---- FILA 3 (facturas vencidas ocupa toda la fila) ----
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        left.add(createCardVencidas(), gbc);

        return left;
    }

    // =====================================================================================
    // PANEL OCUPACIÓN
    // =====================================================================================
    private JPanel createCardOcupacion() {
        JPanel card = buildCard("Ocupación");

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JLabel lbl = new JLabel("78 %");  // valor mock — luego se actualiza con Sistema
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lbl.setForeground(ACCENT);

        center.add(lbl);
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

    // =====================================================================================
    // DERECHA: COLUMNA DE BOTONES GRANDES
    // =====================================================================================
    private JPanel buildButtonsColumn() {

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10, 10, 10, 30));

        right.add(createBigButton("Reservas", () -> open(new PanelReservas())));
        right.add(Box.createVerticalStrut(20));

        right.add(createBigButton("Habitaciones", () -> open(new PanelHabitaciones())));
        right.add(Box.createVerticalStrut(20));

        right.add(createBigButton("Huéspedes", () -> open(new PanelHuespedes())));
        right.add(Box.createVerticalStrut(20));

        right.add(createBigButton("Facturas", () -> open(new PanelFacturas())));

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

    // =====================================================================================
    // UTILIDADES
    // =====================================================================================

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

    private void open(JPanel panel) {
        JFrame f = new JFrame();
        f.setTitle("Ventana");
        f.setSize(1000, 700);
        f.setLocationRelativeTo(null);
        f.setContentPane(panel);
        f.setVisible(true);
    }

    // ---------------- MAIN TEST -----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test PanelControl");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new PanelControl());
            frame.setVisible(true);
        });
    }
}