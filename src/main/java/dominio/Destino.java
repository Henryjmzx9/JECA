package dominio;

public class Destino {
    private int destinoId;
    private String nombre;
    private String pais;
    private String descripcion;
    private byte[] imagen;  // se usa byte[] para VARBINARY(MAX)

    public Destino() {
    }

    public Destino(int destinoId, String nombre, String pais, String descripcion, byte[] imagen) {
        this.destinoId = destinoId;
        this.nombre = nombre;
        this.pais = pais;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public int getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(int destinoId) {
        this.destinoId = destinoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}

