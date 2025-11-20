package validaciones;

public class FunctionalException extends RuntimeException{
    public FunctionalException(String mensaje) {
        super(mensaje);
    }

    public FunctionalException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
