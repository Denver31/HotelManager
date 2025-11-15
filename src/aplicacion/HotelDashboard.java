package aplicacion;

import aplicacion.Sistema;
import dominio.Factura;
import dominio.Reserva;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Panel principal del sistema (Dashboard).
 * Conserva la estética original con paneles blancos, título azul y accesos rápidos.
 * El botón "Actualizar" (en Ocupación) actualiza todos los paneles.
 */
public class HotelDashboard extends JFrame {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LIST_FONT = new Font("Segoe UI", Font.PLAIN, 14);

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
        setTitle("Panel de Control - Sistema Hotelero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG);
        GridBagConstraints gbc = baseGbc();

        gbc = place(main, createTitle("📊 Panel de Control"), gbc, 0, 0, 3, 1, 1, 0);

        gbc = place(main, createOcupacionPanel(), gbc, 0, 1, 1, 1, .5, .2);
        gbc = place(main, createEntradasPanel(), gbc, 1, 1, 1, 1, 1, .2);
        gbc = place(main, createAccesosPanel(), gbc, 2, 1, 1, 3, .3, 1);
        gbc = place(main, createSalidasPanel(), gbc, 0, 2, 1, 1, .5, .2);
        gbc = place(main, createPendientesPanel(), gbc, 1, 2, 1, 1, 1, .2);
        gbc = place(main, createFacturasPanel(), gbc, 0, 3, 2, 1, 1, .4);

        // carga inicial
        refreshAll();

        return main;
    }

    // =================== PANELES =====================

    private JPanel createOcupacionPanel() {
        JPanel card = createCard("Ocupación", this::refreshAll, true);
        ocupacionValueLbl = new JLabel("Cargando…", SwingConstants.CENTER);
        ocupacionValueLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        card.add(ocupacionValueLbl, BorderLayout.CENTER);
        return card;
    }

    private JPanel createEntradasPanel() {
        JPanel card = createCard("Próximas entradas", null, false);
        entradasModel = new DefaultListModel<>();
        card.add(createList(entradasModel), BorderLayout.CENTER);
        return card;
    }

    private JPanel createSalidasPanel() {
        JPanel card = createCard("Próximas salidas", null, false);
        salidasModel = new DefaultListModel<>();
        card.add(createList(salidasModel), BorderLayout.CENTER);
        return card;
    }

    private JPanel createAccesosPanel() {
        JPanel card = createCard("Accesos rápidos", null, false);
        card.setLayout(new GridLayout(4, 1, 10, 10));

        addActionButton(card, "Nueva reserva", () ->
                SwingUtilities.invokeLater(() -> new NuevaReservaForm(sistema).setVisible(true)));

        addActionButton(card, "Agregar habitación", () ->
                SwingUtilities.invokeLater(() -> new NuevaHabitacionForm(sistema).setVisible(true)));

        addActionButton(card, "Pagos", () ->
                SwingUtilities.invokeLater(() -> new NuevoPagoForm(sistema).setVisible(true)));

        return card;
    }

    private JPanel createPendientesPanel() {
        JPanel card = createCard("Reservas pendientes de cobro", null, false);
        pendientesTable = new JTable();
        JScrollPane scroll = new JScrollPane(pendientesTable);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFacturasPanel() {
        JPanel card = createCard("Facturas vencidas", null, false);
        facturasVencidasModel = new DefaultListModel<>();
        card.add(createList(facturasVencidasModel), BorderLayout.CENTER);
        return card;
    }

    // =================== REFRESCOS =====================

    private void refreshAll() {
        refreshOcupacion();
        refreshEntradas();
        refreshSalidas();
        refreshFacturasVencidas();
        refreshPendientes();
    }

    private void refreshOcupacion() {
        try {
            double porcentaje = sistema.getOcupacionHoy();
            ocupacionValueLbl.setText(String.format("Ocupación: %.2f%%", porcentaje));
        } catch (Exception e) {
            ocupacionValueLbl.setText("Error al cargar ocupación");
        }
    }

    private void refreshEntradas() {
        try {
            entradasModel.clear();
            LocalDate hoy = LocalDate.now();
            List<Reserva> entradas = sistema.listarProximasEntradas(hoy, hoy.plusDays(7));
            if (entradas.isEmpty()) {
                entradasModel.addElement("• No hay próximas entradas.");
            } else {
                for (Reserva r : entradas) {
                    entradasModel.addElement(String.format("• %s (%s) - Desde: %s",
                            r.getHuesped().getNombreCompleto(),
                            r.getHabitacion().getNombre(),
                            r.getDesde()));
                }
            }
        } catch (Exception e) {
            entradasModel.clear();
            entradasModel.addElement("Error al cargar entradas");
        }
    }

    private void refreshSalidas() {
        try {
            salidasModel.clear();
            LocalDate hoy = LocalDate.now();
            List<Reserva> salidas = sistema.listarProximasSalidas(hoy, hoy.plusDays(7));
            if (salidas.isEmpty()) {
                salidasModel.addElement("• No hay próximas salidas.");
            } else {
                for (Reserva r : salidas) {
                    salidasModel.addElement(String.format("• %s (%s) - Hasta: %s",
                            r.getHuesped().getNombreCompleto(),
                            r.getHabitacion().getNombre(),
                            r.getHasta()));
                }
            }
        } catch (Exception e) {
            salidasModel.clear();
            salidasModel.addElement("Error al cargar salidas");
        }
    }

    private void refreshFacturasVencidas() {
        try {
            facturasVencidasModel.clear();
            List<Factura> facturas = sistema.getFacturasVencidas(LocalDate.now());
            if (facturas.isEmpty()) {
                facturasVencidasModel.addElement("• No hay facturas vencidas.");
            } else {
                for (Factura f : facturas) {
                    facturasVencidasModel.addElement(String.format(
                            "• Factura #%d - $%.2f - vencida", f.getId(), f.getTotal()));
                }
            }
        } catch (Exception e) {
            facturasVencidasModel.clear();
            facturasVencidasModel.addElement("Error al cargar facturas vencidas");
        }
    }

    private void refreshPendientes() {
        try {
            var reservas = sistema.getReservasPendientesDeCobro(LocalDate.now());

            var model = new javax.swing.table.DefaultTableModel(
                    new Object[]{"ID", "Cliente", "Habitación", "Total", "Vence"}, 0);

            for (Reserva r : reservas) {
                var f = r.getFactura();

                // Solo mostramos si la factura está impaga
                if (!f.isPagada()) {
                    model.addRow(new Object[]{
                            r.getId(),
                            r.getHuesped().getNombreCompleto(),
                            r.getHabitacion().getNombre(),
                            String.format("$%.2f", f.getTotal()),
                            f.getVencimiento().toString()
                    });
                }
            }

            pendientesTable.setModel(model);
            configureTablaPendientesSorter(pendientesTable);
            pendientesTable.revalidate();
            pendientesTable.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar reservas pendientes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // =================== COMPONENTES REUTILIZABLES =====================

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
            refreshBtn.addActionListener(e -> refreshAll());
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
        TableRowSorter sorter = new TableRowSorter(tabla.getModel());
        sorter.setComparator(0, Comparator.comparingInt(id -> Integer.parseInt(id.toString())));
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
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = w;
        g.gridheight = h;
        g.weightx = wx;
        g.weighty = wy;
        parent.add(comp, g);
        return gbc;
    }

    // =================== MAIN =====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelDashboard(new Sistema()).setVisible(true));
    }
}