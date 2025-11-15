package aplicacion;

import dominio.Habitacion;
import dominio.Habitacion.TipoHabitacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NuevaHabitacionForm extends JFrame {

    private final Color fondo = new Color(245, 247, 250);
    private final Color acento = new Color(0, 120, 215);

    private JTextField nombreField;
    private JTextField descripcionField;
    private JTextField precioField;
    private JComboBox<String> tipoCombo;
    private JSpinner capacidadSpinner;

    private final Sistema sistema;

    public NuevaHabitacionForm(Sistema sistema) {
        this.sistema = sistema;
        setupFrame();
        add(buildMainPanel());
    }

    private void setupFrame() {
        setTitle("Nueva Habitación");
        setSize(500, 420);
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
        JLabel titulo = new JLabel("🏨 Agregar nueva habitación", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(acento);
        return titulo;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(fondo);
        GridBagConstraints c = baseGbc();

        initCoreFields();

        addL(form, "Nombre:", c, 0, 0);
        addFSpan2(form, nombreField, c, 1, 0);

        addL(form, "Descripción:", c, 0, 1);
        addFSpan2(form, descripcionField, c, 1, 1);

        addL(form, "Precio ($):", c, 0, 2);
        addFSpan2(form, precioField, c, 1, 2);

        addL(form, "Tipo:", c, 0, 3);
        addFSpan2(form, tipoCombo, c, 1, 3);

        addL(form, "Capacidad:", c, 0, 4);
        addFSpan2(form, capacidadSpinner, c, 1, 4);

        tipoCombo.addActionListener(e -> ajustarCapacidadPorTipo());

        return form;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(fondo);

        JButton crearBtn = new JButton("Agregar habitación");
        crearBtn.setBackground(acento);
        crearBtn.setForeground(Color.WHITE);
        crearBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        crearBtn.setFocusPainted(false);
        crearBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        crearBtn.addActionListener(e -> onCreateHabitacion());

        panel.add(crearBtn);
        return panel;
    }

    private void initCoreFields() {
        nombreField = new JTextField();
        descripcionField = new JTextField();
        precioField = new JTextField();

        tipoCombo = new JComboBox<>(new String[]{"INDIVIDUAL", "COMPARTIDA", "MATRIMONIAL"});
        tipoCombo.setSelectedItem("INDIVIDUAL");

        capacidadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 6, 1));
    }

    private void ajustarCapacidadPorTipo() {
        String tipo = (String) tipoCombo.getSelectedItem();
        SpinnerNumberModel model;
        switch (tipo) {
            case "COMPARTIDA":
                model = new SpinnerNumberModel(2, 2, 4, 1);
                break;
            case "MATRIMONIAL":
                model = new SpinnerNumberModel(2, 2, 2, 1);
                break;
            default: // INDIVIDUAL
                model = new SpinnerNumberModel(1, 1, 1, 1);
                break;
        }
        capacidadSpinner.setModel(model);
    }

    private void onCreateHabitacion() {
        String nombre = nombreField.getText().trim();
        String descripcion = descripcionField.getText().trim();
        String precioStr = precioField.getText().trim();
        String tipoStr = (String) tipoCombo.getSelectedItem();
        int capacidad = (Integer) capacidadSpinner.getValue();

        if (!validarCampos(nombre, descripcion, precioStr)) return;

        try {
            double precio = Double.parseDouble(precioStr);
            TipoHabitacion tipo = TipoHabitacion.valueOf(tipoStr);

            Habitacion nueva = new Habitacion(0, nombre, descripcion, precio, tipo, capacidad);
            sistema.crearHabitacion(nombre, descripcion, precio, tipo, capacidad);

            showInfo("✅ Habitación agregada correctamente.");
            dispose();

        } catch (NumberFormatException ex) {
            showWarn("El precio debe ser un número válido.");
        } catch (Exception ex) {
            showError("Ocurrió un error al crear la habitación:\n" + ex.getMessage());
        }
    }

    private boolean validarCampos(String nombre, String descripcion, String precioStr) {
        if (nombre.isEmpty() || nombre.length() < 3) {
            showWarn("El nombre debe tener al menos 3 caracteres.");
            return false;
        }
        if (descripcion.length() > 255) {
            showWarn("La descripción no puede superar los 255 caracteres.");
            return false;
        }
        if (!precioStr.matches("\\d+(\\.\\d{1,2})?")) {
            showWarn("El precio debe ser un número positivo (ej: 150 o 99.99).");
            return false;
        }
        return true;
    }

    // ========== MÉTODOS AUXILIARES DE INTERFAZ ==========

    private static GridBagConstraints baseGbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private void addL(JPanel p, String text, GridBagConstraints c, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridx = x; c.gridy = y; c.gridwidth = 1; c.weightx = 0;
        p.add(lbl, c);
    }

    private void addFSpan2(JPanel p, JComponent comp, GridBagConstraints c, int x, int y) {
        c.gridx = x; c.gridy = y; c.gridwidth = 2; c.weightx = 1.0;
        p.add(comp, c);
        c.gridwidth = 1;
    }

    // ========== FEEDBACK ==========

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Habitación registrada", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NuevaHabitacionForm(new Sistema()).setVisible(true));
    }
}

