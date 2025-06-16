package persistencia;

import dominio.Pago;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PagoDAOTest {

    private PagoDAO pagoDAO;
    private Pago pagoTest;

    @BeforeEach
    void setUp() {
        pagoDAO = new PagoDAO();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (pagoDAO != null) {
            ArrayList<Pago> pagos = pagoDAO.getAll();
            for (Pago p : pagos) {
                if (p.getReservaId() == 6) { // limpiar todos los pagos de prueba con reservaId 6
                    pagoDAO.delete(p.getPagoId());
                }
            }
        }
    }

    private Pago createPago() throws SQLException {
        Pago pago = new Pago();
        pago.setReservaId(6);               // reserva existente
        pago.setMonto(150.00);
        pago.setMetodoPagoId(1);            // método de pago existente
        pago.setFechaPago(new Date());

        Pago creado = pagoDAO.create(pago);
        assertNotNull(creado, "El pago creado no debe ser nulo.");
        assertEquals(pago.getReservaId(), creado.getReservaId());
        pagoTest = creado;
        return creado;
    }

    private void updatePago(Pago pago) throws SQLException {
        pago.setMonto(250.50);
        boolean actualizado = pagoDAO.update(pago);
        assertTrue(actualizado, "El pago debe actualizarse correctamente.");

        Pago actualizadoPago = pagoDAO.getById(pago.getPagoId());
        assertEquals(250.50, actualizadoPago.getMonto());
    }

    private void searchPago(Pago pago) throws SQLException {
        Pago encontrado = pagoDAO.getById(pago.getPagoId());
        assertNotNull(encontrado, "El pago encontrado no debe ser nulo.");
        assertEquals(pago.getMonto(), encontrado.getMonto());
    }

    private void getByIdPago(Pago pago) throws SQLException {
        Pago encontrado = pagoDAO.getById(pago.getPagoId());
        assertNotNull(encontrado, "El pago obtenido no debe ser nulo.");
        assertEquals(pago.getReservaId(), encontrado.getReservaId());
    }

    private void getAllPagos() throws SQLException {
        ArrayList<Pago> pagos = pagoDAO.getAll();
        assertNotNull(pagos, "La lista de pagos no debe ser nula.");
        assertFalse(pagos.isEmpty(), "Debe haber al menos un pago.");
    }

    private void searchByReservaId() throws SQLException {
        ArrayList<Pago> pagos = pagoDAO.getByReservaId(6);
        assertNotNull(pagos, "La búsqueda por reservaId no debe ser nula.");
        assertFalse(pagos.isEmpty(), "Debe haber pagos para la reservaId=6.");
    }
    private void searchByMetodoPagoId() throws SQLException {
        ArrayList<Pago> pagos = pagoDAO.searchByMetodoPagoId(1);
        assertNotNull(pagos, "La búsqueda por metodoPagoId no debe ser nula.");
        assertFalse(pagos.isEmpty(), "Debe haber pagos para el metodoPagoId=1.");
    }
    private void deletePago(Pago pago) throws SQLException {
        boolean eliminado = pagoDAO.delete(pago.getPagoId());
        assertTrue(eliminado, "El pago debe eliminarse correctamente.");

        Pago eliminadoPago = pagoDAO.getById(pago.getPagoId());
        assertNull(eliminadoPago, "El pago eliminado no debe existir.");
    }

    @Test
    void testPagoDAO() throws SQLException {
        Pago pago = createPago();
        updatePago(pago);
        searchPago(pago);
        getByIdPago(pago);
        getAllPagos();
        searchByReservaId();
        searchByMetodoPagoId();
        deletePago(pago);
    }
}
