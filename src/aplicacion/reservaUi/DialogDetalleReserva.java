package aplicacion.reservaUi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogDetalleReserva extends JDialog {

    // Estilos suaves y fáciles de editar
    private static final Color BG = new Color(245, 247, 250);
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Labels (datos)
    private JLabel lblEstado, lblIngreso, lblEgreso;
    private JLabel lblHuespedId, lblNombre, lblApellido;
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

        cargarMockDatos(); // luego se reemplaza por controller.cargar()
    }

    private void initComponents() {
        // Datos reserva
        lblEstado = createValueLabel();
        lblIngreso = createValueLabel();
        lblEgreso = createValueLabel();

        // Datos huésped
        lblHuespedId = createValueLabel();
        lblNombre = createValueLabel();
        lblApellido = createValueLabel();

        // Datos habitación
        lblHabId = createValueLabel();
        lblHabNombre = createValueLabel();
        lblPrecioNoche = createValueLabel();

        // Facturación
        lblTotal = createValueLabel();
        lblFacturaId = createValueLabel();

        // Botones
        btnCheckIn = createButton("CHECK-IN", new Color(0, 120, 215));
        btnCheckOut = createButton("CHECK-OUT", new Color(0, 160, 80));
        btnCancelar = createButton("CANCELAR", new Color(180, 40, 40));
    }

    // ==== UI PRINCIPAL ====
    private JPanel buildMainPanel() {

        JPanel global = new JPanel();
        global.setLayout(new BoxLayout(global, BoxLayout.Y_AXIS));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Título (alineado a la izquierda)
        JLabel titulo = new JLabel("Detalle de Reserva #" + idReserva);
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        titulo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        titulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        global.add(titulo);

        // Panel central con contenido centrado pero texto a la izquierda
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        // ========== SECCIÓN RESERVA ==========
        content.add(sectionTitle("--- Reserva ---"));
        content.add(line("Estado: ", lblEstado));
        content.add(line("Ingreso: ", lblIngreso));
        content.add(line("Egreso: ", lblEgreso));

        // ========== SECCIÓN HUESPED ==========
        content.add(Box.createVerticalStrut(15));
        content.add(sectionTitle("--- Huésped ---"));
        content.add(line("Huésped ID: ", lblHuespedId));
        content.add(line("Nombre: ", lblNombre));
        content.add(line("Apellido: ", lblApellido));

        // ========== SECCIÓN HABITACIÓN ==========
        content.add(Box.createVerticalStrut(15));
        content.add(sectionTitle("--- Habitación ---"));
        content.add(line("Habitación ID: ", lblHabId));
        content.add(line("Nombre: ", lblHabNombre));
        content.add(line("Precio por noche: ", lblPrecioNoche));

        // ========== SECCIÓN FACTURACIÓN ==========
        content.add(Box.createVerticalStrut(15));
        content.add(sectionTitle("--- Facturación ---"));
        content.add(line("Total: ", lblTotal));
        content.add(line("Factura ID: ", lblFacturaId));

        global.add(content);

        // ==== BOTONES ABAJO ====
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        botones.setOpaque(false);

        botones.add(btnCheckIn);
        botones.add(btnCheckOut);
        botones.add(btnCancelar);

        global.add(Box.createVerticalStrut(15));
        global.add(botones);

        return global;
    }

    // ==== UTILIDADES ====
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

    // ==== LISTENERS ====
    private void initListeners() {

        btnCheckIn.addActionListener(e -> {
            if (confirm("¿Confirmar check-in?")) {
                JOptionPane.showMessageDialog(this, "Check-in realizado.");
            }
        });

        btnCheckOut.addActionListener(e -> {
            if (confirm("¿Confirmar check-out?")) {
                JOptionPane.showMessageDialog(this, "Check-out realizado.");
            }
        });

        btnCancelar.addActionListener(e -> {
            if (confirm("¿Cancelar esta reserva?")) {
                JOptionPane.showMessageDialog(this, "Reserva cancelada.");
            }
        });
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // === MOCK HASTA TENER CONTROLLER ===
    private void cargarMockDatos() {

        lblEstado.setText("CONFIRMADA");
        lblIngreso.setText("2025-11-10");
        lblEgreso.setText("2025-11-15");

        lblHuespedId.setText("55");
        lblNombre.setText("Juan");
        lblApellido.setText("Pérez");

        lblHabId.setText("7");
        lblHabNombre.setText("Doble 7");
        lblPrecioNoche.setText("$10.000");

        lblTotal.setText("$50.000");
        lblFacturaId.setText("215");

        // Habilitar botones según estado MOCK
        actualizarBotones("CONFIRMADA");
    }

    private void actualizarBotones(String estado) {
        btnCheckIn.setEnabled(estado.equals("PENDIENTE") || estado.equals("CONFIRMADA"));
        btnCheckOut.setEnabled(estado.equals("ACTIVA"));
        btnCancelar.setEnabled(estado.equals("PENDIENTE") || estado.equals("CONFIRMADA"));
    }
}
