package aplicacion.reservaUi;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.util.Date;

import dto.HuespedListadoDTO;

import aplicacion.reservaUi.presenter.CrearReservaPresenter;

public class DialogCrearReserva extends JDialog {

    // Estilos
    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // MVP
    private CrearReservaPresenter presenter;
    public void setPresenter(CrearReservaPresenter presenter) { this.presenter = presenter; }

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

        setSize(650, 720);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(650, 650));

        initComponents();
        setContentPane(buildMainPanel());
        initListeners();
    }

    private void initComponents() {

        txtIdHuesped = new JTextField(25);
        txtIdHuesped.setEditable(true);

        txtDni = new JTextField(25);
        txtNombre = new JTextField(25);
        txtApellido = new JTextField(25);
        txtCorreo = new JTextField(25);

        // = HUESPED =
        btnBuscarHuesped = new JButton("Buscar Huésped");
        btnCrearHuesped = new JButton("Crear Huésped");

        // = FECHAS =
        spDesde = new JSpinner(new SpinnerDateModel());
        spDesde.setEditor(new JSpinner.DateEditor(spDesde, "dd/MM/yyyy"));

        spHasta = new JSpinner(new SpinnerDateModel());
        spHasta.setEditor(new JSpinner.DateEditor(spHasta, "dd/MM/yyyy"));

        // = HABITACION =
        txtHabitacionId = new JTextField(6);
        txtHabitacionId.setEditable(false);

        btnSeleccionarHabitacion = new JButton("Seleccionar Habitación");

        // = PAGO =
        cboMetodoPago = new JComboBox<>(new String[]{"TRANSFERENCIA", "TARJETA"});
        cboCuotas = new JComboBox<>();

        actualizarCuotas();

        cboMetodoPago.addActionListener(e -> actualizarCuotas());

        // = CONFIRMAR =
        btnConfirmar = new JButton("CONFIRMAR RESERVA");
        btnConfirmar.setBackground(ACCENT);
        btnConfirmar.setForeground(Color.WHITE);
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

        // --------------- Huesped ---------------
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

        // --------------- Fechas ---------------
        addRow(card, gbc, y++, "Desde:", wrap(spDesde));
        addRow(card, gbc, y++, "Hasta:", wrap(spHasta));

        // --------------- Habitación ---------------
        JPanel habPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        habPanel.setOpaque(false);

        txtHabitacionId.setPreferredSize(new Dimension(60, 28));
        btnSeleccionarHabitacion.setPreferredSize(new Dimension(170, 28));

        habPanel.add(txtHabitacionId);
        habPanel.add(btnSeleccionarHabitacion);

        gbc.gridx = 0;
        gbc.gridy = y;
        card.add(new JLabel("Habitación:"), gbc);

        gbc.gridx = 1;
        card.add(habPanel, gbc);
        y++;

        // --------------- Pago ---------------
        addRow(card, gbc, y++, "Método de Pago:", wrap(cboMetodoPago));
        addRow(card, gbc, y++, "Cuotas:", wrap(cboCuotas));

        // --------------- Confirmar ---------------
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
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(comp, gbc);
    }

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
    //   LISTENERS MVP
    // =======================================================
    private void initListeners() {

        btnBuscarHuesped.addActionListener(e -> {
            if (presenter != null) presenter.onBuscarHuesped();
        });

        btnCrearHuesped.addActionListener(e -> {
            if (presenter != null) presenter.onCrearHuesped();
        });

        btnSeleccionarHabitacion.addActionListener(e -> {
            if (presenter != null) presenter.onSeleccionarHabitacion();
        });

        btnConfirmar.addActionListener(e -> {
            if (presenter != null) presenter.onConfirmarReserva();
        });
    }

    // =======================================================
    //  MÉTODOS PARA USAR DESDE EL PRESENTER
    // =======================================================
    public void mostrarMensaje(String m) {
        JOptionPane.showMessageDialog(this, m, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void cerrar() {
        dispose();
    }

    // ----------- Getters usados por el Presenter -----------

    public String getIdHuesped() { return txtIdHuesped.getText(); }
    public String getDni() { return txtDni.getText(); }
    public String getNombre() { return txtNombre.getText(); }
    public String getApellido() { return txtApellido.getText(); }
    public String getCorreo() { return txtCorreo.getText(); }

    public Date getFechaDesde() { return (Date) spDesde.getValue(); }
    public Date getFechaHasta() { return (Date) spHasta.getValue(); }

    public String getHabitacionId() { return txtHabitacionId.getText(); }

    public String getMetodoPago() { return (String) cboMetodoPago.getSelectedItem(); }
    public int getCuotas() { return (Integer) cboCuotas.getSelectedItem(); }

    // -------- setters para completar datos desde presenter --------
    public void setDatosHuesped(int id, String dni, String nombre, String apellido, String email) {
        txtIdHuesped.setText(String.valueOf(id));
        txtDni.setText(dni);
        txtNombre.setText(nombre);
        txtApellido.setText(apellido);
        txtCorreo.setText(email);
    }

    public void completarHuesped(HuespedListadoDTO dto) {
        txtIdHuesped.setText(String.valueOf(dto.id()));
        txtDni.setText(dto.dni());

        txtNombre.setText(dto.nombre());
        txtApellido.setText(dto.apellido());

        txtCorreo.setText(dto.email());
    }

    public Window getOwnerWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }



    public void setHabitacion(int idHabitacion) {
        txtHabitacionId.setText(String.valueOf(idHabitacion));
    }
}