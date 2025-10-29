package src.Interface;

import src.classes.Sistema;
import src.classes.Habitacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

public class NuevaHabitacionForm extends JFrame {

    private JTextField nombreField;
    private JTextArea descripcionArea;
    private JTextField precioField;
    private JComboBox<Habitacion.tipoHabitacion> tipoCombo;
    private JLabel capacidadValue;

    public NuevaHabitacionForm(Sistema sistema) {
        setTitle("Nueva Habitación");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color fondo = new Color(245, 247, 250);
        Color acento = new Color(0, 120, 215);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBackground(fondo);
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("🛏️ Agregar nueva habitación");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(acento);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titulo, BorderLayout.NORTH);

        // --------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(fondo);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel nombreLabel = new JLabel("Nombre:");
        JLabel descripcionLabel = new JLabel("Descripción:");
        JLabel precioLabel = new JLabel("Precio ($):");
        JLabel tipoLabel = new JLabel("Tipo:");
        JLabel capacidadLabel = new JLabel("Capacidad:");

        Font lf = new Font("Segoe UI", Font.PLAIN, 14);
        nombreLabel.setFont(lf);
        descripcionLabel.setFont(lf);
        precioLabel.setFont(lf);
        tipoLabel.setFont(lf);
        capacidadLabel.setFont(lf);

        nombreField = new JTextField();
        nombreField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        descripcionArea = new JTextArea(4, 20);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);
        descripcionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane descripcionScroll = new JScrollPane(descripcionArea);

        precioField = new JTextField();
        precioField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tipoCombo = new JComboBox<>(Habitacion.tipoHabitacion.values());
        tipoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        capacidadValue = new JLabel("-");
        capacidadValue.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // динамически показываем вместимость по выбранному типу
        tipoCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Habitacion.tipoHabitacion t = (Habitacion.tipoHabitacion) e.getItem();
                capacidadValue.setText(String.valueOf(capacidadPorTipo(t)));
            }
        });
        // выставим начальное значение
        tipoCombo.setSelectedIndex(0);
        capacidadValue.setText(String.valueOf(capacidadPorTipo((Habitacion.tipoHabitacion) tipoCombo.getSelectedItem())));

        // раскладка
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        form.add(nombreLabel, c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        form.add(nombreField, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        form.add(descripcionLabel, c);
        c.gridx = 1; c.gridy = 1; c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        form.add(descripcionScroll, c);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        form.add(precioLabel, c);
        c.gridx = 1; c.gridy = 2; c.weightx = 1;
        form.add(precioField, c);

        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        form.add(tipoLabel, c);
        c.gridx = 1; c.gridy = 3; c.weightx = 1;
        form.add(tipoCombo, c);

        c.gridx = 0; c.gridy = 4; c.weightx = 0;
        form.add(capacidadLabel, c);
        c.gridx = 1; c.gridy = 4; c.weightx = 1;
        form.add(capacidadValue, c);

        mainPanel.add(form, BorderLayout.CENTER);

        // --------- BUTTONS ----------
        JButton agregarBtn = new JButton("Agregar habitación");
        agregarBtn.setBackground(acento);
        agregarBtn.setForeground(Color.WHITE);
        agregarBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        agregarBtn.setFocusPainted(false);
        agregarBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        agregarBtn.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            String descripcion = descripcionArea.getText().trim();
            String precioStr = precioField.getText().trim();
            Habitacion.tipoHabitacion tipo = (Habitacion.tipoHabitacion) tipoCombo.getSelectedItem();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresá el nombre de la habitación.",
                        "Falta nombre", JOptionPane.WARNING_MESSAGE);
                return;
            }
            float precio;
            try {
                precio = Float.parseFloat(precioStr);
                if (precio <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido. Debe ser un número mayor a 0.",
                        "Precio inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Habitacion habitacion = new Habitacion(nombre, descripcion, precio, tipo);
                sistema.agregarHabitacion(habitacion);

                JOptionPane.showMessageDialog(this,
                        "✅ Habitación agregada:\n" +
                                "• Nombre: " + nombre + "\n" +
                                "• Tipo: " + tipo + "\n" +
                                "• Capacidad: " + capacidadPorTipo(tipo) + "\n" +
                                "• Precio: $" + precio,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // 🔚 Закрываем форму после отправки данных
                dispose();

            } catch (Throwable ex) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo crear la habitación:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel south = new JPanel();
        south.setBackground(fondo);
        south.add(agregarBtn);
        mainPanel.add(south, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // локальная логика для подсказки по вместимости (соответствует вашей модели)
    private int capacidadPorTipo(Habitacion.tipoHabitacion tipo) {
        switch (tipo) {
            case INDIVIDUAL:   return 2;
            case MATRIMONIAL:  return 3;
            case COMPARTIDA:   return 5;
            default:           return 0;
        }
    }

    // Тестовый запуск
    public static void main(String[] args) {
        Sistema sistema = new Sistema("", "", "", "", "", "");
        SwingUtilities.invokeLater(() -> new NuevaHabitacionForm(sistema).setVisible(true));
    }
}
