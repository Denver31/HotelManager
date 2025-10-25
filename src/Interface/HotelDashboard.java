package src.Interface;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class HotelDashboard extends JFrame {

    public HotelDashboard() {
        setTitle("Hotel Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        Color bgColor = new Color(245, 247, 250);
        Color cardColor = new Color(255, 255, 255);
        Color accent = new Color(0, 120, 215);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(bgColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JLabel title = new JLabel("📊 Dashboard - Resumen");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(accent);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        mainPanel.add(title, gbc);
        gbc.gridwidth = 1;

        // Пример данных
        String[] entradas = {"12/11 - Hab. 101 - Juan Pérez", "12/11 - Hab. 204 - María López"};
        String[] salidas = {"12/11 - Hab. 102 - Ana Ruiz", "13/11 - Hab. 305 - Carlos Díaz"};
        String[] pendientes = {"Reserva #345 - 2 noches - $150", "Reserva #346 - 1 noche - $80"};
        String[] facturas = {"Factura #120 - $200 - vencida", "Factura #121 - $50 - vencida"};

        // Панель Ocupación
        JPanel ocupacionPanel = createCard("Ocupación", cardColor);
        JLabel ocupLabel = new JLabel("Ocupación: 72%");
        ocupLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ocupLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ocupacionPanel.add(ocupLabel, BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.5; gbc.weighty = 0.2;
        mainPanel.add(ocupacionPanel, gbc);

        // Próximas entradas
        JPanel entradasPanel = createCard("Próximas entradas", cardColor);
        entradasPanel.add(createList(entradas), BorderLayout.CENTER);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        mainPanel.add(entradasPanel, gbc);

        // Accesos rápidos
        JPanel accesosPanel = createCard("Accesos rápidos", cardColor);
        accesosPanel.setLayout(new GridLayout(3, 1, 10, 10));
        String[] botones = {"Nueva reserva", "Check-in", "Check-out"};
        for (String b : botones) {
            JButton btn = new JButton(b);
            btn.setFocusPainted(false);
            btn.setBackground(accent);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

            // Acción para cada botón
            btn.addActionListener(e -> {
                switch (b) {
                    case "Nueva reserva":
                        // Abrir la ventana de nueva reserva
                        SwingUtilities.invokeLater(() -> new NuevaReservaForm().setVisible(true));
                        break;
                    case "Check-in":
                        JOptionPane.showMessageDialog(this, "Funcionalidad de check-in próximamente.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case "Check-out":
                        JOptionPane.showMessageDialog(this, "Funcionalidad de check-out próximamente.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        break;
                }
            });

            accesosPanel.add(btn);
        }
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridheight = 3;
        gbc.weightx = 0.3;
        mainPanel.add(accesosPanel, gbc);
        gbc.gridheight = 1;

        // Próximas salidas
        JPanel salidasPanel = createCard("Próximas salidas", cardColor);
        salidasPanel.add(createList(salidas), BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.5;
        mainPanel.add(salidasPanel, gbc);

        // Reservas pendientes
        JPanel pendientesPanel = createCard("Reservas pendientes de cobro", cardColor);
        pendientesPanel.add(createList(pendientes), BorderLayout.CENTER);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        mainPanel.add(pendientesPanel, gbc);

        // Facturas vencidas (широкое поле)
        JPanel facturasPanel = createCard("Facturas vencidas", cardColor);
        facturasPanel.add(createList(facturas), BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.weightx = 1.0; gbc.weighty = 0.4;
        mainPanel.add(facturasPanel, gbc);

        add(mainPanel);
    }

    private JPanel createCard(String title, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(50, 50, 50));
        panel.add(lbl, BorderLayout.NORTH);

        return panel;
    }

    private JScrollPane createList(String[] data) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String item : data) model.addElement("• " + item);
        JList<String> list = new JList<>(model);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setBackground(new Color(250, 250, 250));
        list.setVisibleRowCount(4);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(300, 100));
        return scroll;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelDashboard().setVisible(true));
    }
}
