package aplicacion;

import aplicacion.Sistema;
import dominio.*;
import dominio.Factura.MetodoPago;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class NuevaReservaForm extends JFrame {

    private JTextField habitacionIdField;
    private JTextField dniField;
    private JTextField nombreField;
    private JTextField apellidoField;
    private JTextField emailField;
    private JSpinner desdeSpinner;
    private JSpinner hastaSpinner;
    private JComboBox<String> metodoCombo;
    private JSpinner cantPagosSpinner;

    private final Color fondo = new Color(245, 247, 250);
    private final Color acento = new Color(0, 120, 215);

    private final Sistema sistema;

    public NuevaReservaForm(Sistema sistema) {
        this.sistema = sistema;
        setupFrame();
        add(buildMainPanel());
    }

    private void setupFrame() {
        setTitle("Nueva Reserva");
        setSize(560, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(fondo);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        main.add(buildTitle(), BorderLayout.NORTH);
        main.add(buildFormPanel(), BorderLayout.CENTER);
        main.add(buildButtonPanel(), BorderLayout.SOUTH);

        return main;
    }

    private JComponent buildTitle() {
        JLabel titulo = new JLabel("📝 Crear nueva reserva", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(acento);
        return titulo;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(fondo);
        GridBagConstraints c = baseGbc();

        initCoreFields();

        addL(form, "Habitación (ID):", c, 0, 0);
        addF(form, habitacionIdField, c, 1, 0);
        addF(form, buildSelectHabitacionButton(), c, 2, 0, 0);

        addL(form, "DNI o Pasaporte:", c, 0, 1);
        addFSpan2(form, dniField, c, 1, 1);

        addL(form, "Nombre:", c, 0, 2);
        addFSpan2(form, nombreField, c, 1, 2);

        addL(form, "Apellido:", c, 0, 3);
        addFSpan2(form, apellidoField, c, 1, 3);

        addL(form, "Correo electrónico:", c, 0, 4);
        addFSpan2(form, emailField, c, 1, 4);

        addL(form, "Desde:", c, 0, 5);
        addFSpan2(form, desdeSpinner, c, 1, 5);

        addL(form, "Hasta:", c, 0, 6);
        addFSpan2(form, hastaSpinner, c, 1, 6);

        addL(form, "Método de pago:", c, 0, 7);
        addFSpan2(form, metodoCombo, c, 1, 7);

        addL(form, "Cantidad de pagos:", c, 0, 8);
        addFSpan2(form, cantPagosSpinner, c, 1, 8);

        setupPaymentControls();
        return form;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(fondo);

        JButton crearBtn = new JButton("Crear reserva");
        crearBtn.setBackground(acento);
        crearBtn.setForeground(Color.WHITE);
        crearBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        crearBtn.setFocusPainted(false);
        crearBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        crearBtn.addActionListener(e -> onCreateReserva());

        panel.add(crearBtn);
        return panel;
    }

    private void initCoreFields() {
        habitacionIdField = new JTextField();
        habitacionIdField.setEditable(false);

        dniField = new JTextField();
        nombreField = new JTextField();
        apellidoField = new JTextField();
        emailField = new JTextField();

        Date hoy = new Date();
        desdeSpinner = createDateSpinner(hoy);
        hastaSpinner = createDateSpinner(hoy);

        metodoCombo = new JComboBox<>(new String[]{"TRANSFERENCIA", "TARJETA DE CREDITO"});
        metodoCombo.setSelectedItem("TRANSFERENCIA");

        cantPagosSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
        cantPagosSpinner.setEnabled(false);
    }

    private JButton buildSelectHabitacionButton() {
        JButton seleccionarBtn = new JButton("Seleccionar habitación…");
        seleccionarBtn.addActionListener(e -> openSeleccionarHabitacionDialog());
        return seleccionarBtn;
    }

    private void setupPaymentControls() {
        metodoCombo.addActionListener(e -> {
            String m = (String) metodoCombo.getSelectedItem();
            boolean tarjeta = "TARJETA DE CREDITO".equals(m);
            cantPagosSpinner.setEnabled(tarjeta);
            cantPagosSpinner.setModel(new SpinnerNumberModel(1, 1, tarjeta ? 3 : 1, 1));
        });
    }

    private void openSeleccionarHabitacionDialog() {
        // Obtener las fechas seleccionadas en los spinners
        LocalDate desde = toLocalDate((Date) desdeSpinner.getValue());
        LocalDate hasta = toLocalDate((Date) hastaSpinner.getValue());

        // Validar que las fechas tengan sentido antes de abrir el diálogo
        if (!hasta.isAfter(desde)) {
            showWarn("La fecha 'Hasta' debe ser posterior a 'Desde' antes de seleccionar una habitación.");
            return;
        }

        // Crear y abrir el diálogo con las fechas
        SeleccionarHabitacionDialog dlg = new SeleccionarHabitacionDialog(this, sistema, desde, hasta);
        dlg.setVisible(true);

        Integer id = dlg.getSelectedHabitacionId();
        if (id != null) habitacionIdField.setText(String.valueOf(id));
    }

    private void onCreateReserva() {
        if (!validateRequired()) return;

        LocalDate desde = toLocalDate((Date) desdeSpinner.getValue());
        LocalDate hasta = toLocalDate((Date) hastaSpinner.getValue());
        LocalDate hoy = LocalDate.now();

        if (!validateDateRange(desde, hasta, hoy)) return;

        try {
            int idHabitacion = Integer.parseInt(habitacionIdField.getText().trim());
            String dni = dniField.getText().trim();
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            MetodoPago metodo = metodoDePagoStrToEnum((String) metodoCombo.getSelectedItem());
            int cuotas = (Integer) cantPagosSpinner.getValue();

            String email = emailField.getText().trim();

            if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                showWarn("Por favor, ingresá un correo electrónico válido.");
                return;
            }

            sistema.crearReserva(dni, nombre, apellido, email, idHabitacion, desde, hasta, metodo, cuotas);


            showInfo("✅ Reserva creada correctamente.");
            dispose();

        } catch (Exception ex) {
            showError("Error al crear la reserva:\n" + ex.getMessage());
        }
    }

    // ================= VALIDACIONES =================

    private boolean validateRequired() {
        if (habitacionIdField.getText().trim().isEmpty()) {
            showWarn("Seleccione una habitación.");
            return false;
        }
        if (!dniField.getText().matches("[0-9A-Za-z]{6,15}")) {
            showWarn("DNI/Pasaporte inválido. Use solo letras y números (6–15 caracteres).");
            return false;
        }
        if (!nombreField.getText().matches("[A-Za-zÁÉÍÓÚáéíóúñÑ ]{2,30}")) {
            showWarn("Nombre inválido (solo letras y espacios).");
            return false;
        }
        if (!apellidoField.getText().matches("[A-Za-zÁÉÍÓÚáéíóúñÑ ]{2,30}")) {
            showWarn("Apellido inválido (solo letras y espacios).");
            return false;
        }
        return true;
    }

    private boolean validateDateRange(LocalDate desde, LocalDate hasta, LocalDate hoy) {
        if (desde.isBefore(hoy)) {
            showWarn("La fecha de inicio no puede ser anterior a hoy.");
            return false;
        }
        if (!hasta.isAfter(desde)) {
            showWarn("La fecha 'Hasta' debe ser posterior a 'Desde'.");
            return false;
        }
        return true;
    }

    // ================= UTILITARIOS =================

    private static GridBagConstraints baseGbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private static JSpinner createDateSpinner(Date base) {
        SpinnerDateModel model = new SpinnerDateModel(base, null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    private static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static MetodoPago metodoDePagoStrToEnum(String metodo) {
        return "TARJETA DE CREDITO".equals(metodo)
                ? MetodoPago.TARJETA
                : MetodoPago.TRANSFERENCIA;
    }

    // ================= FEEDBACK =================

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Reserva registrada", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NuevaReservaForm(new Sistema()).setVisible(true));
    }

    // ================= MÉTODOS AUXILIARES PARA FORMULARIO =================

    private void addL(JPanel p, String text, GridBagConstraints c, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        c.weightx = 0;
        p.add(lbl, c);
    }

    private void addF(JPanel p, JComponent comp, GridBagConstraints c, int x, int y) {
        addF(p, comp, c, x, y, 1.0);
    }

    private void addF(JPanel p, JComponent comp, GridBagConstraints c, int x, int y, double weightx) {
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        c.weightx = weightx;
        p.add(comp, c);
    }

    private void addFSpan2(JPanel p, JComponent comp, GridBagConstraints c, int x, int y) {
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 2;
        c.weightx = 1.0;
        p.add(comp, c);
        c.gridwidth = 1; // reset
    }

}
