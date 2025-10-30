package src.classes;

public class Huespede {
    private String nombre;
    private String DNI;

    public Huespede(String nombre, String DNI) {
        this.nombre = nombre;
        this.DNI = DNI;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDNI() {
        return DNI;
    }
}
