package src.Interface;

import src.classes.Habitacion;
import src.classes.Factura;
import src.classes.Huespede;
import src.classes.Sistema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class NuevoPagoForm extends JFrame {

    // UI fields
    private JTextField FacturaIdField;
    private JSpinner  cantPagosSpinner;

    // Style
    private final Color fondo  = new Color(245, 247, 250);
    private final Color acento = new Color(0, 120, 215);

    // Data
    private final Sistema sistema;

    public NuevoPagoForm(Sistema sistema) {
        this.sistema = sistema;
        setupFrame();
        add(buildMainPanel());
    }

    /* =========================
     *     FRAME / PANELS
     * ========================= */
    private void setupFrame() {
        setTitle("Nuevo pago");
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
        JLabel titulo = new JLabel("📝 Crear nuevo pago", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(acento);
        return titulo;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(fondo);
        GridBagConstraints c = baseGbc();

        // Fields init
        initCoreFields();

        // Row 0: Habitación
        addL(form, "Factura (ID):", c, 0, 0);
        addF(form, FacturaIdField, c, 1, 0);
        addF(form, buildSelectFacturaButton(), c, 2, 0, 0);
        
        addL(form, "Cuanto pago:", c, 0, 7);
        addFSpan2(form, cantPagosSpinner, c, 1, 7);

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
        crearBtn.addActionListener(e -> onPagarFactura());

        panel.add(crearBtn);
        return panel;
    }

    /* =========================
     *        INIT / UI
     * ========================= */
    private void initCoreFields() {
        FacturaIdField = new JTextField();
        FacturaIdField.setEditable(false);

        cantPagosSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 36, 1));
        cantPagosSpinner.setEnabled(false);
    }

    private JButton buildSelectFacturaButton() {
        JButton seleccionarBtn = new JButton("Seleccionar Factura…");
        seleccionarBtn.addActionListener(e -> openSeleccionarFacturaDialog());
        return seleccionarBtn;
    }

    /* =========================
     *        ACTIONS
     * ========================= */
    private void openSeleccionarFacturaDialog() {
        SeleccionarFacturaDialog dlg = new SeleccionarFacturaDialog(this, sistema);
        dlg.setVisible(true);
        Integer id = dlg.getSelectedFacturaId();
        if (id != null) FacturaIdField.setText(String.valueOf(id));
    }

    private void onPagarFactura() {
        if (!validateRequired()) return;

        int facturaId = Integer.parseInt(FacturaIdField.getText().trim());
        int cantPagos   = (Integer) cantPagosSpinner.getValue();

        try {
            sistema.pagarFactura(facturaId, cantPagos);

            showInfo("✅ Reserva creada con éxito:\n\n" +
                    "Factura (ID): " + facturaId +
                    "\nCant. pagos: " + cantPagos);

            // éxito → закрываем форму
            dispose();

        } catch (Exception ex) {
            showError("Ocurrió un error al crear la reserva:\n" + ex.getMessage());
        }
    }

    /* =========================
     *       VALIDATION
     * ========================= */
    private boolean validateRequired() {
        String facturaIdStr = FacturaIdField.getText().trim();

        if (facturaIdStr.isEmpty()) {
            showWarn("Por favor, completá los campos obligatorios (Factura).");
            return false;
        }
        return true;
    }

    private boolean validateDateRange(LocalDate desde, LocalDate hasta) {
        if (!hasta.isAfter(desde)) {
            showWarn("La fecha 'Hasta' debe ser posterior a 'Desde'.");
            return false;
        }
        return true;
    }

    /* =========================
     *       HELPERS
     * ========================= */
    private static GridBagConstraints baseGbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        return c;
    }

    private void addL(JPanel p, String text, GridBagConstraints c, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridx = x; c.gridy = y; c.gridwidth = 1; c.weightx = 0;
        p.add(lbl, c);
    }

    private void addF(JPanel p, JComponent comp, GridBagConstraints c, int x, int y) {
        addF(p, comp, c, x, y, 1);
    }

    private void addF(JPanel p, JComponent comp, GridBagConstraints c, int x, int y, double weightx) {
        c.gridx = x; c.gridy = y; c.gridwidth = 1; c.weightx = weightx;
        p.add(comp, c);
    }

    private void addFSpan2(JPanel p, JComponent comp, GridBagConstraints c, int x, int y) {
        c.gridx = x; c.gridy = y; c.gridwidth = 2; c.weightx = 1;
        p.add(comp, c);
        c.gridwidth = 1;
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

    private static Factura.metodoDePago metodoDePagoStrToEnum(String metodo) {
        if ("TRANSFERENCIA".equals(metodo)) return Factura.metodoDePago.TRANSFERENCIA;
        if ("TARJETA DE CREDITO".equals(metodo)) return Factura.metodoDePago.TARJETA;
        return Factura.metodoDePago.TRANSFERENCIA;
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Reserva registrada", JOptionPane.INFORMATION_MESSAGE);
    }

    /* =========================
     *         MAIN
     * ========================= */
    public static void main(String[] args) {
        Sistema sistema = new Sistema("hotel.db", "hotel.db", "hotel.db", "hotel.db");
        SwingUtilities.invokeLater(() -> new NuevaReservaForm(sistema).setVisible(true));
    }
}
