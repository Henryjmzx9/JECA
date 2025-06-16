package dominio;

import utils.Rol;

public class  Usuario {
    private int id;
    private String name;
    private String passwordHash;
    private String email;
    private byte status;
    private Rol rol;
    private String telefono;
    private String descripcion;

    public Usuario() {
    }

    public Usuario(int id, String name, String passwordHash, String email, byte status, Rol rol) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.email = email;
        this.status = status;
        this.rol = rol;
    }
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    // Para mostrar en JComboBox
    @Override
    public String toString() {
        return name;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getStrEstatus() {
        String str = "";
        switch (status) {
            case 1:
                str = "ACTIVO";
                break;
            case 0:
                str = "INACTIVO";
                break;
            default:
                str = "DESCONOCIDO";
        }
        return str;
    }
}

