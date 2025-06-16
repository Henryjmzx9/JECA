package dominio;

public class Cliente {
    private int clienteId;
    private int userId;
    private String telefono;
    private String direccion;
    private String nombre;  // <-- Lo agregamos

    public Cliente() {
    }

    public Cliente(int clienteId, int userId, String telefono, String direccion, String nombre) {
        this.clienteId = clienteId;
        this.userId = userId;
        this.telefono = telefono;
        this.direccion = direccion;
        this.nombre = nombre;
    }

    public Cliente(int clienteId) {
        this.clienteId = clienteId;
    }

    public Cliente(int clienteId, String nombre) {
        this.clienteId = clienteId;
        this.nombre = nombre;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Este método lo podés dejar o quitar según cómo prefieras mostrar en tablas
    public String getNombreCompleto() {
        return nombre != null ? nombre : "Cliente #" + clienteId;
    }
}