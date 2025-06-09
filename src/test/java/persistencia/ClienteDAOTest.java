package persistencia;

import dominio.Cliente;
import dominio.Usuario;
import org.junit.jupiter.api.*;
import utils.Rol;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDAOTest {

    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioTest;

    @BeforeEach
    void setUp() throws SQLException {
        clienteDAO = new ClienteDAO();
        usuarioDAO = new UsuarioDAO();

        // Crear un usuario con rol Cliente
        Random random = new Random();
        int num = random.nextInt(1000) + 1;
        String email = "cliente" + num + "@test.com";

        Usuario usuario = new Usuario(0, "Cliente Test", "clave123", email, (byte) 1, Rol.Cliente);
        usuarioTest = usuarioDAO.create(usuario);
        assertNotNull(usuarioTest, "No se pudo crear el usuario para el cliente.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Eliminar cliente si existe
        Cliente cliente = clienteDAO.getByUserId(usuarioTest.getId());
        if (cliente != null) {
            clienteDAO.delete(cliente.getClienteId());
        }

        // Eliminar usuario
        usuarioDAO.delete(usuarioTest.getId());
    }

    private Cliente createCliente() throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setUserId(usuarioTest.getId());
        cliente.setTelefono("7777-8888");
        cliente.setDireccion("Col. Test");

        Cliente creado = clienteDAO.create(cliente);
        assertNotNull(creado, "El cliente creado no debe ser nulo.");
        assertEquals(cliente.getTelefono(), creado.getTelefono());
        return creado;
    }

    private void updateCliente(Cliente cliente) throws SQLException {
        cliente.setTelefono("9999-0000");
        cliente.setDireccion("Col. Nueva");

        boolean updated = clienteDAO.update(cliente);
        assertTrue(updated, "La actualización del cliente debe ser exitosa.");

        getById(cliente);
    }

    private void getById(Cliente cliente) throws SQLException {
        Cliente obtenido = clienteDAO.getById(cliente.getClienteId());
        assertNotNull(obtenido, "El cliente obtenido no debe ser nulo.");
        assertEquals(cliente.getTelefono(), obtenido.getTelefono());
    }

    private void searchCliente(Cliente cliente) throws SQLException {
        ArrayList<Cliente> clientes = clienteDAO.searchCliente(cliente.getTelefono());
        assertTrue(clientes.stream().anyMatch(c -> c.getTelefono().equals(cliente.getTelefono())),
                "El cliente no fue encontrado por búsqueda.");
    }

    private void deleteCliente(Cliente cliente) throws SQLException {
        boolean eliminado = clienteDAO.delete(cliente.getClienteId());
        assertTrue(eliminado, "El cliente debe ser eliminado.");

        Cliente resultado = clienteDAO.getById(cliente.getClienteId());
        assertNull(resultado, "El cliente eliminado no debe existir.");
    }

    private void getAllClientes() {
        try {
            ArrayList<Cliente> clientes = clienteDAO.getAll();
            assertNotNull(clientes, "La lista de clientes no debe ser nula.");
            assertFalse(clientes.isEmpty(), "La lista de clientes no debe estar vacía.");
        } catch (SQLException e) {
            fail("Error al obtener todos los clientes: " + e.getMessage());
        }
    }

    @Test
    void testClienteDAO() throws SQLException {
        Cliente cliente = createCliente();
        updateCliente(cliente);
        getById(cliente);
        searchCliente(cliente);
        getAllClientes();
        deleteCliente(cliente);
    }

    @Test
    void createCliente2() throws SQLException {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario(0, "Usuario Test", "password123", "test@example.com", (byte) 1, Rol.Cliente);
        Usuario usuarioCreado = usuarioDAO.create(usuario);
        assertNotNull(usuarioCreado, "El usuario de prueba no se pudo crear.");

        // Crear un cliente relacionado con el usuario
        Cliente cliente = new Cliente();
        cliente.setUserId(usuarioCreado.getId());
        cliente.setTelefono("8888-9999");
        cliente.setDireccion("Test Dir");

        Cliente clienteCreado = clienteDAO.create(cliente);
        assertNotNull(clienteCreado, "El cliente creado debe existir.");

        // Limpieza: Eliminar cliente y usuario
        clienteDAO.delete(clienteCreado.getClienteId());
        usuarioDAO.delete(usuarioCreado.getId());
    }
}
