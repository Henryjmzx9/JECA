package persistencia;

import dominio.Destino;
import dominio.Paquete;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PaqueteDAOTest {

    private DestinoDAO destinoDAO;
    private PaqueteDAO paqueteDAO;
    private Destino destinoTest;

    @BeforeEach
    void setUp() throws SQLException {
        destinoDAO = new DestinoDAO();
        paqueteDAO = new PaqueteDAO();

        // Crear un destino de prueba
        Random random = new Random();
        int num = random.nextInt(1000) + 1;

        Destino destino = new Destino();
        destino.setNombre("Destino Test " + num);
        destino.setPais("El Salvador");
        destino.setDescripcion("Un destino de prueba.");
        destino.setImagen(null); // o una imagen en bytes si deseas

        destinoTest = destinoDAO.create(destino);
        assertNotNull(destinoTest, "El destino de prueba no se pudo crear.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Eliminar paquete si existe
        Paquete paquete = paqueteDAO.getByNombre("Paquete Test");
        if (paquete != null) {
            paqueteDAO.delete(paquete.getPaqueteId());
        }

        // Eliminar destino
        destinoDAO.delete(destinoTest.getDestinoId());
    }

    private Paquete createPaquete() throws SQLException {
        Paquete paquete = new Paquete();
        paquete.setNombre("Paquete Test");
        paquete.setDescripcion("Incluye todo.");
        paquete.setPrecio(250.0);
        paquete.setDestinoId(destinoTest.getDestinoId());

        // Setear fechas válidas
        paquete.setFechaInicio(LocalDate.now());
        paquete.setFechaFin(LocalDate.now().plusDays(5));

        Paquete creado = paqueteDAO.create(paquete);
        assertNotNull(creado, "El paquete creado no debe ser nulo.");
        assertEquals(paquete.getNombre(), creado.getNombre());
        return creado;
    }


    private void updatePaquete(Paquete paquete) throws SQLException {
        paquete.setDescripcion("Actualizado!");
        paquete.setPrecio(300.0);

        boolean updated = paqueteDAO.update(paquete);
        assertTrue(updated, "La actualización del paquete debe ser exitosa.");

        getById(paquete);
    }

    private void getById(Paquete paquete) throws SQLException {
        Paquete obtenido = paqueteDAO.getById(paquete.getPaqueteId());
        assertNotNull(obtenido, "El paquete obtenido no debe ser nulo.");
        assertEquals(paquete.getDescripcion(), obtenido.getDescripcion());
    }

    private void searchPaquete(Paquete paquete) throws SQLException {
        // Cambiar search a searchPaquete para que coincida con la firma
        ArrayList<Paquete> paquetes = paqueteDAO.searchPaquete(paquete.getNombre());
        assertTrue(paquetes.stream().anyMatch(p -> p.getNombre().equals(paquete.getNombre())),
                "El paquete no fue encontrado por búsqueda.");
    }


    private void deletePaquete(Paquete paquete) throws SQLException {
        boolean eliminado = paqueteDAO.delete(paquete.getPaqueteId());
        assertTrue(eliminado, "El paquete debe ser eliminado.");

        Paquete resultado = paqueteDAO.getById(paquete.getPaqueteId());
        assertNull(resultado, "El paquete eliminado no debe existir.");
    }

    private void getAllPaquetes() {
        try {
            ArrayList<Paquete> paquetes = paqueteDAO.getAll();
            assertNotNull(paquetes, "La lista de paquetes no debe ser nula.");
            assertFalse(paquetes.isEmpty(), "La lista de paquetes no debe estar vacía.");
        } catch (SQLException e) {
            fail("Error al obtener todos los paquetes: " + e.getMessage());
        }
    }

    @Test
    void testPaqueteDAO() throws SQLException {
        Paquete paquete = createPaquete();
        updatePaquete(paquete);
        getById(paquete);
        searchPaquete(paquete);
        getAllPaquetes();
        deletePaquete(paquete);
    }

    @Test
    void createPaquete2() throws SQLException {
        // Crear otro paquete con valores distintos
        Paquete paquete = new Paquete();
        paquete.setNombre("Paquete Test 2");
        paquete.setDescripcion("Otro paquete de prueba.");
        paquete.setPrecio(199.99);
        paquete.setDestino(destinoTest);

        Paquete paqueteCreado = paqueteDAO.create(paquete);
        assertNotNull(paqueteCreado, "El paquete creado debe existir.");

        // Limpieza
        paqueteDAO.delete(paqueteCreado.getPaqueteId());
    }
}
