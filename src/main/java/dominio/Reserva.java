package dominio;
import utils.EstadoReserva;
import java.time.LocalDate;

public class Reserva {
    private int reservaId;
    private Cliente cliente;
    private Paquete paquete;
    private LocalDate fechaReserva;
    private EstadoReserva estado;

    public Reserva() {
    }

    public Reserva(int reservaId, Cliente cliente, Paquete paquete, LocalDate fechaReserva, EstadoReserva estado) {
        this.reservaId = reservaId;
        this.cliente = cliente;
        this.paquete = paquete;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }
    public int getReservaId() {
        return reservaId;
    }

    public void setReservaId(int reservaId) {
        this.reservaId = reservaId;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Paquete getPaquete() {
        return paquete;
    }

    public void setPaquete(Paquete paquete) {
        this.paquete = paquete;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}

