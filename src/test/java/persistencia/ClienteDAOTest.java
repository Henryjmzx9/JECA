package persistencia;

import dominio.Cliente;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDAOTest {

    private ClienteDAO clienteDAO;

    @BeforeEach
    void setUp() {
        clienteDAO = new ClienteDAO();
    }

    private Cliente create(Cliente cliente) throws SQLException {
        Cliente res = clienteDAO.create(cliente);
        assertNotNull(res, "El cliente creado no debe ser nulo.");
        assertEquals(cliente.getUserId(), res.getUserId());
        assertEquals(cliente.getTelefono(), res.getTelefono());
        assertEquals(cliente.getDireccion(), res.getDireccion());
        return res;
    }

    private void update(Cliente cliente) throws SQLException {
        cliente.setTelefono(cliente.getTelefono() + "_upd");
        cliente.setDireccion("upd_" + cliente.getDireccion());

        boolean res = clienteDAO.update(cliente);
        assertTrue(res, "La actualización debe ser exitosa.");

        getById(cliente);
    }

    private void getById(Cliente cliente) throws SQLException {
        Cliente res = clienteDAO.getById(cliente.getClienteId());
        assertNotNull(res, "El cliente obtenido no debe ser nulo.");
        assertEquals(cliente.getClienteId(), res.getClienteId());
        assertEquals(cliente.getUserId(), res.getUserId());
        assertEquals(cliente.getTelefono(), res.getTelefono());
        assertEquals(cliente.getDireccion(), res.getDireccion());
    }

    private void search(Cliente cliente) throws SQLException {
        ArrayList<Cliente> clientes = clienteDAO.search(cliente.getTelefono());
        boolean found = clientes.stream()
                .anyMatch(item -> item.getTelefono().contains(cliente.getTelefono()));

        assertTrue(found, "El teléfono buscado no fue encontrado: " + cliente.getTelefono());
    }

    private void delete(Cliente cliente) throws SQLException {
        boolean res = clienteDAO.delete(cliente.getClienteId());
        assertTrue(res, "La eliminación debe ser exitosa.");

        Cliente res2 = clienteDAO.getById(cliente.getClienteId());
        assertNull(res2, "El cliente debería estar eliminado.");
    }

    private void authenticate(Cliente cliente, String telefono) throws SQLException {
        Cliente res = clienteDAO.authenticate(cliente.getUserId(), telefono);
        assertNotNull(res, "La autenticación debe retornar un cliente válido.");
        assertEquals(cliente.getTelefono(), res.getTelefono());
    }

    private void authenticationFails(int userId, String telefono) throws SQLException {
        Cliente res = clienteDAO.authenticate(userId, telefono);
        assertNull(res, "La autenticación debería fallar con credenciales inválidas.");
    }

    private void updatePassword(Cliente cliente, String newTelefono) throws SQLException {
        cliente.setTelefono(newTelefono);
        boolean res = clienteDAO.update(cliente);
        assertTrue(res, "La actualización del teléfono debe ser exitosa.");
        authenticate(cliente, newTelefono);
    }

    @Test
    void testClienteDAO() throws SQLException {
        Random random = new Random();
        int userId = random.nextInt(1000) + 1;
        String telefono = "555-" + random.nextInt(1000);
        String direccion = "Calle Falsa " + random.nextInt(100);

        Cliente cliente = new Cliente(0, userId, telefono, direccion);

        Cliente testCliente = create(cliente);
        update(testCliente);
        search(testCliente);

        authenticate(testCliente, telefono);
        authenticationFails(userId, "telefonoIncorrecto");

        String newTelefono = "555-9999";
        updatePassword(testCliente, newTelefono);

        delete(testCliente);
    }

    @Test
    void createCliente() throws SQLException {
        Cliente cliente = new Cliente(0, 1, "555-1234", "Avenida Siempre Viva");
        Cliente res = clienteDAO.create(cliente);
        assertNotNull(res, "El cliente creado debe existir.");
        clienteDAO.delete(res.getClienteId()); // limpieza
    }
}