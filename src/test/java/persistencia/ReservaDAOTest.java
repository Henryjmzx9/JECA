package persistencia;

import dominio.Cliente;
import dominio.Paquete;
import dominio.Reserva;
import utils.EstadoReserva;
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

    private Cliente clienteTest;
    private Paquete paqueteTest;

    @BeforeEach
    void setUp() throws SQLException {
        clienteDAO = new ClienteDAO();
        paqueteDAO = new PaqueteDAO();
        reservaDAO = new ReservaDAO();

        // Obtener el userId del usuario existente
        int existingUserId = UsuarioDAO.getIdByEmail("carlos.mendoza123@gmail.com"); // Método para obtener userId por email
        assertTrue(existingUserId > 0, "El usuario Carlos Mendoza no existe en la base de datos.");

        // Crear cliente de prueba
        Random random = new Random();
        int num = random.nextInt(1000) + 1;

        Cliente cliente = new Cliente();
        cliente.setUserId(existingUserId); // Asociar el userId existente
        cliente.setTelefono("12345678");
        cliente.setDireccion("Dirección Test " + num);

        clienteTest = clienteDAO.create(cliente);
        assertNotNull(clienteTest, "No se pudo crear el cliente de prueba.");

        // Crear paquete de prueba (recuerda setear fechas)
        Paquete paquete = new Paquete();
        paquete.setNombre("Paquete Test " + num);
        paquete.setDescripcion("Incluye todo.");
        paquete.setPrecio(299.99);
        paquete.setFechaInicio(LocalDate.now());
        paquete.setFechaFin(LocalDate.now().plusDays(5));
        paquete.setDestinoId(8); // Cambia según tus datos disponibles

        paqueteTest = paqueteDAO.create(paquete);
        assertNotNull(paqueteTest, "No se pudo crear el paquete de prueba.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Manejar eliminación de datos incluso si las pruebas fallan
        if (reservaDAO != null) {
            ArrayList<Reserva> reservas = reservaDAO.getAll();
            for (Reserva r : reservas) {
                if (r.getCliente().getClienteId() == clienteTest.getClienteId() ||
                        r.getPaquete().getPaqueteId() == paqueteTest.getPaqueteId()) {
                    reservaDAO.delete(r.getReservaId());
                }
            }
        }

        if (paqueteDAO != null && paqueteTest != null) {
            paqueteDAO.delete(paqueteTest.getPaqueteId());
        }

        if (clienteDAO != null && clienteTest != null) {
            clienteDAO.delete(clienteTest.getClienteId());
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
        getByIdReserva(reserva);
        getAllReservas();
        deleteReserva(reserva);
    }
}