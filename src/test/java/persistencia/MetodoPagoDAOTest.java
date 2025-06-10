package persistencia;

import dominio.MetodoPago;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MetodoPagoDAOTest {

    private MetodoPagoDAO metodoDAO;

    @BeforeEach
    void setUp() {
        metodoDAO = new MetodoPagoDAO();
    }

    private MetodoPago create(MetodoPago metodo) throws SQLException {
        MetodoPago res = metodoDAO.create(metodo);
        assertNotNull(res, "El método de pago creado no debe ser nulo.");
        assertEquals(metodo.getNombreMetodo(), res.getNombreMetodo());
        return res;
    }

    private void update(MetodoPago metodo) throws SQLException {
        metodo.setNombreMetodo(metodo.getNombreMetodo() + "_edit");
        boolean res = metodoDAO.update(metodo);
        assertTrue(res, "La actualización debe ser exitosa.");
        getById(metodo);
    }

    private void getById(MetodoPago metodo) throws SQLException {
        MetodoPago res = metodoDAO.getById(metodo.getMetodoPagoId());
        assertNotNull(res, "El método de pago obtenido no debe ser nulo.");
        assertEquals(metodo.getMetodoPagoId(), res.getMetodoPagoId());
        assertEquals(metodo.getNombreMetodo(), res.getNombreMetodo());
    }

    private void search(MetodoPago metodo) throws SQLException {
        ArrayList<MetodoPago> lista = metodoDAO.search(metodo.getNombreMetodo());
        boolean encontrado = lista.stream()
                .anyMatch(mp -> mp.getNombreMetodo().contains(metodo.getNombreMetodo()));
        assertTrue(encontrado, "No se encontró el método de pago por nombre.");
    }
    private void getAll() throws SQLException {
        ArrayList<MetodoPago> lista = metodoDAO.getAll();
        assertFalse(lista.isEmpty(), "La lista de métodos de pago no debe estar vacía.");
        for (MetodoPago mp : lista) {
            assertNotNull(mp.getNombreMetodo(), "El nombre del método de pago no debe ser nulo.");
        }
    }

    private void delete(MetodoPago metodo) throws SQLException {
        boolean eliminado = metodoDAO.delete(metodo.getMetodoPagoId());
        assertTrue(eliminado, "La eliminación debe ser exitosa.");
        MetodoPago eliminadoCheck = metodoDAO.getById(metodo.getMetodoPagoId());
        assertNull(eliminadoCheck, "El método de pago ya no debería existir.");
    }

    private void authenticate(MetodoPago metodo, String nombreMetodoEsperado) throws SQLException {
        MetodoPago res = metodoDAO.authenticate(nombreMetodoEsperado);
        assertNotNull(res, "La autenticación debe retornar un método válido.");
        assertEquals(nombreMetodoEsperado, res.getNombreMetodo());
    }

    private void authenticationFails(String nombreInvalido) throws SQLException {
        MetodoPago res = metodoDAO.authenticate(nombreInvalido);
        assertNull(res, "La autenticación debe fallar con nombre incorrecto.");
    }

    private void updatePassword(MetodoPago metodo, String nuevoNombre) throws SQLException {
        metodo.setNombreMetodo(nuevoNombre);
        boolean actualizado = metodoDAO.update(metodo);
        assertTrue(actualizado, "La actualización del nombre debe ser exitosa.");
        authenticate(metodo, nuevoNombre);
    }

    @Test
    void testMetodoPagoDAO() throws SQLException {
        String nombreMetodo = "TarjetaTest_" + new Random().nextInt(1000);
        MetodoPago metodo = new MetodoPago(0, nombreMetodo);

        MetodoPago creado = create(metodo);
        update(creado);
        search(creado);

        authenticate(creado, creado.getNombreMetodo());
        authenticationFails("NombreInvalido123");

        updatePassword(creado, "Actualizado_" + creado.getNombreMetodo());

        delete(creado);
    }

    @Test
    void createMetodoPago() throws SQLException {
        MetodoPago metodo = new MetodoPago(0, "PagoTemporal");
        MetodoPago creado = metodoDAO.create(metodo);
        assertNotNull(creado);
        metodoDAO.delete(creado.getMetodoPagoId()); // limpieza
    }
}