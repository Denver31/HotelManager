package dominio;

import java.time.LocalDate;
import validaciones.InputException;
import validaciones.BusinessRuleException;

public class Factura {

    public enum MetodoPago {
        TRANSFERENCIA,
        TARJETA
    }

    public enum EstadoFactura {
        PENDIENTE,
        PAGADA,
        CANCELADA,
        VENCIDA
    }

    private int id;
    private final double total;
    private final MetodoPago metodo;
    private final int cuotas;

    private final LocalDate fechaAlta;
    private final LocalDate vencimiento;

    private EstadoFactura estado;
    private LocalDate fechaPago;

    // ============================================================
    // Constructor para facturas NUEVAS
    // ============================================================
    public Factura(double total, MetodoPago metodo, int cuotas, LocalDate vencimiento) {

        if (total <= 0)
            throw new InputException("El total debe ser mayor a 0.");

        if (metodo == null)
            throw new InputException("Debe especificar un método de pago.");

        if (vencimiento == null)
            throw new InputException("La fecha de vencimiento es obligatoria.");

        validarCuotas(metodo, cuotas);

        this.total = total;
        this.metodo = metodo;
        this.cuotas = cuotas;

        this.fechaAlta = LocalDate.now();
        this.vencimiento = vencimiento;

        this.estado = EstadoFactura.PENDIENTE;
        this.fechaPago = null;
    }

    // ============================================================
    // Constructor RECONSTRUCCIÓN (desde DB)
    // ============================================================
    public Factura(int id, double total, MetodoPago metodo, int cuotas,
                   LocalDate fechaAlta, LocalDate vencimiento,
                   EstadoFactura estado, LocalDate fechaPago) {

        this.id = id;
        this.total = total;
        this.metodo = metodo;
        this.cuotas = cuotas;
        this.fechaAlta = fechaAlta;
        this.vencimiento = vencimiento;
        this.estado = estado;
        this.fechaPago = fechaPago;
    }

    // ============================================================
    // Getters
    // ============================================================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getTotal() { return total; }
    public MetodoPago getMetodo() { return metodo; }
    public int getCuotas() { return cuotas; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public LocalDate getVencimiento() { return vencimiento; }
    public EstadoFactura getEstado() { return estado; }
    public LocalDate getFechaPago() { return fechaPago; }

    public boolean estaPendiente() { return estado == EstadoFactura.PENDIENTE; }
    public boolean estaPagada() { return estado == EstadoFactura.PAGADA; }
    public boolean estaCancelada() { return estado == EstadoFactura.CANCELADA; }
    public boolean estaVencida() { return estado == EstadoFactura.VENCIDA; }

    // ============================================================
    // Reglas de negocio
    // ============================================================
    public void registrarPago() {
        if (!estaPendiente())
            throw new BusinessRuleException("Solo se pueden pagar facturas pendientes.");

        this.estado = EstadoFactura.PAGADA;
        this.fechaPago = LocalDate.now();
    }

    public void cancelar() {
        if (estaPagada())
            throw new BusinessRuleException("No se puede cancelar una factura pagada.");
        this.estado = EstadoFactura.CANCELADA;
    }

    public void evaluarVencimiento() {
        if (estaPendiente() && LocalDate.now().isAfter(vencimiento)) {
            this.estado = EstadoFactura.VENCIDA;
        }
    }

    private static void validarCuotas(MetodoPago metodo, int cuotas) {
        if (cuotas < 1)
            throw new InputException("La cantidad de cuotas debe ser >= 1.");

        if (metodo == MetodoPago.TRANSFERENCIA && cuotas != 1)
            throw new InputException("Las transferencias solo permiten 1 cuota.");

        if (metodo == MetodoPago.TARJETA && (cuotas < 1 || cuotas > 3))
            throw new InputException("Las tarjetas permiten entre 1 y 3 cuotas.");
    }
}