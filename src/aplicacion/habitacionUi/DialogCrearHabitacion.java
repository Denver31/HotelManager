package aplicacion.habitacionUi;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;

public class DialogCrearHabitacion extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JTextField txtNombre;
    private JComboBox<String> cboTipo;
    private JSpinner spCapacidad;
    private JTextField txtPrecio;
    private JTextArea txtDescripcion;

    private JButton btnCrear;

    public DialogCrearHabitacion(Window owner) {
        super(owner, "Crear Habitación", ModalityType.APPLICATION_MODAL);

        setSize(600, 600);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(560, 560));

        initComponents();
        setContentPane(buildMainPanel());
    }

    private void initComponents() {

        txtNombre = new JTextField(20);

        cboTipo = new JComboBox<>(new String[]{
                "MATRIMONIAL", "INDIVIDUAL", "COMPARTIDA"
        });

        spCapacidad = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        spCapacidad.setPreferredSize(new Dimension(80, 28));

        txtPrecio = new JTextField(10);

        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);

        btnCrear = new JButton("CREAR HABITACIÓN");
        btnCrear.setBackground(ACCENT);
        btnCrear.setForeground(Color.WHITE);

        // Listener del tipo para ajustar capacidad
        cboTipo.addActionListener(e -> actualizarCapacidad());
        actualizarCapacidad(); // estado inicial
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel(new BorderLayout());
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Crear Habitación");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        titulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        global.add(titulo, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        addRow(card, gbc, y++, "Nombre:", wrap(txtNombre));
        addRow(card, gbc, y++, "Tipo:", wrap(cboTipo));
        addRow(card, gbc, y++, "Capacidad:", wrap(spCapacidad));
        addRow(card, gbc, y++, "Precio por noche:", wrap(txtPrecio));

        // Descripción
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        card.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JScrollPane scroll = new JScrollPane(txtDescripcion);
        scroll.setPreferredSize(new Dimension(260, 90));
        card.add(scroll, gbc);

        y++;

        // Botón CREAR (sin botón cancelar)
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        btnCrear.setPreferredSize(new Dimension(250, 35));
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
        gbc.fill = GridBagConstraints.NONE;
        panel.add(comp, gbc);
    }

    private JPanel wrap(JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.add(comp);
        return p;
    }

    // =======================================================
    //   CAPACIDAD SEGÚN TIPO
    // =======================================================
    private void actualizarCapacidad() {

        String tipo = (String) cboTipo.getSelectedItem();

        switch (tipo) {
            case "MATRIMONIAL" -> {
                spCapacidad.setModel(new SpinnerNumberModel(2, 2, 2, 1));
                spCapacidad.setEnabled(false);
            }
            case "INDIVIDUAL" -> {
                spCapacidad.setModel(new SpinnerNumberModel(1, 1, 1, 1));
                spCapacidad.setEnabled(false);
            }
            case "COMPARTIDA" -> {
                spCapacidad.setModel(new SpinnerNumberModel(1, 1, 4, 1));
                spCapacidad.setEnabled(true);
            }
        }
    }
}