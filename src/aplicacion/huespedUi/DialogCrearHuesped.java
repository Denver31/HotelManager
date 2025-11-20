package aplicacion.huespedUi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class DialogCrearHuesped extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;

    private JButton btnCrear;

    public DialogCrearHuesped(Window owner) {
        super(owner, "Crear Huésped", ModalityType.APPLICATION_MODAL);

        setSize(500, 400);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(450, 350));

        initComponents();
        setContentPane(buildMainPanel());
    }

    private void initComponents() {

        txtDni = new JTextField(20);
        txtNombre = new JTextField(20);
        txtApellido = new JTextField(20);
        txtEmail = new JTextField(20);

        btnCrear = new JButton("CREAR HUÉSPED");
        btnCrear.setBackground(ACCENT);
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFocusPainted(false);
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel(new BorderLayout());
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Crear Huésped");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        titulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        global.add(titulo, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        addRow(card, gbc, y++, "DNI:", txtDni);
        addRow(card, gbc, y++, "Nombre:", txtNombre);
        addRow(card, gbc, y++, "Apellido:", txtApellido);
        addRow(card, gbc, y++, "Email:", txtEmail);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        btnCrear.setPreferredSize(new Dimension(200, 32));
        card.add(btnCrear, gbc);

        global.add(card, BorderLayout.CENTER);

        return global;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        int y, String label, JComponent comp) {

        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp, gbc);
    }
}
