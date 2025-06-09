package persistencia;

import dominio.Paquete;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class PaqueteDAOTest {

    private PaqueteDAO paqueteDAO;

    @BeforeEach
    void setUp() {
        paqueteDAO = new PaqueteDAO();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Eliminar todos los paquetes creados en las pruebas para limpiar la base de datos.
        ArrayList<Paquete> paquetes = paqueteDAO.getAll();
        for (Paquete paquete : paquetes) {
            paqueteDAO.delete(paquete.getPaqueteId());
        }
    }

    private Paquete createPaquete() throws SQLException {
        Paquete paquete = new Paquete();
        paquete.setNombre("Paquete Test");
        paquete.setDescripcion("Descripción del paquete de prueba.");
        paquete.setPrecio(199.99);
        paquete.setDuracionDias(7);
        paquete.setFechaInicio(LocalDate.now());
        paquete.setFechaFin(LocalDate.now().plusDays(7));
        paquete.setDestinoId(1); // Asegúrate de que este destino exista en la base de datos.

        Paquete creado = paqueteDAO.create(paquete);
        assertNotNull(creado, "El paquete creado no debe ser nulo.");
        assertEquals(paquete.getNombre(), creado.getNombre());
        return creado;
    }

    private void updatePaquete(Paquete paquete) throws SQLException {
        paquete.setNombre("Paquete Test Actualizado");
        paquete.setDescripcion("Descripción actualizada.");
        paquete.setPrecio(299.99);
        paquete.setDuracionDias(10);

        boolean updated = paqueteDAO.update(paquete);
        assertTrue(updated, "La actualización del paquete debe ser exitosa.");

        getById(paquete);
    }

    private void getById(Paquete paquete) throws SQLException {
        Paquete obtenido = paqueteDAO.getById(paquete.getPaqueteId());
        assertNotNull(obtenido, "El paquete obtenido no debe ser nulo.");
        assertEquals(paquete.getNombre(), obtenido.getNombre());
        assertEquals(paquete.getDescripcion(), obtenido.getDescripcion());
    }

    private void getAllPaquetes() {
        try {
            ArrayList<Paquete> paquetes = paqueteDAO.getAll();
            assertNotNull(paquetes, "La lista de paquetes no debe ser nula.");
        } catch (SQLException e) {
            fail("Error al obtener todos los paquetes: " + e.getMessage());
        }
    }

    private void deletePaquete(Paquete paquete) throws SQLException {
        boolean eliminado = paqueteDAO.delete(paquete.getPaqueteId());
        assertTrue(eliminado, "El paquete debe ser eliminado.");

        Paquete resultado = paqueteDAO.getById(paquete.getPaqueteId());
        assertNull(resultado, "El paquete eliminado no debe existir.");
    }

    @Test
    void testPaqueteDAO() throws SQLException {
        Paquete paquete = createPaquete();
        updatePaquete(paquete);
        getById(paquete);
        getAllPaquetes();
        deletePaquete(paquete);
    }

    @Test
    void createPaquete2() throws SQLException {
        Paquete paquete = new Paquete();
        paquete.setNombre("Paquete Extra");
        paquete.setDescripcion("Descripción de prueba extra.");
        paquete.setPrecio(99.99);
        paquete.setDuracionDias(3);
        paquete.setFechaInicio(LocalDate.now());
        paquete.setFechaFin(LocalDate.now().plusDays(3));
        paquete.setDestinoId(1); // Asegúrate de que este destino exista en la base de datos.

        Paquete creado = paqueteDAO.create(paquete);
        assertNotNull(creado, "El paquete creado debe existir.");

        // Limpieza: Eliminar paquete
        paqueteDAO.delete(creado.getPaqueteId());
    }
}
