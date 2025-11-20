package aplicacion.habitacionUi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogDetalleHabitacion extends JDialog {

    // Estilos
    private static final Color BG = new Color(245, 247, 250);
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Labels
    private JLabel lblNombre, lblTipo, lblCapacidad, lblDescripcion,
            lblPrecio, lblEstado;

    private JButton btnBaja, btnReactivar;

    private final int idHabitacion;

    public DialogDetalleHabitacion(Window owner, int idHabitacion) {
        super(owner, "Detalle de Habitación", ModalityType.APPLICATION_MODAL);
        this.idHabitacion = idHabitacion;

        setSize(550, 650);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(500, 550));
        setBackground(BG);

        initComponents();
        setContentPane(buildMainPanel());
        initListeners();
        cargarMockDatos();
    }

    private void initComponents() {

        lblNombre = createValueLabel();
        lblTipo = createValueLabel();
        lblCapacidad = createValueLabel();
        lblDescripcion = createValueLabel();
        lblPrecio = createValueLabel();
        lblEstado = createValueLabel();

        btnBaja = createButton("DAR DE BAJA", new Color(180, 40, 40));
        btnReactivar = createButton("REACTIVAR", new Color(0, 160, 80));
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel();
        global.setLayout(new BoxLayout(global, BoxLayout.Y_AXIS));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Detalle de Habitación #" + idHabitacion);
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

        content.add(sectionTitle("--- Habitación ---"));
        content.add(line("Nombre: ", lblNombre));
        content.add(line("Tipo: ", lblTipo));
        content.add(line("Capacidad: ", lblCapacidad));
        content.add(line("Descripción: ", lblDescripcion));
        content.add(line("Precio: ", lblPrecio));
        content.add(line("Estado: ", lblEstado));

        global.add(content);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        botones.setOpaque(false);
        botones.add(btnBaja);
        botones.add(btnReactivar);

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
        b.setPreferredSize(new Dimension(140, 32));
        return b;
    }

    private JLabel sectionTitle(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(SECTION_FONT);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(10, 0, 5, 0));
        return lbl;
    }

    private JPanel line(String name, JLabel value) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(name);
        lbl.setFont(LABEL_FONT);

        p.add(lbl);
        p.add(value);

        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    private void initListeners() {

        btnBaja.addActionListener(e -> {
            if (confirm("¿Dar de baja esta habitación?")) {
                lblEstado.setText("BAJA");
                btnBaja.setEnabled(false);
                btnReactivar.setEnabled(true);
            }
        });

        btnReactivar.addActionListener(e -> {
            if (confirm("¿Reactivar esta habitación?")) {
                lblEstado.setText("ACTIVA");
                btnBaja.setEnabled(true);
                btnReactivar.setEnabled(false);
            }
        });
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void cargarMockDatos() {

        lblNombre.setText("Suite 12");
        lblTipo.setText("MATRIMONIAL");
        lblCapacidad.setText("2");
        lblDescripcion.setText("Suite amplia con vista al mar.");
        lblPrecio.setText("$25.000");
        lblEstado.setText("ACTIVA");

        actualizarBotones("ACTIVA");
    }

    private void actualizarBotones(String estado) {
        btnBaja.setEnabled(estado.equals("ACTIVA"));
        btnReactivar.setEnabled(estado.equals("BAJA"));
    }
}