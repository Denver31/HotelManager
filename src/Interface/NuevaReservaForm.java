package src.Interface;

import src.classes.Sistema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class NuevaReservaForm extends JFrame {

    private JTextField habitacionIdField; // сюда кладём выбранный ID
    private JTextField dniField;
    private JTextField nombreField;
    private JTextField apellidoField;

    // новые поля дат
    private JSpinner desdeSpinner;
    private JSpinner hastaSpinner;

    public NuevaReservaForm(Sistema sistema) {
        setTitle("Nueva Reserva");
        setSize(560, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color fondo = new Color(245, 247, 250);
        Color acento = new Color(0, 120, 215);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(fondo);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("📝 Crear nueva reserva");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(acento);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(fondo);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        JLabel habitacionLabel = new JLabel("Habitación (ID):");
        JLabel dniLabel = new JLabel("DNI o Pasaporte del cliente:");
        JLabel nombreLabel = new JLabel("Nombre del cliente:");
        JLabel apellidoLabel = new JLabel("Apellido del cliente:");
        JLabel desdeLabel = new JLabel("Desde (fecha):");
        JLabel hastaLabel = new JLabel("Hasta (fecha):");

        Font lf = new Font("Segoe UI", Font.PLAIN, 14);
        habitacionLabel.setFont(lf);
        dniLabel.setFont(lf);
        nombreLabel.setFont(lf);
        apellidoLabel.setFont(lf);
        desdeLabel.setFont(lf);
        hastaLabel.setFont(lf);

        habitacionIdField = new JTextField();
        habitacionIdField.setEditable(false); // не редактируем вручную
        JButton seleccionarBtn = new JButton("Seleccionar habitación…");
        seleccionarBtn.addActionListener(e -> {
            SeleccionarHabitacionDialog dlg = new SeleccionarHabitacionDialog(this, sistema);
            dlg.setVisible(true);
            Integer id = dlg.getSelectedHabitacionId();
            if (id != null) {
                habitacionIdField.setText(String.valueOf(id));
            }
        });

        dniField = new JTextField();
        nombreField = new JTextField();
        apellidoField = new JTextField();

        // --- Спиннеры дат (без внешних библиотек) ---
        Date hoy = new Date();

        SpinnerDateModel desdeModel = new SpinnerDateModel(hoy, null, null, Calendar.DAY_OF_MONTH);
        desdeSpinner = new JSpinner(desdeModel);
        JSpinner.DateEditor desdeEditor = new JSpinner.DateEditor(desdeSpinner, "yyyy-MM-dd");
        desdeSpinner.setEditor(desdeEditor);

        SpinnerDateModel hastaModel = new SpinnerDateModel(hoy, null, null, Calendar.DAY_OF_MONTH);
        hastaSpinner = new JSpinner(hastaModel);
        JSpinner.DateEditor hastaEditor = new JSpinner.DateEditor(hastaSpinner, "yyyy-MM-dd");
        hastaSpinner.setEditor(hastaEditor);

        // --- Размещение ---
        c.gridx = 0; c.gridy = 0; formPanel.add(habitacionLabel, c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1; formPanel.add(habitacionIdField, c);
        c.gridx = 2; c.gridy = 0; c.weightx = 0; formPanel.add(seleccionarBtn, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0; formPanel.add(dniLabel, c);
        c.gridx = 1; c.gridy = 1; c.gridwidth = 2; c.weightx = 1; formPanel.add(dniField, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 2; c.weightx = 0; formPanel.add(nombreLabel, c);
        c.gridx = 1; c.gridy = 2; c.gridwidth = 2; c.weightx = 1; formPanel.add(nombreField, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 3; c.weightx = 0; formPanel.add(apellidoLabel, c);
        c.gridx = 1; c.gridy = 3; c.gridwidth = 2; c.weightx = 1; formPanel.add(apellidoField, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 4; c.weightx = 0; formPanel.add(desdeLabel, c);
        c.gridx = 1; c.gridy = 4; c.gridwidth = 2; c.weightx = 1; formPanel.add(desdeSpinner, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 5; c.weightx = 0; formPanel.add(hastaLabel, c);
        c.gridx = 1; c.gridy = 5; c.gridwidth = 2; c.weightx = 1; formPanel.add(hastaSpinner, c);
        c.gridwidth = 1;

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton crearBtn = new JButton("Crear reserva");
        crearBtn.setBackground(acento);
        crearBtn.setForeground(Color.WHITE);
        crearBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        crearBtn.setFocusPainted(false);
        crearBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        crearBtn.addActionListener(e -> {
            String habitacionIdStr = habitacionIdField.getText().trim();
            String dni = dniField.getText().trim();
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();

            if (habitacionIdStr.isEmpty() || dni.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, completá los campos obligatorios (Habitación y DNI).",
                        "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Получаем и валидируем даты
            LocalDate desde = toLocalDate((Date) desdeSpinner.getValue());
            LocalDate hasta = toLocalDate((Date) hastaSpinner.getValue());

            if (!hasta.isAfter(desde)) {
                JOptionPane.showMessageDialog(this,
                        "La fecha 'Hasta' debe ser posterior a 'Desde'.",
                        "Rango de fechas inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int habitacionId = Integer.parseInt(habitacionIdStr);

            // тут можно вызвать sistema.agregarReserva(habitacionId, idHuespede, factura, desde, hasta);
            // когда появятся id del huésped y la factura.

            JOptionPane.showMessageDialog(this,
                    "✅ Reserva creada con éxito:\n\n" +
                            "Habitación (ID): " + habitacionId +
                            "\nCliente: " + nombre + " " + apellido +
                            "\nDNI/Pasaporte: " + dni +
                            "\nDesde: " + desde +
                            "\nHasta: " + hasta,
                    "Reserva registrada", JOptionPane.INFORMATION_MESSAGE);

            // очистка
            habitacionIdField.setText("");
            dniField.setText("");
            nombreField.setText("");
            apellidoField.setText("");
            desdeSpinner.setValue(new Date());
            hastaSpinner.setValue(new Date());
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(fondo);
        buttonPanel.add(crearBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static void main(String[] args) {
        Sistema sistema = new Sistema("", "", "", "", "", "");
        SwingUtilities.invokeLater(() -> new NuevaReservaForm(sistema).setVisible(true));
    }
}
