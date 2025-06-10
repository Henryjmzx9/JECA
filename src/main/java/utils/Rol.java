package utils;

public enum Rol {
    Administrador("Administrador"),
    Agente("Agente"),
    Cliente("Cliente");

    private final String valor;

    Rol(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static Rol fromString(String texto) {
        for (Rol rol : Rol.values()) {
            if (rol.getValor().equalsIgnoreCase(texto)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + texto);
    }
}