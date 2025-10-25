package src.classes;

public class Habitacion {

    public enum tipoHabitacion {
        INDIVIDUAL,
        MATRIMONIAL,
        COMPARTIDA
    }

    private String nombre;
    private String descripcion;
    private float precio;
    private int capacidad;


    public Habitacion(String nombre, String descripcion, float precio, tipoHabitacion tipo) throws Throwable{
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        switch (tipo) {
            case INDIVIDUAL:
                this.capacidad = 2;
            case MATRIMONIAL:
                this.capacidad = 3;
            case COMPARTIDA:
                this.capacidad = 5;
        }

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getCapacidad() {
        return capacidad;
    }
}

