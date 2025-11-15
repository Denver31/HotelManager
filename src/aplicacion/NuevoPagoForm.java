package aplicacion;

import aplicacion.Sistema;
import dominio.Factura;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NuevoPagoForm extends JFrame {

    private JTextField facturaIdField;
    private final Color fondo  = new Color(245, 247, 250);
    private final Color acento = new Color(0, 120, 215);

    private final Sistema sistema;

    public NuevoPagoForm(Sistema sistema) {
        this.sistema = sistema;
        setupFrame();
        add(buildMainPanel());
    }

    private void setupFrame() {
        setTitle("Registrar pago");
        setSize(480, 220);
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
        JLabel titulo = new JLabel("💳 Registrar pago de factura", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(acento);
        return titulo;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(fondo);
        GridBagConstraints c = baseGbc();

        facturaIdField = new JTextField();
        facturaIdField.setEditable(false);

        addL(form, "Factura (ID):", c, 0, 0);
        addF(form, facturaIdField, c, 1, 0);
        addF(form, buildSelectFacturaButton(), c, 2, 0, 0);

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
        crearBtn.addActionListener(e -> onRegistrarPago());

        panel.add(crearBtn);
        return panel;
    }

    private JButton buildSelectFacturaButton() {
        JButton seleccionarBtn = new JButton("Seleccionar factura…");
        seleccionarBtn.addActionListener(e -> openSeleccionarFacturaDialog());
        return seleccionarBtn;
    }

    private void openSeleccionarFacturaDialog() {
        SeleccionarFacturaDialog dlg = new SeleccionarFacturaDialog(this, sistema);
        dlg.setVisible(true);
        Integer id = dlg.getSelectedFacturaId();
        if (id != null) facturaIdField.setText(String.valueOf(id));
    }

    private void onRegistrarPago() {
        String idStr = facturaIdField.getText().trim();
        if (idStr.isEmpty()) {
            showWarn("Por favor, seleccioná una factura antes de continuar.");
            return;
        }

        try {
            int idFactura = Integer.parseInt(idStr);
            sistema.registrarPago(idFactura);

            showInfo("✅ Pago registrado correctamente.\nFactura ID: " + idFactura);
            dispose();

        } catch (Exception ex) {
            showError("Error al registrar el pago:\n" + ex.getMessage());
        }
    }

    // =========================
    // Helpers UI
    // =========================
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
        Sistema sistema = new Sistema();
        SwingUtilities.invokeLater(() -> new NuevoPagoForm(sistema).setVisible(true));
    }
}
