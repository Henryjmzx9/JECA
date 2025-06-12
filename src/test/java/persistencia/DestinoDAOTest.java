package persistencia;

import dominio.Destino;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DestinoDAOTest {

    private DestinoDAO destinoDAO;

    @BeforeEach
    void setUp() {
        destinoDAO = new DestinoDAO();
    }

    private Destino create(Destino destino) throws SQLException {
        Destino res = destinoDAO.create(destino);
        assertNotNull(res, "El destino creado no debe ser nulo.");
        assertEquals(destino.getNombre(), res.getNombre());
        assertEquals(destino.getPais(), res.getPais());
        assertEquals(destino.getDescripcion(), res.getDescripcion());
        return res;
    }

    private void update(Destino destino) throws SQLException {
        destino.setNombre(destino.getNombre() + "_upd");
        destino.setPais(destino.getPais() + "_upd");
        destino.setDescripcion("actualizado");

        boolean res = destinoDAO.update(destino);
        assertTrue(res, "La actualización debe ser exitosa.");
        getById(destino);
    }

    private void getById(Destino destino) throws SQLException {
        Destino res = destinoDAO.getById(destino.getDestinoId());
        assertNotNull(res, "El destino no debe ser nulo.");
        assertEquals(destino.getNombre(), res.getNombre());
        assertEquals(destino.getPais(), res.getPais());
        assertEquals(destino.getDescripcion(), res.getDescripcion());
    }

    private void search(Destino destino) throws SQLException {
        ArrayList<Destino> destinos = destinoDAO.search(destino.getNombre());
        boolean found = destinos.stream().anyMatch(d -> d.getNombre().contains(destino.getNombre()));
        assertTrue(found, "El nombre buscado no fue encontrado: " + destino.getNombre());
    }

    private void delete(Destino destino) throws SQLException {
        boolean res = destinoDAO.delete(destino.getDestinoId());
        assertTrue(res, "La eliminación debe ser exitosa.");

        Destino res2 = destinoDAO.getById(destino.getDestinoId());
        assertNull(res2, "El destino debería estar eliminado.");
    }



    @Test
    void testDestinoDAO() throws SQLException {
        Random random = new Random();
        String nombre = "Destino" + random.nextInt(10000);
        String pais = "Pais" + random.nextInt(1000);
        String descripcion = "Un lugar interesante";
        byte[] imagen = new byte[]{1, 2, 3}; // Dummy data

        Destino destino = new Destino(0, nombre, pais, descripcion, imagen);

        Destino testDestino = create(destino);
        update(testDestino);
        search(testDestino);
        String nuevoPais = "PaisActualizado";

        delete(testDestino);
    }

    @Test
    void createDestino() throws SQLException {
        Destino destino = new Destino(0, "NombreTest", "PaisTest", "Descripcion", new byte[]{10, 20, 30});
        Destino res = destinoDAO.create(destino);
        assertNotNull(res, "El destino creado debe existir.");
        destinoDAO.delete(res.getDestinoId()); // limpieza
    }
}