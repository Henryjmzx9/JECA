package utils;

public enum EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA;

    /**
     * Devuelve el nombre del estado como texto.
     * Este método es útil si necesitas mostrar el valor o guardarlo en base de datos.
     */
    public String getValor() {
        return this.name();
    }

    /**
     * Convierte una cadena de texto a un valor del enum EstadoReserva.
     * Si la cadena no es válida, retorna PENDIENTE como valor por defecto.
     *
     * @param estado la cadena de texto a convertir
     * @return el valor del enum correspondiente o PENDIENTE si no coincide
     */
    public static EstadoReserva fromString(String estado) {
        if (estado == null) return PENDIENTE;
        try {
            return EstadoReserva.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDIENTE;
        }
    }
}