package src.Interface;

import src.classes.Sistema;
import src.classes.Habitacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

public class NuevaHabitacionForm extends JFrame {

    // ---- стиль
    private static final Color BG = new Color(245, 247, 250);
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font  TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font  FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // ---- данные
    private final Sistema sistema;

    // ---- UI refs
    private JTextField nombreField;
    private JTextArea  descripcionArea;
    private JTextField precioField;
    private JComboBox<Habitacion.tipoHabitacion> tipoCombo;
    private JLabel capacidadValue;

    public NuevaHabitacionForm(Sistema sistema) {
        this.sistema = sistema;
        setupFrame();
        add(buildMainPanel());
    }

    /* =========================
     *        FRAME / UI
     * ========================= */
    private void setupFrame() {
        setTitle("Nueva Habitación");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(16, 16, 16, 16));

        main.add(buildHeader(), BorderLayout.NORTH);
        main.add(buildFormPanel(), BorderLayout.CENTER);
        main.add(buildButtonsPanel(), BorderLayout.SOUTH);

        return main;
    }

    private JComponent buildHeader() {
        JLabel titulo = new JLabel("🛏️ Agregar nueva habitación", SwingConstants.CENTER);
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        return titulo;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG);
        GridBagConstraints c = baseGbc();

        initFields();
        attachListeners();

        addL(form, "Nombre:", c, 0, 0);
        addF(form, nombreField, c, 1, 0);

        addL(form, "Descripción:", c, 0, 1);
        c.fill = GridBagConstraints.BOTH;
        addF(form, new JScrollPane(descripcionArea), c, 1, 1);
        c.fill = GridBagConstraints.HORIZONTAL;

        addL(form, "Precio ($):", c, 0, 2);
        addF(form, precioField, c, 1, 2);

        addL(form, "Tipo:", c, 0, 3);
        addF(form, tipoCombo, c, 1, 3);

        addL(form, "Capacidad:", c, 0, 4);
        addF(form, capacidadValue, c, 1, 4);

        return form;
    }

    private JPanel buildButtonsPanel() {
        JPanel south = new JPanel();
        south.setBackground(BG);

        JButton addBtn = primaryButton("Agregar habitación");
        addBtn.addActionListener(e -> onAddHabitacion());

        south.add(addBtn);
        return south;
    }

    /* =========================
     *        INIT / LISTENERS
     * ========================= */
    private void initFields() {
        nombreField = new JTextField(); nombreField.setFont(FIELD_FONT);

        descripcionArea = new JTextArea(4, 20);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);
        descripcionArea.setFont(FIELD_FONT);

        precioField = new JTextField(); precioField.setFont(FIELD_FONT);

        tipoCombo = new JComboBox<>(Habitacion.tipoHabitacion.values());
        tipoCombo.setFont(FIELD_FONT);

        capacidadValue = new JLabel("-", SwingConstants.LEFT);
        capacidadValue.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // начальные значения
        tipoCombo.setSelectedIndex(0);
        capacidadValue.setText(String.valueOf(capacidadPorTipo((Habitacion.tipoHabitacion) tipoCombo.getSelectedItem())));
    }

    private void attachListeners() {
        tipoCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Habitacion.tipoHabitacion t = (Habitacion.tipoHabitacion) e.getItem();
                capacidadValue.setText(String.valueOf(capacidadPorTipo(t)));
            }
        });
    }

    /* =========================
     *         ACTIONS
     * ========================= */
    private void onAddHabitacion() {
        String nombre = nombreField.getText().trim();
        String descripcion = descripcionArea.getText().trim();
        String precioStr = precioField.getText().trim();
        Habitacion.tipoHabitacion tipo = (Habitacion.tipoHabitacion) tipoCombo.getSelectedItem();

        if (!validateNombre(nombre)) return;

        Float precio = parsePrecio(precioStr);
        if (precio == null) return;

        try {
            Habitacion h = new Habitacion(nombre, descripcion, precio, tipo);
            sistema.agregarHabitacion(h);

            showInfo("✅ Habitación agregada:\n" +
                    "• Nombre: " + nombre + "\n" +
                    "• Tipo: " + tipo + "\n" +
                    "• Capacidad: " + capacidadPorTipo(tipo) + "\n" +
                    "• Precio: $" + precio);

            dispose(); // закрываем после успеха

        } catch (Throwable ex) {
            showError("No se pudo crear la habitación:\n" + ex.getMessage());
        }
    }

    /* =========================
     *       VALIDATION
     * ========================= */
    private boolean validateNombre(String nombre) {
        if (nombre.isEmpty()) {
            showWarn("Ingresá el nombre de la habitación.");
            return false;
        }
        return true;
    }

    private Float parsePrecio(String precioStr) {
        try {
            float p = Float.parseFloat(precioStr);
            if (p <= 0) throw new NumberFormatException();
            return p;
        } catch (NumberFormatException ex) {
            showWarn("Precio inválido. Debe ser un número mayor a 0.");
            return null;
        }
    }

    /* =========================
     *         HELPERS
     * ========================= */
    private static GridBagConstraints baseGbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        return c;
    }

    private void addL(JPanel p, String text, GridBagConstraints c, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LABEL_FONT);
        GridBagConstraints g = (GridBagConstraints) c.clone();
        g.gridx = x; g.gridy = y; g.weightx = 0; g.gridwidth = 1;
        p.add(lbl, g);
    }

    private void addF(JPanel p, JComponent comp, GridBagConstraints c, int x, int y) {
        GridBagConstraints g = (GridBagConstraints) c.clone();
        g.gridx = x; g.gridy = y; g.weightx = 1; g.gridwidth = 1;
        p.add(comp, g);
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return b;
    }

    private void showWarn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
    private void showInfo(String msg)  { JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE); }

    // локальная логика вместимости — подгоняй под свою модель
    private int capacidadPorTipo(Habitacion.tipoHabitacion tipo) {
        switch (tipo) {
            case INDIVIDUAL:   return 2;
            case MATRIMONIAL:  return 3;
            case COMPARTIDA:   return 5;
            default:           return 0;
        }
    }

    /* =========================
     *          MAIN
     * ========================= */
    public static void main(String[] args) {
        Sistema sistema = new Sistema("hotel.db", "hotel.db", "hotel.db", "hotel.db");
        SwingUtilities.invokeLater(() -> new NuevaHabitacionForm(sistema).setVisible(true));
    }
}
