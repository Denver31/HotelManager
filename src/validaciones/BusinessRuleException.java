package validaciones;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException (String mensage) {
        super(mensage);
    }

    public BusinessRuleException (String mensage, Throwable cause) {
            super(mensage, cause);
    }
}

