package dominio;

public class Cliente {
    private int clienteId;
    private int userId;
    private String telefono;
    private String direccion;
    public Cliente() {
    }

    public Cliente(int clienteId, int userId, String telefono, String direccion) {
        this.clienteId = clienteId;
        this.userId = userId;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Cliente(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // Agregado para mostrar algo representativo en la UI
    public String getNombreCompleto() {
        // Si en algún momento tienes nombre y apellido como atributos, reemplaza esta línea
        return "Cliente #" + clienteId;
    }
}