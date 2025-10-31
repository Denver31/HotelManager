package src.Interface;

import src.classes.Sistema;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class HotelDashboard extends JFrame {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font  TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font  CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font  LIST_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final Sistema sistema;

    private JLabel ocupacionValueLbl;
    private DefaultListModel<String> entradasModel;
    private DefaultListModel<String> salidasModel;
    private DefaultListModel<String> facturasVencidasModel;
    private JTable pendientesTable;

    public HotelDashboard(Sistema sistema) {
        this.sistema = sistema;
        setupFrame();
        add(buildMainPanel());
    }

    private void setupFrame() {
        setTitle("Hotel Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG);
        GridBagConstraints gbc = baseGbc();

        gbc = place(main, createTitle("📊 Dashboard - Resumen"), gbc, 0, 0, 3, 1, 1, 0);

        gbc = place(main, createOcupacionPanel(), gbc, 0, 1, 1, 1, .5, .2);

        gbc = place(main, createEntradasPanel(), gbc, 1, 1, 1, 1, 1, .2);

        gbc = place(main, createAccesosPanel(), gbc, 2, 1, 1, 3, .3, 1);

        gbc = place(main, createSalidasPanel(), gbc, 0, 2, 1, 1, .5, .2);

        gbc = place(main, createPendientesPanel(), gbc, 1, 2, 1, 1, 1, .2);

        gbc = place(main, createFacturasPanel(), gbc, 0, 3, 2, 1, 1, .4);

        refreshOcupacion();
        refreshEntradas();
        refreshSalidas();
        refreshFacturasVencidas();
        refreshPendientes();

        return main;
    }

    private JPanel createOcupacionPanel() {
        JPanel card = createCard("Ocupación", this::refreshOcupacion, true);
        ocupacionValueLbl = new JLabel("Cargando…", SwingConstants.CENTER);
        ocupacionValueLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        card.add(ocupacionValueLbl, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEntradasPanel() {
        JPanel card = createCard("Próximas entradas", this::refreshEntradas, true);
        entradasModel = new DefaultListModel<>();
        card.add(createList(entradasModel), BorderLayout.CENTER);
        return card;
    }

    private JPanel createSalidasPanel() {
        JPanel card = createCard("Próximas salidas", this::refreshSalidas, true);
        salidasModel = new DefaultListModel<>();
        card.add(createList(salidasModel), BorderLayout.CENTER);
        return card;
    }

    private JPanel createAccesosPanel() {
        JPanel card = createCard("Accesos rápidos", null, false);
        card.setLayout(new GridLayout(4, 1, 10, 10));

        addActionButton(card, "Nueva reserva", () ->
                SwingUtilities.invokeLater(() -> new NuevaReservaForm(sistema).setVisible(true)));

        addActionButton(card, "Agregar Habitacion", () ->
                SwingUtilities.invokeLater(() -> new NuevaHabitacionForm(sistema).setVisible(true)));
        addActionButton(card, "Pagas", () ->
                SwingUtilities.invokeLater(() -> new NuevoPagoForm(sistema).setVisible(true)));

        return card;
    }

    private JPanel createPendientesPanel() {
        JPanel card = createCard("Reservas pendientes de cobro", this::refreshPendientes, true);

        pendientesTable = new JTable(new FacturasPendientesTableModel(sistema));
        configureTablaPendientesSorter(pendientesTable);

        JScrollPane scroll = new JScrollPane(pendientesTable);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFacturasPanel() {
        JPanel card = createCard("Facturas vencidas", this::refreshFacturasVencidas, true);
        facturasVencidasModel = new DefaultListModel<>();
        card.add(createList(facturasVencidasModel), BorderLayout.CENTER);
        return card;
    }

    private void refreshOcupacion() {
        // TODO: заменить на реальные данные из sistema (например, sistema.getOcupacionPorcentaje())
        int porcentaje = 72;
        ocupacionValueLbl.setText("Ocupación: " + porcentaje + "%");
    }

    private void refreshEntradas() {
        ArrayList<Sistema.Movimiento> entradas = sistema.getEntradas();

        entradasModel.clear();
        for (Sistema.Movimiento m : entradas) entradasModel.addElement("• " + m.getHuespede() + "-" + m.getFecha());
    }

    private void refreshSalidas() {
        ArrayList<Sistema.Movimiento> salidas = sistema.getSalidas();

        salidasModel.clear();
        for (Sistema.Movimiento m : salidas) salidasModel.addElement("• " + m.getHuespede() + "-" + m.getFecha());
    }

    private void refreshFacturasVencidas() {
        // TODO: cargar desde sistema.getFacturasVencidas()
        String[] facturas = {
                "Factura #120 - $200 - vencida",
                "Factura #121 - $50 - vencida"
        };
        facturasVencidasModel.clear();
        for (String s : facturas) facturasVencidasModel.addElement("• " + s);
    }

    private void refreshPendientes() {
       FacturasPendientesTableModel model = new FacturasPendientesTableModel(sistema);
        pendientesTable.setModel(model);
        configureTablaPendientesSorter(pendientesTable);
        pendientesTable.revalidate();
        pendientesTable.repaint();
    }

    private JPanel createCard(String title, Runnable onRefresh, boolean showRefresh) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(CARD_TITLE_FONT);
        lbl.setForeground(new Color(50, 50, 50));
        header.add(lbl, BorderLayout.WEST);

        if (showRefresh) {
            JButton refreshBtn = new JButton("↻ Actualizar");
            refreshBtn.setFocusPainted(false);
            refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            refreshBtn.addActionListener(e -> {
                if (onRefresh != null) onRefresh.run();
            });
            header.add(refreshBtn, BorderLayout.EAST);
        }

        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private JScrollPane createList(DefaultListModel<String> model) {
        JList<String> list = new JList<>(model);
        list.setFont(LIST_FONT);
        list.setBackground(new Color(250, 250, 250));
        list.setVisibleRowCount(4);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(300, 100));
        return scroll;
    }

    private JLabel createTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(TITLE_FONT);
        lbl.setForeground(ACCENT);
        return lbl;
    }

    private void addActionButton(JPanel parent, String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.addActionListener(e -> action.run());
        parent.add(btn);
    }

    private void configureTablaPendientesSorter(JTable tabla) {
        TableRowSorter<FacturasPendientesTableModel> sorter =
                new TableRowSorter<>((FacturasPendientesTableModel) tabla.getModel());
        sorter.setComparator(0, Comparator.naturalOrder()); // ID
        sorter.setComparator(1, Comparator.comparingDouble(d -> (Double) d)); // Total
        sorter.setComparator(2, Comparator.comparingDouble(d -> (Double) d)); // Pagado
        sorter.setComparator(3, Comparator.comparingDouble(d -> (Double) d)); // Restante
        sorter.setComparator(4, Comparator.naturalOrder()); // Pagar hasta (ISO-строка)
        tabla.setRowSorter(sorter);
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

    private GridBagConstraints place(JPanel parent, JComponent comp, GridBagConstraints gbc,
                                     int x, int y, int w, int h, double wx, double wy) {
        GridBagConstraints g = (GridBagConstraints) gbc.clone();
        g.gridx = x; g.gridy = y; g.gridwidth = w; g.gridheight = h;
        g.weightx = wx; g.weighty = wy;
        parent.add(comp, g);
        return gbc;
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        Sistema sistema = new Sistema("hotel.db", "hotel.db", "hotel.db", "hotel.db");
        SwingUtilities.invokeLater(() -> new HotelDashboard(sistema).setVisible(true));
    }
}
