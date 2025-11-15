package validaciones;

public class PagoRequeridoException extends RuntimeException {
    public PagoRequeridoException(String mensaje) {
        super(mensaje);
    }
}
