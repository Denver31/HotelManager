package src.classes;

public class Habitacion {

    public enum tipoHabitacion {
        INDIVIDUAL,
        MATRIMONIAL,
        COMPARTIDA
    }

    private String nombre;
    private String descripcion;
    private double precio;
    private int capacidad;


    public Habitacion(String nombre, String descripcion, double precio, tipoHabitacion tipo){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        switch (tipo) {
            case INDIVIDUAL:
                this.capacidad = 2;
                break;
            case MATRIMONIAL:
                this.capacidad = 3;
                break;
            case COMPARTIDA:
                this.capacidad = 5;
                break;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getCapacidad() {
        return capacidad;
    }
}

