package aplicacion.facturaUi;

import aplicacion.facturaUi.presenter.DetalleFacturaPresenter;
import dto.FacturaDetalleDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogDetalleFactura extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final int idFactura;

    // Presenter
    private DetalleFacturaPresenter presenter;

    // Labels
    private JLabel lblEstado;
    private JLabel lblReservaId;
    private JLabel lblHuespedId;
    private JLabel lblNombreCompleto;
    private JLabel lblFechaAlta;
    private JLabel lblFechaVencimiento;
    private JLabel lblTotal;

    // Nuevos labels
    private JLabel lblMetodoPago;
    private JLabel lblCuotas;
    private JLabel lblFechaPago;

    // Botón
    private JButton btnRegistrarPago;

    // ============================================================
    // Constructor
    // ============================================================
    public DialogDetalleFactura(Window owner, int idFactura) {
        super(owner, "Detalle de Factura", ModalityType.APPLICATION_MODAL);
        this.idFactura = idFactura;

        setSize(550, 650);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(500, 550));
        setBackground(BG);

        initComponents();
        setContentPane(buildMainPanel());
        initListeners();
    }

    // ============================================================
    // Inyección del Presenter
    // ============================================================
    public void setPresenter(DetalleFacturaPresenter presenter) {
        this.presenter = presenter;
        presenter.cargarFactura(idFactura);
    }

    // ============================================================
    // Componentes UI
    // ============================================================
    private void initComponents() {

        lblEstado = createValueLabel();
        lblReservaId = createValueLabel();
        lblHuespedId = createValueLabel();
        lblNombreCompleto = createValueLabel();
        lblFechaAlta = createValueLabel();
        lblFechaVencimiento = createValueLabel();
        lblTotal = createValueLabel();

        // Nuevos labels
        lblMetodoPago = createValueLabel();
        lblCuotas = createValueLabel();
        lblFechaPago = createValueLabel();

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
        content.add(Box.createVerticalStrut(10));

        // ============================================================
        // NUEVA SECCIÓN: Información de pago
        // ============================================================

        content.add(line("Método de pago: ", lblMetodoPago));
        content.add(Box.createVerticalStrut(10));

        content.add(line("Cuotas: ", lblCuotas));
        content.add(Box.createVerticalStrut(10));

        content.add(line("Fecha de pago: ", lblFechaPago));
        content.add(Box.createVerticalStrut(20));

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

    // ============================================================
    // Listeners
    // ============================================================
    private void initListeners() {
        btnRegistrarPago.addActionListener(e -> {
            if (confirm("¿Registrar pago de esta factura?")) {
                presenter.registrarPago(idFactura);
            }
        });
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(
                this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }

    // ============================================================
    // Métodos llamados por el Presenter
    // ============================================================
    public void mostrarDetalle(FacturaDetalleDTO dto) {

        lblEstado.setText(dto.estado());
        lblReservaId.setText(String.valueOf(dto.idReserva()));
        lblHuespedId.setText(String.valueOf(dto.idHuesped()));
        lblNombreCompleto.setText(dto.nombreCompleto());
        lblFechaAlta.setText(dto.fechaAlta());
        lblFechaVencimiento.setText(dto.fechaVencimiento());
        lblTotal.setText(dto.total());

        // Nuevos
        lblMetodoPago.setText(dto.metodoPago());
        lblCuotas.setText(String.valueOf(dto.cuotas()));
        lblFechaPago.setText(dto.fechaPago());

        actualizarBoton(dto.estado());
    }

    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarBoton(String estado) {
        boolean pendiente = "PENDIENTE".equalsIgnoreCase(estado);
        btnRegistrarPago.setEnabled(pendiente);
    }
}