package persistencia;

import dominio.Usuario;
import org.junit.jupiter.api.*;
import utils.Rol;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDAOTest {

    private UsuarioDAO usuarioDAO;

    @BeforeEach
    void setUp() {
        usuarioDAO = new UsuarioDAO();
    }

    private Usuario create(Usuario usuario) throws SQLException {
        Usuario res = usuarioDAO.create(usuario);

        assertNotNull(res, "El usuario creado no debe ser nulo.");
        assertEquals(usuario.getName(), res.getName());
        assertEquals(usuario.getEmail(), res.getEmail());
        assertEquals(usuario.getStatus(), res.getStatus());
        return res;
    }

    private void update(Usuario usuario) throws SQLException {
        usuario.setName(usuario.getName() + "_upd");
        usuario.setEmail("upd_" + usuario.getEmail());
        usuario.setStatus((byte) 1);

        boolean res = usuarioDAO.update(usuario);
        assertTrue(res, "La actualización debe ser exitosa.");

        getById(usuario);
    }

    private void getById(Usuario usuario) throws SQLException {
        Usuario res = usuarioDAO.getById(usuario.getId());
        assertNotNull(res, "El usuario obtenido no debe ser nulo.");
        assertEquals(usuario.getId(), res.getId());
        assertEquals(usuario.getName(), res.getName());
        assertEquals(usuario.getEmail(), res.getEmail());
        assertEquals(usuario.getStatus(), res.getStatus());
    }

    private void search(Usuario usuario) throws SQLException {
        ArrayList<Usuario> usuarios = usuarioDAO.search(usuario.getName());
        boolean found = false;

        for (Usuario item : usuarios) {
            if (item.getName().contains(usuario.getName())) {
                found = true;
            } else {
                found = false;
                break;
            }
        }

        assertTrue(found, "El nombre buscado no fue encontrado: " + usuario.getName());
    }

    private void authenticate(Usuario usuario, String password) throws SQLException {
        Usuario res = usuarioDAO.authenticate(usuario.getEmail(), password);
        assertNotNull(res, "La autenticación debe retornar un usuario válido.");
        assertEquals(usuario.getEmail(), res.getEmail());
        assertEquals(res.getStatus(), 1);
    }

    private void authenticateFail(String email, String password) throws SQLException {
        Usuario res = usuarioDAO.authenticate(email, password);
        assertNull(res, "La autenticación debería fallar con credenciales inválidas.");
    }

    private void updatePassword(Usuario usuario, String newPassword) throws SQLException {
        usuario.setPasswordHash(newPassword);
        boolean res = usuarioDAO.updatePassword(usuario);
        assertTrue(res, "La actualización de contraseña debe ser exitosa.");
        authenticate(usuario, newPassword);
    }

    private void delete(Usuario usuario) throws SQLException {
        boolean res = usuarioDAO.delete(usuario.getId());
        assertTrue(res, "La eliminación debe ser exitosa.");

        Usuario res2 = usuarioDAO.getById(usuario.getId());
        assertNull(res2, "El usuario debería estar eliminado.");
    }

    @Test
    void testUsuarioDAO() throws SQLException {
        Random random = new Random();
        int num = random.nextInt(1000) + 1;
        String email = "henry" + num + "@test.com";

        Usuario usuario = new Usuario(0, "Henry Test", "clave123", email, (byte) 1, Rol.ADMINISTRADOR);

        Usuario testUser = create(usuario);
        update(testUser);
        search(testUser);

        testUser.setPasswordHash("clave123");
        authenticate(testUser, "clave123");

        authenticateFail("noexiste@test.com", "passwordIncorrecta");

        updatePassword(testUser, "nuevaClave123");
        testUser.setPasswordHash("nuevaClave123");
        authenticate(testUser, "nuevaClave123");

        delete(testUser);
    }

    @Test
    void createUser() throws SQLException {
        Usuario usuario = new Usuario(0, "admin", "12345", "admin@gmail.com", (byte) 1, Rol.ADMINISTRADOR);
        Usuario res = usuarioDAO.create(usuario);
        assertNotNull(res, "El usuario creado debe existir.");
        usuarioDAO.delete(res.getId()); // limpieza
    }
}
