package aplicacion;

import dominio.Habitacion;
import dominio.Reserva;
import aplicacion.Sistema;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Панель для отображения всех комнат отеля с возможностью фильтрации по датам
 */
public class HabitacionesPanel extends JFrame {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final Sistema sistema;
    private JTable habitacionesTable;
    private JSpinner desdeSpinner;
    private JSpinner hastaSpinner;
    private JButton filtrarBtn;
    private JButton limpiarBtn;
    private TableRowSorter<DefaultTableModel> sorter;

    public HabitacionesPanel(Sistema sistema) {
        this.sistema = sistema;
        setupFrame();
        add(buildMainPanel());
        cargarHabitaciones();
    }

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Заголовок
        JLabel titleLabel = new JLabel("🏨 Todas habitaciones de Hotel");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(ACCENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        main.add(titleLabel, BorderLayout.NORTH);

        // Центральная панель с фильтрами и таблицей
        main.add(createContentPanel(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(BG);

        // Панель фильтров
        content.add(createFilterPanel(), BorderLayout.NORTH);

        // Таблица комнат
        content.add(createTablePanel(), BorderLayout.CENTER);

        return content;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = createCard("Filtro por datos", null, false);
        filterPanel.setLayout(new GridLayout(1, 5, 10, 0));

        // Дата "С"
        JPanel desdePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        desdePanel.setOpaque(false);
        JLabel desdeLabel = new JLabel("Desde:");
        desdeLabel.setFont(LABEL_FONT);
        desdePanel.add(desdeLabel);

        desdeSpinner = new JSpinner(new SpinnerDateModel());
        desdeSpinner.setEditor(new JSpinner.DateEditor(desdeSpinner, "dd/MM/yyyy"));
        desdeSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
        desdePanel.add(desdeSpinner);
        filterPanel.add(desdePanel);

        // Дата "По"
        JPanel hastaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hastaPanel.setOpaque(false);
        JLabel hastaLabel = new JLabel("Hasta:");
        hastaLabel.setFont(LABEL_FONT);
        hastaPanel.add(hastaLabel);

        hastaSpinner = new JSpinner(new SpinnerDateModel());
        hastaSpinner.setEditor(new JSpinner.DateEditor(hastaSpinner, "dd/MM/yyyy"));
        hastaSpinner.setValue(java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
        hastaPanel.add(hastaSpinner);
        filterPanel.add(hastaPanel);

        // Кнопки
        filtrarBtn = new JButton("🔍 Buscar Habitaciones");
        filtrarBtn.setBackground(ACCENT);
        filtrarBtn.setForeground(Color.WHITE);
        filtrarBtn.setFocusPainted(false);
        filtrarBtn.addActionListener(e -> aplicarFiltro());
        filterPanel.add(filtrarBtn);

        limpiarBtn = new JButton("🗑️ Limpiar filtros");
        limpiarBtn.setBackground(new Color(200, 200, 200));
        limpiarBtn.setFocusPainted(false);
        limpiarBtn.addActionListener(e -> limpiarFiltro());
        filterPanel.add(limpiarBtn);

        // Заполнитель
        filterPanel.add(new JPanel());

        return filterPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = createCard("Lista de habitaciones", null, false);
        tablePanel.setLayout(new BorderLayout());

        String[] columnNames = {
                "ID", "Nombre", "Tipo", "Capacidad", "Precio por la noche",
                "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 -> Integer.class;
                    case 3 -> Integer.class;
                    case 4 -> Double.class;
                    default -> String.class;
                };
            }
        };

        habitacionesTable = new JTable(model);
        habitacionesTable.setRowHeight(25);
        habitacionesTable.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(model);
        habitacionesTable.setRowSorter(sorter);

       sorter.setComparator(0, Comparator.comparingInt(o -> Integer.parseInt(o.toString())));
        sorter.setComparator(3, Comparator.comparingInt(o -> Integer.parseInt(o.toString())));
        sorter.setComparator(4, Comparator.comparingDouble(o -> Double.parseDouble(o.toString())));

        JScrollPane scrollPane = new JScrollPane(habitacionesTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void cargarHabitaciones() {
        try {
            DefaultTableModel model = (DefaultTableModel) habitacionesTable.getModel();
            model.setRowCount(0);

            LocalDate desde = convertToLocalDate((java.util.Date) desdeSpinner.getValue());
            LocalDate hasta = convertToLocalDate((java.util.Date) hastaSpinner.getValue());
            List<Habitacion> habitaciones = sistema.listarHabitacionesDisponibles(desde, hasta);

            for (Habitacion hab : habitaciones) {
                boolean disponible = true;

                String estado = disponible ? "🟢 Esta disponible" : "🔴 No esta disponible";

                model.addRow(new Object[]{
                        hab.getId(),
                        hab.getNombre(),
                        hab.getTipo(),
                        hab.getCapacidad(),
                        hab.getPrecio(),
                        estado,
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltro() {
        cargarHabitaciones();
    }

    private void limpiarFiltro() {
        desdeSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
        hastaSpinner.setValue(java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
        cargarHabitaciones();
    }

    private LocalDate convertToLocalDate(java.util.Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    private JPanel createCard(String title, Runnable onRefresh, boolean showRefresh) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(CARD_TITLE_FONT);
        lbl.setForeground(new Color(50, 50, 50));
        header.add(lbl, BorderLayout.WEST);

        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HabitacionesPanel(new Sistema()).setVisible(true));
    }
}