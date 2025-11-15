package dominio;

import java.util.concurrent.atomic.AtomicInteger;

public class Huesped {

    private int id;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;

    public Huesped(String dni, String nombre, String apellido, String email) {
        if (dni == null || dni.isEmpty())
            throw new IllegalArgumentException("El DNI no puede ser vacío.");
        if (nombre == null || nombre.isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser vacío.");
        if (apellido == null || apellido.isEmpty())
            throw new IllegalArgumentException("El apellido no puede ser vacío.");
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Constructor alternativo (para reconstrucción desde BD)
    public Huesped(int id, String dni, String nombre, String apellido, String email) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDni() { return dni; }
    public void setDni(String dni) {
        if (dni == null || dni.isEmpty())
            throw new IllegalArgumentException("El DNI no puede ser vacío.");
        this.dni = dni;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser vacío.");
        this.nombre = nombre;
    }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) {
        if (apellido == null || apellido.isEmpty())
            throw new IllegalArgumentException("El apellido no puede ser vacío.");
        this.apellido = apellido;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return String.format("Huesped[id=%d, dni=%s, nombre=%s %s]", id, dni, nombre, apellido);
    }
}
