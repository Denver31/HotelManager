package aplicacion.reservaUi;

import aplicacion.huespedUi.DialogCrearHuesped;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.util.Date;

public class DialogCrearReserva extends JDialog {

    // 🎨 Estilos
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Datos huésped
    private JTextField txtIdHuesped;
    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCorreo;

    private JButton btnBuscarHuesped;
    private JButton btnCrearHuesped;

    // Fechas
    private JSpinner spDesde;
    private JSpinner spHasta;

    // Habitación
    private JTextField txtHabitacionId;
    private JButton btnSeleccionarHabitacion;

    // Pago
    private JComboBox<String> cboMetodoPago;
    private JComboBox<Integer> cboCuotas;

    // Confirmar
    private JButton btnConfirmar;

    public DialogCrearReserva(Window owner) {
        super(owner, "Crear Reserva", ModalityType.APPLICATION_MODAL);

        setSize(650, 720);                    // Ventana más espaciosa
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(650, 650));

        initComponents();
        setContentPane(buildMainPanel());
        initListeners();
    }

    private void initComponents() {

        txtIdHuesped = new JTextField(25);
        txtDni = new JTextField(25);
        txtNombre = new JTextField(25);
        txtApellido = new JTextField(25);
        txtCorreo = new JTextField(25);

        btnBuscarHuesped = new JButton("Buscar Huésped");
        btnCrearHuesped = new JButton("Crear Huésped");

        // === FECHAS ===
        spDesde = new JSpinner(new SpinnerDateModel());
        spDesde.setEditor(new JSpinner.DateEditor(spDesde, "dd/MM/yyyy"));
        spDesde.setPreferredSize(new Dimension(90, 28));

        spHasta = new JSpinner(new SpinnerDateModel());
        spHasta.setEditor(new JSpinner.DateEditor(spHasta, "dd/MM/yyyy"));
        spHasta.setPreferredSize(new Dimension(90, 28));

        // === HABITACIÓN ===
        txtHabitacionId = new JTextField(6);

        btnSeleccionarHabitacion = new JButton("Seleccionar Habitación");

        // === PAGO ===
        cboMetodoPago = new JComboBox<>(new String[]{"TRANSFERENCIA", "TARJETA"});
        cboMetodoPago.setPreferredSize(new Dimension(140, 28));

        cboCuotas = new JComboBox<>(new Integer[]{1, 2, 3});
        cboCuotas.setPreferredSize(new Dimension(80, 28));

        cboMetodoPago.addActionListener(e -> actualizarCuotas());

        btnConfirmar = new JButton("CONFIRMAR RESERVA");
        btnConfirmar.setBackground(ACCENT);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);

        actualizarCuotas();
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel(new BorderLayout());
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Crear Reserva");
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

        // HUESPED
        addRow(card, gbc, y++, "Huésped ID:", wrap(txtIdHuesped));
        addRow(card, gbc, y++, "DNI:", wrap(txtDni));
        addRow(card, gbc, y++, "Nombre:", wrap(txtNombre));
        addRow(card, gbc, y++, "Apellido:", wrap(txtApellido));
        addRow(card, gbc, y++, "Correo:", wrap(txtCorreo));

        JPanel botonesH = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botonesH.setOpaque(false);
        botonesH.add(btnBuscarHuesped);
        botonesH.add(btnCrearHuesped);

        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.NONE;
        card.add(botonesH, gbc);

        // FECHAS
        addRow(card, gbc, y++, "Desde:", wrap(spDesde));
        addRow(card, gbc, y++, "Hasta:", wrap(spHasta));

        // HABITACIÓN
        JPanel habPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        habPanel.setOpaque(false);

        txtHabitacionId.setPreferredSize(new Dimension(60, 28));
        btnSeleccionarHabitacion.setPreferredSize(new Dimension(170, 28));

        habPanel.add(txtHabitacionId);
        habPanel.add(btnSeleccionarHabitacion);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        card.add(new JLabel("Habitación:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        card.add(habPanel, gbc);
        y++;

        // PAGO
        addRow(card, gbc, y++, "Método de Pago:", wrap(cboMetodoPago));
        addRow(card, gbc, y++, "Cuotas:", wrap(cboCuotas));

        // CONFIRMAR
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        btnConfirmar.setPreferredSize(new Dimension(250, 35));
        card.add(btnConfirmar, gbc);

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

    /** Envuelve un campo para preservar su preferredSize */
    private JPanel wrap(JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.add(comp);
        return p;
    }

    // =======================================================
    //   CUOTAS SEGÚN MÉTODO DE PAGO
    // =======================================================
    private void actualizarCuotas() {
        String metodo = (String) cboMetodoPago.getSelectedItem();

        cboCuotas.removeAllItems();

        if ("TRANSFERENCIA".equals(metodo)) {
            cboCuotas.addItem(1);
            cboCuotas.setEnabled(false);
        } else {
            cboCuotas.addItem(1);
            cboCuotas.addItem(2);
            cboCuotas.addItem(3);
            cboCuotas.setEnabled(true);
        }
    }

    // =======================================================
    //   LISTENERS / INTEGRACIÓN DIÁLOGOS
    // =======================================================
    private void initListeners() {

        // Buscar huésped
        btnBuscarHuesped.addActionListener(e -> abrirDialogBuscarHuesped());

        // Crear huésped (ya creado DialogCrearHuesped)
        btnCrearHuesped.addActionListener(e -> {
            DialogCrearHuesped dialog = new DialogCrearHuesped(
                    SwingUtilities.getWindowAncestor(this)
            );
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            // TODO: si DialogCrearHuesped devuelve el huésped creado,
            // completarlo aquí.
        });

        // Seleccionar habitación
        btnSeleccionarHabitacion.addActionListener(e -> abrirDialogSeleccionarHabitacion());

        // Confirmar reserva
        btnConfirmar.addActionListener(e -> {
            // TODO: construir DTO / comando y enviarlo a ReservaController / Sistema
            System.out.println("[UI] Confirmar reserva (TODO implementar lógica real)");
            dispose();
        });
    }

    private void abrirDialogBuscarHuesped() {

        String idStr = txtIdHuesped.getText().trim();
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtCorreo.getText().trim();

        DialogBuscarHuesped dialog = new DialogBuscarHuesped(
                SwingUtilities.getWindowAncestor(this),
                idStr,
                dni,
                nombre,
                apellido,
                email
        );

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        DialogBuscarHuesped.HuespedSeleccionado sel = dialog.getSeleccionado();
        if (sel != null) {
            txtIdHuesped.setText(String.valueOf(sel.id));
            txtDni.setText(sel.dni);
            txtNombre.setText(sel.nombre);
            txtApellido.setText(sel.apellido);
            txtCorreo.setText(sel.email);
        }
    }

    private void abrirDialogSeleccionarHabitacion() {

        Date desde = (Date) spDesde.getValue();
        Date hasta = (Date) spHasta.getValue();

        DialogSeleccionarHabitacion dialog = new DialogSeleccionarHabitacion(
                SwingUtilities.getWindowAncestor(this),
                desde,
                hasta
        );

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        DialogSeleccionarHabitacion.HabitacionSeleccionada sel = dialog.getSeleccionada();
        if (sel != null) {
            txtHabitacionId.setText(String.valueOf(sel.id));
        }
    }
}