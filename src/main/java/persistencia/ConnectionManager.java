package persistencia;

import java.sql.Connection; // Representa una conexión a la base de datos.
import java.sql.DriverManager; // Gestiona los drivers JDBC y establece conexiones.
import java.sql.SQLException; // Representa errores específicos de la base de datos.

/**
 * Esta clase se encarga de gestionar la conexión a la base de datos SQL Server utilizando JDBC.
 * Implementa el patrón Singleton para asegurar que solo exista una única instancia
 * de la clase y, por lo tanto, una única conexión a la base de datos compartida.
 */
public class ConnectionManager {
    /**
     * Define la cadena de conexión a la base de datos. Contiene la información
     * necesaria para establecer la comunicación con el servidor de base de datos.
     *
     * - jdbc:sqlserver://... : Indica el tipo de conexión (JDBC para SQL Server).
     * - DESKTOP-9KSCT6V\\SQLEXPRESS01:1433 : Dirección del servidor y puerto.
     * - encrypt=true : Indica si la conexión debe ser encriptada.
     * - database=Inventario : Especifica la base de datos a la que se quiere conectar.
     * - trustServerCertificate=true : Indica que se confíe en el certificado del servidor (para entornos de desarrollo).
     * - user=dev : Nombre de usuario para la autenticación.
     * - password=dev : Contraseña para la autenticación.
     */
    private static final String STR_CONNECTION = "jdbc:sqlserver://DESKTOP-0TG6QTK\\SQLEXPRESS:1433;" +
            "encrypt=true;" +
            "database=AgenciaViajesDB;" +
            "trustServerCertificate=true;" +
            "user=java2025;" +
            "password=12345;";

    /**
     * Representa la conexión activa a la base de datos. Inicialmente es nula.
     */
    private Connection connection;

    /**
     * Única instancia de la clase JDBCConnectionManager (para el patrón Singleton).
     * Se inicializa a null y se crea solo cuando se necesita por primera vez.
     */
    private static ConnectionManager instance;

    /**
     * Constructor privado para evitar la creación de instancias directamente desde fuera de la clase.
     * Esto es fundamental para el patrón Singleton.
     */
    private ConnectionManager() {
        this.connection = null;
        try {
            // Carga el driver JDBC de Microsoft SQL Server. Esto es necesario para que Java pueda
            // comunicarse con la base de datos SQL Server.
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            // Si el driver no se encuentra, se lanza una excepción indicando el error.
            throw new RuntimeException("Error al cargar el driver JDBC de SQL Server", e);
        }
    }

    /**
     * Este método se encarga de establecer la conexión con la base de datos.
     * Es sincronizado (`synchronized`) para asegurar que solo un hilo a la vez pueda
     * intentar establecer la conexión, lo cual es importante en entornos multihilo.
     *
     * @return La instancia de la conexión a la base de datos.
     * @throws SQLException Si ocurre un error al intentar conectar a la base de datos.
     */
    public synchronized Connection connect() throws SQLException {
        // Verifica si la conexión ya existe y si no está cerrada.
        if (this.connection == null || this.connection.isClosed()) {
            try {
                // Intenta establecer la conexión utilizando la cadena de conexión.
                this.connection = DriverManager.getConnection(STR_CONNECTION);
            } catch (SQLException exception) {
                // Si ocurre un error durante la conexión, se lanza una excepción SQLException
                // con un mensaje más descriptivo que incluye el mensaje original de la excepción.
                throw new SQLException("Error al conectar a la base de datos: " + exception.getMessage(), exception);
            }
        }
        // Retorna la conexión (ya sea la existente o la recién creada).
        return this.connection;
    }

    /**
     * Este método se encarga de cerrar la conexión a la base de datos.
     * También lanza una SQLException si ocurre un error al intentar cerrar la conexión.
     *
     * @throws SQLException Si ocurre un error al intentar cerrar la conexión.
     */
    public void disconnect() throws SQLException {
        // Verifica si la conexión existe (no es nula).
        if (this.connection != null) {
            try {
                // Intenta cerrar la conexión.
                this.connection.close();
            } catch (SQLException exception) {
                // Si ocurre un error al cerrar la conexión, se lanza una excepción SQLException
                // con un mensaje más descriptivo.
                throw new SQLException("Error al cerrar la conexión: " + exception.getMessage(), exception);
            } finally {
                // El bloque finally se ejecuta siempre, independientemente de si hubo una excepción o no.
                // Aquí se asegura que la referencia a la conexión se establezca a null,
                // indicando que ya no hay una conexión activa gestionada por esta instancia.
                this.connection = null;
            }
        }
    }

    /**
     * Este método estático y sincronizado (`synchronized`) implementa el patrón Singleton.
     * Devuelve la única instancia de JDBCConnectionManager. Si la instancia aún no existe,
     * la crea antes de devolverla. La sincronización asegura que la creación de la instancia
     * sea segura en entornos multihilo (que varios hilos no intenten crear la instancia al mismo tiempo).
     *
     * @return La única instancia de JDBCConnectionManager.
     */
    public static synchronized ConnectionManager getInstance() {
        // Verifica si la instancia ya ha sido creada.
        if (instance == null) {
            // Si no existe, crea una nueva instancia de JDBCConnectionManager.
            instance = new ConnectionManager();
        }
        // Retorna la instancia existente (o la recién creada).
        return instance;
    }
}