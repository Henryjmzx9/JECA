package dominio;
import java.util.Date;

public class Pago {
    private int pagoId;
    private int reservaId;
    private double monto;
    private int metodoPagoId;
    private Date fechaPago;

    public Pago() {
    }
    public Pago(int pagoId, int reservaId, double monto, int metodoPagoId, Date fechaPago) {
        this.pagoId = pagoId;
        this.reservaId = reservaId;
        this.monto = monto;
        this.metodoPagoId = metodoPagoId;
        this.fechaPago = fechaPago;
    }

    public int getPagoId() {
        return pagoId;
    }

    public void setPagoId(int pagoId) {
        this.pagoId = pagoId;
    }

    public int getReservaId() {
        return reservaId;
    }

    public void setReservaId(int reservaId) {
        this.reservaId = reservaId;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getMetodoPagoId() {
        return metodoPagoId;
    }

    public void setMetodoPagoId(int metodoPagoId) {
        this.metodoPagoId = metodoPagoId;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }
}