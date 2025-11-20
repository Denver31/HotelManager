package aplicacion.huespedUi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogDetalleHuesped extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JLabel lblNombre;
    private JLabel lblApellido;
    private JLabel lblDni;
    private JLabel lblEmail;
    private JLabel lblEstado;

    private JButton btnBaja;
    private JButton btnReactivar;
    private JButton btnEditar;

    private final int idHuesped;

    public DialogDetalleHuesped(Window owner, int idHuesped) {
        super(owner, "Detalle de Huésped", ModalityType.APPLICATION_MODAL);
        this.idHuesped = idHuesped;

        setSize(550, 550);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(500, 500));
        setBackground(BG);

        initComponents();
        setContentPane(buildMainPanel());
        initListeners();
        cargarMockDatos();
    }

    private void initComponents() {

        lblNombre = createValueLabel();
        lblApellido = createValueLabel();
        lblDni = createValueLabel();
        lblEmail = createValueLabel();
        lblEstado = createValueLabel();

        btnBaja = createButton("DAR DE BAJA", new Color(180, 40, 40));
        btnReactivar = createButton("REACTIVAR", new Color(0, 160, 80));
        btnEditar = createButton("EDITAR", new Color(70, 130, 180));
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel();
        global.setLayout(new BoxLayout(global, BoxLayout.Y_AXIS));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Detalle de Huésped #" + idHuesped);
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

        content.add(sectionTitle("--- Huésped ---"));
        content.add(line("Nombre: ", lblNombre));
        content.add(line("Apellido: ", lblApellido));
        content.add(line("DNI: ", lblDni));
        content.add(line("Email: ", lblEmail));
        content.add(line("Estado: ", lblEstado));

        global.add(content);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        botones.setOpaque(false);

        botones.add(btnEditar);
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

        btnBaja.addActionListener(e -> {
            // TODO: validar con reglas de negocio (reservas PENDIENTE/CONFIRMADA/ACTIVA)
            if (confirm("¿Dar de baja este huésped?")) {
                lblEstado.setText("BAJA");
                actualizarBotones("BAJA");
            }
        });

        btnReactivar.addActionListener(e -> {
            if (confirm("¿Reactivar este huésped?")) {
                lblEstado.setText("ACTIVO");
                actualizarBotones("ACTIVO");
            }
        });

        btnEditar.addActionListener(e -> {
            DialogEditHuesped dialog = new DialogEditHuesped(
                    SwingUtilities.getWindowAncestor(this),
                    idHuesped,
                    lblDni.getText(),
                    lblNombre.getText(),
                    lblApellido.getText(),
                    lblEmail.getText()
            );
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            // TODO: luego de editar, recargar datos desde el controller
        });
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void cargarMockDatos() {

        // MOCK hasta tener controller
        lblNombre.setText("Juan");
        lblApellido.setText("Pérez");
        lblDni.setText("30123456");
        lblEmail.setText("juan@example.com");
        lblEstado.setText("ACTIVO");

        actualizarBotones("ACTIVO");
    }

    private void actualizarBotones(String estado) {
        boolean activo = "ACTIVO".equalsIgnoreCase(estado);
        boolean baja = "BAJA".equalsIgnoreCase(estado);

        btnEditar.setEnabled(activo);
        btnBaja.setEnabled(activo);
        btnReactivar.setEnabled(baja);
    }
}