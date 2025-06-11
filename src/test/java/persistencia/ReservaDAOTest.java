package persistencia;

import dominio.Cliente;
import dominio.Paquete;
import dominio.Reserva;
import dominio.Usuario;
import utils.EstadoReserva;
import utils.Rol;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;


import static org.junit.jupiter.api.Assertions.*;

class ReservaDAOTest {

    private ClienteDAO clienteDAO;
    private PaqueteDAO paqueteDAO;
    private ReservaDAO reservaDAO;
    private UsuarioDAO usuarioDAO;

    private Cliente clienteTest;
    private Paquete paqueteTest;
    private Usuario usuarioTest;

    @BeforeEach
    void setUp() throws SQLException {
        clienteDAO = new ClienteDAO();
        paqueteDAO = new PaqueteDAO();
        reservaDAO = new ReservaDAO();
        usuarioDAO = new UsuarioDAO();

        // Crear usuario de prueba único para esta reserva
        Random random = new Random();
        int num = random.nextInt(1000) + 1;
        String email = "reservacliente" + num + "@test.com";

        Usuario usuario = new Usuario(0, "Cliente Reserva", "clave123", email, (byte) 1, Rol.Cliente);
        usuarioTest = usuarioDAO.create(usuario);
        assertNotNull(usuarioTest, "No se pudo crear el usuario de prueba.");

        // Crear cliente asociado
        Cliente cliente = new Cliente();
        cliente.setUserId(usuarioTest.getId());
        cliente.setTelefono("12345678");
        cliente.setDireccion("Dirección Test " + num);

        clienteTest = clienteDAO.create(cliente);
        assertNotNull(clienteTest, "No se pudo crear el cliente de prueba.");

        // Crear paquete de prueba
        Paquete paquete = new Paquete();
        paquete.setNombre("Paquete Test " + num);
        paquete.setDescripcion("Incluye todo.");
        paquete.setPrecio(299.99);
        paquete.setFechaInicio(LocalDate.now());
        paquete.setFechaFin(LocalDate.now().plusDays(5));
        paquete.setDestinoId(8); // Ajustar según tus datos válidos

        paqueteTest = paqueteDAO.create(paquete);
        assertNotNull(paqueteTest, "No se pudo crear el paquete de prueba.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Eliminar reservas asociadas
        if (reservaDAO != null) {
            ArrayList<Reserva> reservas = reservaDAO.getAll();
            for (Reserva r : reservas) {
                if (r.getCliente().getClienteId() == clienteTest.getClienteId() ||
                        r.getPaquete().getPaqueteId() == paqueteTest.getPaqueteId()) {
                    reservaDAO.delete(r.getReservaId());
                }
            }
        }

        // Eliminar paquete
        if (paqueteDAO != null && paqueteTest != null) {
            paqueteDAO.delete(paqueteTest.getPaqueteId());
        }

        // Eliminar cliente
        if (clienteDAO != null && clienteTest != null) {
            clienteDAO.delete(clienteTest.getClienteId());
        }

        // Eliminar usuario
        if (usuarioDAO != null && usuarioTest != null) {
            usuarioDAO.delete(usuarioTest.getId());
        }
    }

    private Reserva createReserva() throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setCliente(clienteTest);
        reserva.setPaquete(paqueteTest);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Reserva creada = reservaDAO.create(reserva);
        assertNotNull(creada, "La reserva creada debe existir.");
        assertEquals(reserva.getEstado(), creada.getEstado());
        return creada;
    }

    private void updateReserva(Reserva reserva) throws SQLException {
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        boolean updated = reservaDAO.update(reserva);
        assertTrue(updated, "La actualización debe ser exitosa.");

        Reserva actualizada = reservaDAO.getById(reserva.getReservaId());
        assertEquals(EstadoReserva.CONFIRMADA, actualizada.getEstado());
    }

    private void searchReserva(Reserva reserva) throws SQLException {
        Reserva encontrada = reservaDAO.getById(reserva.getReservaId());
        assertNotNull(encontrada, "La reserva encontrada no debe ser nula.");
        assertEquals(reserva.getEstado(), encontrada.getEstado());
    }

    private void deleteReserva(Reserva reserva) throws SQLException {
        boolean deleted = reservaDAO.delete(reserva.getReservaId());
        assertTrue(deleted, "La reserva debe eliminarse correctamente.");

        Reserva resultado = reservaDAO.getById(reserva.getReservaId());
        assertNull(resultado, "La reserva eliminada no debe existir.");
    }

    private void getByIdReserva(Reserva reserva) throws SQLException {
        Reserva encontrada = reservaDAO.getById(reserva.getReservaId());
        assertNotNull(encontrada, "La reserva obtenida no debe ser nula.");
        assertEquals(reserva.getEstado(), encontrada.getEstado());
    }

    private void getAllReservas() throws SQLException {
        ArrayList<Reserva> reservas = reservaDAO.getAll();
        assertNotNull(reservas, "La lista de reservas no debe ser nula.");
        assertFalse(reservas.isEmpty(), "Debe haber al menos una reserva.");
    }

    @Test
    void testReservaDAO() throws SQLException {
        Reserva reserva = createReserva();
        updateReserva(reserva);
        searchReserva(reserva);
        getByIdReserva(reserva);
        getAllReservas();
        deleteReserva(reserva);
    }
}