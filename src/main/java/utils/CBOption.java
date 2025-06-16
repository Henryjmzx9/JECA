package utils;

public class CBOption {
    private String displayText;
    private Object value;

    // Constructor principal
    public CBOption(String displayText, Object value) {
        this.displayText = displayText;
        this.value = value;
    }

    // Método para obtener el texto que se muestra
    public String getDisplayText() {
        return displayText;
    }

    // Método para obtener el valor asociado
    public Object getValue() {
        return value;
    }

    // Método para obtener el valor como int (manejo seguro)
    public int getIntValue() {
        if (value instanceof Integer) {
            return (int) value;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return -1; // Valor por defecto si no se puede convertir
        }
    }

    @Override
    public String toString() {
        return displayText; // Esto es lo que se mostrará en el JComboBox
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CBOption other = (CBOption) obj;

        if (this.value == null && other.value == null) return true;
        if (this.value == null || other.value == null) return false;

        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}