// TODO: agregar logica de pagos parciales/totales
package src.classes;

import java.time.LocalDate;

public class Factura {
    public enum tipoDePago {
        PARCIAL,
        TOTAL
    }

    public enum metodoDePago {
        TRANSFERENCIA,
        TARJETA_DE_CREDITO,
    }

    final double precision = 0.001;


    private double total;
    private double pagado;
    private int cantPagos;
    private tipoDePago tipo;
    private metodoDePago metodo;
    private LocalDate pagarHasta;
    public Factura(double total, tipoDePago tipo, metodoDePago metodo, int cantPagos, LocalDate pagarHasta) {
        this.tipo = tipo;
        this.pagado = 0;
        this.pagarHasta = pagarHasta;
        this.metodo=metodo;

        switch (metodo) {
            case TRANSFERENCIA:
                this.cantPagos = 1;
                this.total = total * 0.95;
                break;
            case TARJETA_DE_CREDITO:
                this.cantPagos = cantPagos;
                this.total = total;
                break;
        }
    }

    public void pagar() {
        pagado += total/cantPagos;
        pagarHasta = pagarHasta.plusDays(7);
    }

    public double getTotal() {
        return total;
    }

    public double getPagado() {
        return pagado;
    }

    public int getCantPagos() {
        return cantPagos;
    }

    public tipoDePago getTipo() {
        return tipo;
    }

    public LocalDate getPagarHasta() {
        return pagarHasta;
    }

    public boolean esPagado() {
        return total - pagado < precision;
    }

    public boolean esExpirado() {
        LocalDate today = LocalDate.now();
        return today.isAfter(pagarHasta);
    }
    public metodoDePago getMetodo(){
        return this.metodo;
    }
}
