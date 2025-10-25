package src.Interface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NuevaReservaForm extends JFrame {

    private JTextField habitacionField;
    private JTextField dniField;
    private JTextField montoField;

    public NuevaReservaForm() {
        setTitle("Nueva Reserva");
        setSize(420, 300);
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

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBackground(fondo);

        JLabel habitacionLabel = new JLabel("ID de habitación:");
        JLabel dniLabel = new JLabel("DNI o Pasaporte del cliente:");
        JLabel montoLabel = new JLabel("Monto a pagar ($):");

        habitacionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dniLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        montoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        habitacionField = new JTextField();
        dniField = new JTextField();
        montoField = new JTextField();

        formPanel.add(habitacionLabel);
        formPanel.add(habitacionField);
        formPanel.add(dniLabel);
        formPanel.add(dniField);
        formPanel.add(montoLabel);
        formPanel.add(montoField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Botón
        JButton crearBtn = new JButton("Crear reserva");
        crearBtn.setBackground(acento);
        crearBtn.setForeground(Color.WHITE);
        crearBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        crearBtn.setFocusPainted(false);
        crearBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        crearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String habitacion = habitacionField.getText().trim();
                String dni = dniField.getText().trim();
                String monto = montoField.getText().trim();

                if (habitacion.isEmpty() || dni.isEmpty() || monto.isEmpty()) {
                    JOptionPane.showMessageDialog(NuevaReservaForm.this,
                            "Por favor, completá todos los campos.",
                            "Campos incompletos",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(NuevaReservaForm.this,
                        "✅ Reserva creada con éxito:\n\n" +
                                "Habitación: " + habitacion +
                                "\nCliente (DNI/Pasaporte): " + dni +
                                "\nMonto a pagar: $" + monto,
                        "Reserva registrada",
                        JOptionPane.INFORMATION_MESSAGE);

                // Limpiar campos
                habitacionField.setText("");
                dniField.setText("");
                montoField.setText("");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(fondo);
        buttonPanel.add(crearBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NuevaReservaForm().setVisible(true));
    }
}
