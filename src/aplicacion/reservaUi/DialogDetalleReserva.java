package aplicacion.reservaUi;

import dto.reserva.ReservaDetalleDTO;

import aplicacion.reservaUi.presenter.DetalleReservaPresenter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogDetalleReserva extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // MVP
    private DetalleReservaPresenter presenter;

    public void setPresenter(DetalleReservaPresenter presenter) {
        this.presenter = presenter;
    }

    // Labels
    private JLabel lblEstado, lblIngreso, lblEgreso;
    private JLabel lblHuespedId, lblNombreCompleto;
    private JLabel lblHabId, lblHabNombre, lblPrecioNoche;
    private JLabel lblTotal, lblFacturaId;

    // Botones
    private JButton btnCheckIn, btnCheckOut, btnCancelar;

    private final int idReserva;

    public DialogDetalleReserva(Window owner, int idReserva) {
        super(owner, "Detalle de Reserva", ModalityType.APPLICATION_MODAL);
        this.idReserva = idReserva;

        setSize(550, 720);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(500, 600));
        setBackground(BG);

        initComponents();
        setContentPane(buildMainPanel());
        initListeners();
    }

    private void initComponents() {

        lblEstado = createValueLabel();
        lblIngreso = createValueLabel();
        lblEgreso = createValueLabel();

        lblHuespedId = createValueLabel();
        lblNombreCompleto = createValueLabel();

        lblHabId = createValueLabel();
        lblHabNombre = createValueLabel();
        lblPrecioNoche = createValueLabel();

        lblTotal = createValueLabel();
        lblFacturaId = createValueLabel();

        btnCheckIn = createButton("CHECK-IN", new Color(0, 120, 215));
        btnCheckOut = createButton("CHECK-OUT", new Color(0, 160, 80));
        btnCancelar = createButton("CANCELAR", new Color(180, 40, 40));
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel();
        global.setLayout(new BoxLayout(global, BoxLayout.Y_AXIS));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Detalle de Reserva #" + idReserva);
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

        // -------- RESERVA --------
        content.add(sectionTitle("--- Reserva ---"));
        content.add(line("Estado: ", lblEstado));
        content.add(line("Ingreso: ", lblIngreso));
        content.add(line("Egreso: ", lblEgreso));

        // -------- HUESPED --------
        content.add(Box.createVerticalStrut(15));
        content.add(sectionTitle("--- Huésped ---"));
        content.add(line("Huésped ID: ", lblHuespedId));
        content.add(line("Nombre completo: ", lblNombreCompleto));

        // -------- HABITACION --------
        content.add(Box.createVerticalStrut(15));
        content.add(sectionTitle("--- Habitación ---"));
        content.add(line("Habitación ID: ", lblHabId));
        content.add(line("Nombre: ", lblHabNombre));
        content.add(line("Precio por noche: ", lblPrecioNoche));

        // -------- FACTURACION --------
        content.add(Box.createVerticalStrut(15));
        content.add(sectionTitle("--- Facturación ---"));
        content.add(line("Total: ", lblTotal));
        content.add(line("Factura ID: ", lblFacturaId));

        global.add(content);

        // -------- BOTONES --------
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        botones.setOpaque(false);

        botones.add(btnCheckIn);
        botones.add(btnCheckOut);
        botones.add(btnCancelar);

        global.add(Box.createVerticalStrut(15));
        global.add(botones);

        return global;
    }

    // === Helpers UI ===
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
        b.setPreferredSize(new Dimension(130, 32));
        return b;
    }

    private JLabel sectionTitle(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(SECTION_FONT);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(10, 0, 5, 0));
        return lbl;
    }

    private JPanel line(String label, JLabel valueLbl) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 3));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);

        p.add(lbl);
        p.add(valueLbl);

        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    // === Listeners delegados a Presenter ===
    private void initListeners() {

        btnCheckIn.addActionListener(e -> {
            if (presenter != null) presenter.onCheckIn();
        });

        btnCheckOut.addActionListener(e -> {
            if (presenter != null) presenter.onCheckOut();
        });

        btnCancelar.addActionListener(e -> {
            if (presenter != null) presenter.onCancelar();
        });
    }

    // === Métodos usados por el Presenter ===
    public void mostrarDatos(ReservaDetalleDTO dto) {
        lblEstado.setText(dto.estado());
        lblIngreso.setText(dto.desde().toString());
        lblEgreso.setText(dto.hasta().toString());

        lblHuespedId.setText(String.valueOf(dto.idHuesped()));
        lblNombreCompleto.setText(dto.nombreCompletoHuesped());

        lblHabId.setText(String.valueOf(dto.idHabitacion()));
        lblHabNombre.setText(dto.nombreHabitacion());
        lblPrecioNoche.setText("$" + dto.totalFactura() / calcularNoches(dto));

        lblTotal.setText("$" + dto.totalFactura());
        lblFacturaId.setText(String.valueOf(dto.idFactura()));

        setBotonesSegunEstado(dto.estado());
    }

    private long calcularNoches(ReservaDetalleDTO dto) {
        return java.time.temporal.ChronoUnit.DAYS.between(dto.desde(), dto.hasta());
    }

    public void actualizarEstado(String nuevoEstado) {
        lblEstado.setText(nuevoEstado);
        setBotonesSegunEstado(nuevoEstado);
    }

    public void setBotonesSegunEstado(String estado) {
        btnCheckIn.setEnabled(estado.equals("PENDIENTE") || estado.equals("CONFIRMADA"));
        btnCheckOut.setEnabled(estado.equals("ACTIVA"));
        btnCancelar.setEnabled(estado.equals("PENDIENTE") || estado.equals("CONFIRMADA"));
    }

    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void cerrar() {
        dispose();
    }
}
