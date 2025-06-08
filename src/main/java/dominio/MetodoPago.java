package dominio;

public class MetodoPago {
    private int metodoPagoId;
    private String nombreMetodo;

    public MetodoPago() {
    }

    public MetodoPago(int metodoPagoId, String nombreMetodo) {
        this.metodoPagoId = metodoPagoId;
        this.nombreMetodo = nombreMetodo;
    }

    public int getMetodoPagoId() {
        return metodoPagoId;
    }

    public void setMetodoPagoId(int metodoPagoId) {
        this.metodoPagoId = metodoPagoId;
    }

    public String getNombreMetodo() {
        return nombreMetodo;
    }

    public void setNombreMetodo(String nombreMetodo) {
        this.nombreMetodo = nombreMetodo;
    }

    @Override
    public String toString() {
        return nombreMetodo;
    }
}