package dominio;

import java.time.LocalDate;

public class Paquete {
    private int paqueteId;
    private String nombre;
    private String descripcion;
    private double precio;
    private int duracionDias;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int destinoId;
    private Destino destino;

    public Paquete() {
    }
    public Paquete(int paqueteId, String nombre, String descripcion, double precio, int duracionDias, LocalDate fechaInicio, LocalDate fechaFin, int destinoId) {
        this.paqueteId = paqueteId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionDias = duracionDias;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.destinoId = destinoId;
    }

    public int getPaqueteId() {
        return paqueteId;
    }

    public void setPaqueteId(int paqueteId) {
        this.paqueteId = paqueteId;
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

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(int duracionDias) {
        this.duracionDias = duracionDias;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(int destinoId) {
        this.destinoId = destinoId;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }

    public Destino getDestino() {
        return destino;
    }
}
