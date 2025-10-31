package src.Interface;

import src.classes.Factura;
import src.classes.Sistema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class NuevoPagoForm extends JFrame {

    // UI fields
    private JTextField facturaIdField;
    private JSpinner   cantPagosSpinner;

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
        setSize(560, 280);
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
        JLabel titulo = new JLabel("📝 Registrar pago de factura", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(acento);
        return titulo;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(fondo);
        GridBagConstraints c = baseGbc();

        initCoreFields();

        // fila 0: factura
        addL(form, "Factura (ID):", c, 0, 0);
        addF(form, facturaIdField, c, 1, 0);
        addF(form, buildSelectFacturaButton(), c, 2, 0, 0);

        // fila 1: cantidad de pagos
        addL(form, "Cantidad pagos:", c, 0, 1);
        addFSpan2(form, cantPagosSpinner, c, 1, 1);

        return form;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(fondo);

        JButton crearBtn = new JButton("Registrar pago");
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
        facturaIdField = new JTextField();
        facturaIdField.setEditable(false);

        // по умолчанию выключен — включим после выбора фактуры
        cantPagosSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        cantPagosSpinner.setEnabled(false);
    }

    private JButton buildSelectFacturaButton() {
        JButton seleccionarBtn = new JButton("Seleccionar factura…");
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
        if (id != null) {
            facturaIdField.setText(String.valueOf(id));

            // ★ получаем фактуру и настраиваем лимиты
            Factura f = sistema.getFacturaById(id); // предполагаем, что такой метод есть
            if (f != null) {
                configureCantPagosSpinner(f);
            } else {
                // на всякий — если не нашлось
                cantPagosSpinner.setEnabled(false);
            }
        }
    }

    private void configureCantPagosSpinner(Factura f) {
        double total   = f.getTotal();
        double pagado  = f.getPagado();
        int cantTotal  = f.getCantPagos();

        if (cantTotal <= 0 || total <= 0) {
            cantPagosSpinner.setModel(new SpinnerNumberModel(1, 1, 1, 1));
            cantPagosSpinner.setEnabled(false);
            return;
        }

        double cuota = total / cantTotal;
        double restante = total - pagado;

        if (restante <= 0.0001) {
            cantPagosSpinner.setModel(new SpinnerNumberModel(1, 1, 1, 1));
            cantPagosSpinner.setEnabled(false);
            return;
        }

        int pagosRestantes = (int) Math.round(restante / cuota);
        if (pagosRestantes < 1) pagosRestantes = 1;

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, pagosRestantes, 1);
        cantPagosSpinner.setModel(model);
        cantPagosSpinner.setEnabled(true);
    }

    private void onPagarFactura() {
        if (!validateRequired()) return;

        int facturaId = Integer.parseInt(facturaIdField.getText().trim());
        int cantPagos = (Integer) cantPagosSpinner.getValue();

        try {
            sistema.pagarFactura(facturaId, cantPagos);

            showInfo("✅ Pago registrado:\n" +
                    "Factura (ID): " + facturaId +
                    "\nCantidad de pagos: " + cantPagos);

            dispose();

        } catch (Exception ex) {
            showError("Ocurrió un error al registrar el pago:\n" + ex.getMessage());
        }
    }

    /* =========================
     *       VALIDATION
     * ========================= */
    private boolean validateRequired() {
        String facturaIdStr = facturaIdField.getText().trim();

        if (facturaIdStr.isEmpty()) {
            showWarn("Por favor, seleccioná una factura.");
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

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Pago registrado", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        Sistema sistema = new Sistema("hotel.db", "hotel.db", "hotel.db", "hotel.db");
        SwingUtilities.invokeLater(() -> new NuevoPagoForm(sistema).setVisible(true));
    }
}
