package aplicacion.reservaUi;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DialogBuscarHuesped extends JDialog {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(0, 120, 215);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Criterios
    private JTextField txtId;
    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;

    private JButton btnBuscar;
    private JButton btnCerrar;

    // Resultados
    private JTable tabla;
    private HuespedTableModel tableModel;
    private JButton btnSeleccionar;

    // Resultado elegido
    public static class HuespedSeleccionado {
        public final int id;
        public final String dni;
        public final String nombre;
        public final String apellido;
        public final String email;

        public HuespedSeleccionado(int id, String dni, String nombre, String apellido, String email) {
            this.id = id;
            this.dni = dni;
            this.nombre = nombre;
            this.apellido = apellido;
            this.email = email;
        }
    }

    private HuespedSeleccionado seleccionado;

    public DialogBuscarHuesped(Window owner,
                               String idInicial,
                               String dniInicial,
                               String nombreInicial,
                               String apellidoInicial,
                               String emailInicial) {
        super(owner, "Buscar Huésped", ModalityType.APPLICATION_MODAL);

        setSize(750, 500);
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(700, 450));

        initComponents();

        txtId.setText(idInicial);
        txtDni.setText(dniInicial);
        txtNombre.setText(nombreInicial);
        txtApellido.setText(apellidoInicial);
        txtEmail.setText(emailInicial);

        setContentPane(buildMainPanel());
        initListeners();
    }

    private void initComponents() {

        txtId = new JTextField(8);
        txtDni = new JTextField(10);
        txtNombre = new JTextField(12);
        txtApellido = new JTextField(12);
        txtEmail = new JTextField(15);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(ACCENT);
        btnBuscar.setForeground(Color.WHITE);

        btnCerrar = new JButton("Cerrar");

        tableModel = new HuespedTableModel();
        tabla = new JTable(tableModel);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(true);

        btnSeleccionar = new JButton("Seleccionar");
        btnSeleccionar.setEnabled(false);
    }

    private JPanel buildMainPanel() {

        JPanel global = new JPanel(new BorderLayout(10, 10));
        global.setBackground(BG);
        global.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Buscar Huésped");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ACCENT);
        titulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        global.add(titulo, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        center.add(createCriteriaCard(), BorderLayout.NORTH);
        center.add(createTableCard(), BorderLayout.CENTER);

        global.add(center, BorderLayout.CENTER);
        global.add(createBottomButtons(), BorderLayout.SOUTH);

        return global;
    }

    private JPanel createCriteriaCard() {

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // Fila 1: ID, DNI
        gbc.gridy = y;
        gbc.gridx = 0;
        card.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        card.add(txtId, gbc);

        gbc.gridx = 2;
        card.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 3;
        card.add(txtDni, gbc);

        // Fila 2: Nombre, Apellido
        gbc.gridy = ++y;
        gbc.gridx = 0;
        card.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        card.add(txtNombre, gbc);

        gbc.gridx = 2;
        card.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 3;
        card.add(txtApellido, gbc);

        // Fila 3: Email + botón buscar
        gbc.gridy = ++y;
        gbc.gridx = 0;
        card.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(txtEmail, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        card.add(btnBuscar, gbc);

        return card;
    }

    private JPanel createTableCard() {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(600, 250));

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBottomButtons() {

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.setOpaque(false);

        bottom.add(btnSeleccionar);
        bottom.add(btnCerrar);

        return bottom;
    }

    private void initListeners() {

        btnCerrar.addActionListener(e -> dispose());

        btnBuscar.addActionListener(e -> ejecutarBusqueda());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnSeleccionar.setEnabled(tabla.getSelectedRow() != -1);
            }
        });

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    seleccionarFilaActualYSalir();
                }
            }
        });

        btnSeleccionar.addActionListener(e -> seleccionarFilaActualYSalir());
    }

    private void ejecutarBusqueda() {

        String idStr = txtId.getText().trim();
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();

        // TODO: reemplazar por llamada a Sistema/Controller
        List<HuespedSeleccionado> base = crearMockHuespedes();

        List<HuespedSeleccionado> resultado = filtrar(base, idStr, dni, nombre, apellido, email);

        if (resultado.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron huéspedes con ese criterio.",
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            tableModel.setDatos(new ArrayList<>());
            return;
        }

        if (resultado.size() == 1) {
            // Un solo resultado → lo tomamos directo y cerramos
            seleccionado = resultado.get(0);
            dispose();
            return;
        }

        // Más de uno → mostrar en tabla
        tableModel.setDatos(resultado);
    }

    private List<HuespedSeleccionado> crearMockHuespedes() {
        List<HuespedSeleccionado> list = new ArrayList<>();
        list.add(new HuespedSeleccionado(1, "30123456", "Juan", "Pérez", "juan@example.com"));
        list.add(new HuespedSeleccionado(2, "28999888", "Ana", "Gómez", "ana@example.com"));
        list.add(new HuespedSeleccionado(3, "31222333", "Juan", "Pérez", "otrojuan@example.com"));
        return list;
    }

    private List<HuespedSeleccionado> filtrar(List<HuespedSeleccionado> base,
                                              String idStr,
                                              String dni,
                                              String nombre,
                                              String apellido,
                                              String email) {

        List<HuespedSeleccionado> out = new ArrayList<>();

        // Prioridad según lo que pediste:
        // 1) ID, 2) DNI, 3) Nombre+Apellido, 4) Email

        if (!idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                for (HuespedSeleccionado h : base) {
                    if (h.id == id) out.add(h);
                }
            } catch (NumberFormatException ignored) {}
            return out;
        }

        if (!dni.isEmpty()) {
            for (HuespedSeleccionado h : base) {
                if (h.dni.equalsIgnoreCase(dni)) out.add(h);
            }
            return out;
        }

        if (!nombre.isEmpty() && !apellido.isEmpty()) {
            String n = nombre.toLowerCase();
            String a = apellido.toLowerCase();
            for (HuespedSeleccionado h : base) {
                if (h.nombre.toLowerCase().contains(n)
                        && h.apellido.toLowerCase().contains(a)) {
                    out.add(h);
                }
            }
            return out;
        }

        if (!email.isEmpty()) {
            String em = email.toLowerCase();
            for (HuespedSeleccionado h : base) {
                if (h.email.toLowerCase().equals(em)) out.add(h);
            }
            return out;
        }

        // Si no hay criterios → devolver lista vacía
        return out;
    }

    private void seleccionarFilaActualYSalir() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow == -1) return;

        int row = tabla.convertRowIndexToModel(viewRow);
        HuespedSeleccionado h = tableModel.getAt(row);
        seleccionado = h;
        dispose();
    }

    public HuespedSeleccionado getSeleccionado() {
        return seleccionado;
    }

    // =======================
    // TABLE MODEL
    // =======================
    private static class HuespedTableModel extends AbstractTableModel {

        private final String[] columnas = {
                "ID", "DNI", "Nombre", "Apellido", "Email"
        };

        private List<HuespedSeleccionado> filas = new ArrayList<>();

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HuespedSeleccionado h = filas.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> h.id;
                case 1 -> h.dni;
                case 2 -> h.nombre;
                case 3 -> h.apellido;
                case 4 -> h.email;
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Integer.class : String.class;
        }

        public void setDatos(List<HuespedSeleccionado> lista) {
            this.filas = new ArrayList<>(lista);
            fireTableDataChanged();
        }

        public HuespedSeleccionado getAt(int row) {
            return filas.get(row);
        }
    }
}