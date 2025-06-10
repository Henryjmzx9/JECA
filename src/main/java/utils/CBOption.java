package utils;

public class CBOption {
    private String displayText;
    private Object value;

    public CBOption(String displayText, Object value) {
        this.displayText = displayText;
        this.value = value;
    }

    public String getDisplayText() {
        return displayText;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return displayText; // Esto es lo que se mostrará en el JComboBox
    }
    @Override
    public boolean equals(Object obj) {
        // Verifica si el objeto con el que se está comparando es nulo.
        // Si es nulo, no puede ser igual a esta instancia, por lo tanto, retorna falso.
        if (obj == null) {
            return false;
        }
        // Verifica si el objeto con el que se está comparando pertenece a una clase diferente.
        // Si las clases no son las mismas, los objetos no pueden ser iguales, por lo tanto, retorna falso.
        if (getClass() != obj.getClass()) {
            return false;
        }
        // Realiza un casting del objeto genérico 'obj' a la clase específica 'CBOption'
        // para poder acceder a sus atributos y métodos.
        final CBOption other = (CBOption) obj;
        // Compara el valor del atributo 'value' de esta instancia con el valor del atributo 'value'
        // del objeto 'other'. Si los valores no son iguales, los objetos no son iguales,
        // por lo tanto, retorna falso.
        if (this.getValue() != other.getValue()) {
            return false;
        }
        // Si todas las verificaciones anteriores pasan (el objeto no es nulo, pertenece a la misma
        // clase y el valor del atributo 'value' es el mismo), entonces los objetos se consideran iguales
        // y se retorna verdadero. En este caso, la igualdad se basa únicamente en el valor del
        // atributo 'value'. Si la clase 'CBOption' tiene más atributos relevantes para la igualdad,
        // también deberían compararse aquí.
        return true;
    }
}