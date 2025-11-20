package aplicacion.facturaUi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogDetalleFactura extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final int idFactura;

    // Labels
    private JLabel lblEstado;
    private JLabel lblReservaId;
    private JLabel lblHuespedId;
    private JLabel lblNombreCompleto;
    private JLabel lblFechaAlta;
    private JLabel lblFechaVencimiento;
    private JLabel lblTotal;

    // Botón
    private JButton btnRegistrarPago;

    public DialogDetalleFactura(Window owner, int idFactura) {
        super(owner, "Detalle de Factura", ModalityType.APPLICATION_MODAL);
        this.idFactura = idFactura;

        setSize(550, 600);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(500, 500));
        setBackground(BG);

        initComponents();
        setContentPane(buildMainPanel());
        initListeners();

        cargarMockDatos(); // luego se reemplaza por controller
    }

    private void initComponents() {

        lblEstado = createValueLabel();
        lblReservaId = createValueLabel();
        lblHuespedId = createValueLabel();
        lblNombreCompleto = createValueLabel();
        lblFechaAlta = createValueLabel();
        lblFechaVencimiento = createValueLabel();
        lblTotal = createValueLabel();

        btnRegistrarPago = createButton("REGISTRAR PAGO", new Color(0, 160, 80));
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel();
        global.setLayout(new BoxLayout(global, BoxLayout.Y_AXIS));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Detalle de Factura #" + idFactura);
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        titulo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        titulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        global.add(titulo);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Estado
        content.add(line("Estado: ", lblEstado));
        content.add(Box.createVerticalStrut(10));

        // Reserva
        content.add(line("Reserva ID: ", lblReservaId));
        content.add(Box.createVerticalStrut(10));

        // Huésped
        content.add(line("Huésped ID: ", lblHuespedId));
        content.add(line("Nombre completo: ", lblNombreCompleto));
        content.add(Box.createVerticalStrut(10));

        // Fechas
        content.add(line("Fecha de alta: ", lblFechaAlta));
        content.add(line("Fecha de vencimiento: ", lblFechaVencimiento));
        content.add(Box.createVerticalStrut(10));

        // Total
        content.add(line("Total: ", lblTotal));

        global.add(content);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        botones.setOpaque(false);
        botones.add(btnRegistrarPago);

        global.add(Box.createVerticalStrut(15));
        global.add(botones);

        return global;
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("-");
        lbl.setFont(LABEL_FONT);
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(160, 32));
        return b;
    }

    private JPanel line(String label, JLabel valueLbl) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);

        p.add(lbl);
        p.add(valueLbl);

        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    private void initListeners() {

        btnRegistrarPago.addActionListener(e -> {
            // TODO: llamar a FacturaController.registrarPago(idFactura)
            if (confirm("¿Registrar pago de esta factura?")) {
                lblEstado.setText("PAGADA");
                actualizarBoton("PAGADA");
                JOptionPane.showMessageDialog(this,
                        "Pago registrado correctamente.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // MOCK hasta tener controller real
    private void cargarMockDatos() {

        // En la versión real, acá cargarías desde el controller usando idFactura
        lblEstado.setText("PENDIENTE");
        lblReservaId.setText("77");
        lblHuespedId.setText("55");
        lblNombreCompleto.setText("Juan Pérez");
        lblFechaAlta.setText("2025-11-10");
        lblFechaVencimiento.setText("2025-11-15");
        lblTotal.setText("$50.000");

        actualizarBoton("PENDIENTE");
    }

    private void actualizarBoton(String estado) {
        boolean pendiente = "PENDIENTE".equalsIgnoreCase(estado);
        btnRegistrarPago.setEnabled(pendiente);
    }
}