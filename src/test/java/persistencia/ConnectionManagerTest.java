package persistencia;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection; // Importa la clase Connection del paquete java.sql, que se utiliza para establecer una conexión con la base de datos.
import java.sql.SQLException; // Importa la clase SQLException del paquete java.sql, que se utiliza para manejar excepciones relacionadas con operaciones de base de datos.

import static org.junit.jupiter.api.Assertions.*; // Importa todos los métodos estáticos de la clase Assertions del paquete org.junit.jupiter.api. Esto proporciona métodos para realizar aserciones en las pruebas unitarias, como assertEquals, assertTrue, etc.

class ConnectionManagerTest {
    ConnectionManager connectionManager;
    @BeforeEach
    void setUp() throws SQLException {
        // Se ejecuta antes de cada método de prueba.
        // Inicializa el ConnectionManager.
        connectionManager = ConnectionManager.getInstance();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Se ejecuta después de cada método de prueba.
        // Cierra la conexión y limpia los recursos.
        if (connectionManager != null) {
            connectionManager.disconnect();
            connectionManager = null; // Para asegurar que no se use accidentalmente
        }
    }

//comentarios de prueba
    @Test
    void connect() throws SQLException {
        // Intenta establecer una conexión a la base de datos utilizando el método connect() de ConnectionManager.
        Connection conn = connectionManager.connect();
        // Realiza una aserción para verificar que la conexión establecida no sea nula.
        // Si conn es nulo, la prueba fallará con el mensaje "La conexion no debe ser nula".
        assertNotNull(conn, "La conexion no debe ser nula");
        // Realiza una aserción para verificar que la conexión establecida esté abierta.
        // El método isClosed() devuelve true si la conexión está cerrada, por lo que assertFalse espera que devuelva false.
        // Si la conexión está cerrada, la prueba fallará con el mensaje "La conexion debe esta abierta".
        assertFalse(conn.isClosed(), "La conexion debe esta abierta");
        if (conn != null) {
            conn.close(); // Cierra la conexión después de la prueba.
        }
    }
    @Test
    void verBaseTablasYCampos() throws SQLException {
        Connection conn = connectionManager.connect();
        System.out.println("Base conectada: " + conn.getCatalog());

        var stmtTablas = conn.createStatement();
        var rsTablas = stmtTablas.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE'");

        while (rsTablas.next()) {
            String nombreTabla = rsTablas.getString("TABLE_NAME");
            System.out.println("\nTabla: " + nombreTabla);

            // Ahora obtener las columnas de esta tabla
            var stmtColumnas = conn.createStatement();
            var rsColumnas = stmtColumnas.executeQuery(
                    "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + nombreTabla + "'");

            while (rsColumnas.next()) {
                String columna = rsColumnas.getString("COLUMN_NAME");
                String tipoDato = rsColumnas.getString("DATA_TYPE");
                System.out.println("   - Campo: " + columna + " (" + tipoDato + ")");
            }

            rsColumnas.close();
            stmtColumnas.close();
        }

        rsTablas.close();
        stmtTablas.close();
        conn.close();
    }



}