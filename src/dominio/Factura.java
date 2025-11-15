package dominio;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public class Factura {

    public enum MetodoPago {
        TRANSFERENCIA,
        TARJETA
    }

    private int id;
    private double total;
    private boolean pagada;
    private LocalDate fechaPago;
    private MetodoPago metodo;
    private int cuotas;
    private LocalDate vencimiento;

    // Constructor principal (nueva factura)
    public Factura(double total, MetodoPago metodo, int cuotas, LocalDate vencimiento) {
        if (metodo == null || vencimiento == null)
            throw new IllegalArgumentException("Método de pago y vencimiento son obligatorios.");
        if (total <= 0)
            throw new IllegalArgumentException("El total debe ser mayor a 0.");

        this.total = total;
        this.metodo = metodo;
        this.cuotas = cuotas;
        this.vencimiento = vencimiento;
        this.pagada = false;
        this.fechaPago = null;
    }

    // Constructor alternativo (para reconstrucción desde BD)
    public Factura(int id, double total, MetodoPago metodo, int cuotas, LocalDate vencimiento, boolean pagada, LocalDate fechaPago) {
        this.id = id;
        this.total = total;
        this.metodo = metodo;
        this.cuotas = cuotas;
        this.vencimiento = vencimiento;
        this.pagada = pagada;
        this.fechaPago = fechaPago;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public double getTotal() { return total; }
    public void setTotal(double total) {
        if (total <= 0) throw new IllegalArgumentException("El total debe ser mayor a 0");
        this.total = total;
    }

    public boolean isPagada() { return pagada; }
    public LocalDate getFechaPago() { return fechaPago; }

    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) {
        if (metodo == null) throw new IllegalArgumentException("Método de pago no puede ser null");
        this.metodo = metodo;
    }

    public int getCuotas() { return cuotas; }
    public void setCuotas(int cuotas) {
        if (cuotas < 0) throw new IllegalArgumentException("Cuotas no puede ser negativo");
        this.cuotas = cuotas;
    }

    public LocalDate getVencimiento() { return vencimiento; }
    public void setVencimiento(LocalDate vencimiento) {
        if (vencimiento == null) throw new IllegalArgumentException("Vencimiento no puede ser null");
        this.vencimiento = vencimiento;
    }

    public void registrarPago() {
        if (this.pagada)
            throw new IllegalStateException("La factura ya fue pagada");
        this.pagada = true;
        this.fechaPago = LocalDate.now();
    }

    public boolean esVencida() {
        return !isPagada() && LocalDate.now().isAfter(vencimiento);
    }

    public boolean estaActiva() {
        return !pagada && !esVencida();
    }
}
