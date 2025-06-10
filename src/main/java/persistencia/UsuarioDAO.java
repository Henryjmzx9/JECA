package persistencia;

import dominio.Usuario;
import utils.PasswordHasher;
import utils.Rol;
import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO {
    private final ConnectionManager conn;
    private static final String TABLE_NAME = "Users"; // <-- tabla corregida aquí

    public UsuarioDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Usuario create(Usuario usuario) throws SQLException {
        Usuario res = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (name, passwordHash, email, status, rol) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getName());
            String hashedPassword = PasswordHasher.hashPassword(usuario.getPasswordHash());
            ps.setString(2, hashedPassword);
            ps.setString(3, usuario.getEmail());
            ps.setByte(4, usuario.getStatus());
            ps.setString(5, usuario.getRol().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows != 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        res = getById(idGenerado);
                    } else {
                        throw new SQLException("Failed to create user, no ID obtained.");
                    }
                }
            }
        }
        return res;
    }

    public boolean update(Usuario usuario) throws SQLException {
        boolean res;
        String sql = "UPDATE " + TABLE_NAME + " SET name = ?, email = ?, status = ?, rol = ? WHERE id = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario.getName());
            ps.setString(2, usuario.getEmail());
            ps.setByte(3, usuario.getStatus());
            ps.setString(4, usuario.getRol().name());
            ps.setInt(5, usuario.getId());

            res = ps.executeUpdate() > 0;
        }
        return res;
    }

    public boolean updatePassword(Usuario usuario) throws SQLException {
        boolean res;
        String sql = "UPDATE " + TABLE_NAME + " SET passwordHash = ? WHERE id = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String hashedPassword = PasswordHasher.hashPassword(usuario.getPasswordHash());
            ps.setString(1, hashedPassword);
            ps.setInt(2, usuario.getId());

            res = ps.executeUpdate() > 0;
        }
        return res;
    }

    public boolean delete(int id) throws SQLException {
        boolean res;
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            res = ps.executeUpdate() > 0;
        }
        return res;
    }

    public Usuario getById(int id) throws SQLException {
        Usuario usuario = null;
        String sql = "SELECT id, name, passwordHash, email, status, rol FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setName(rs.getString("name"));
                    usuario.setPasswordHash(rs.getString("passwordHash"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setStatus(rs.getByte("status"));
                    usuario.setRol(Rol.valueOf(rs.getString("rol")));
                }
            }
        }
        return usuario;
    }

    public ArrayList<Usuario> search(String name) throws SQLException {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, name, email, status, rol FROM " + TABLE_NAME + " WHERE name LIKE ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setName(rs.getString("name"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setStatus(rs.getByte("status"));
                    usuario.setRol(Rol.valueOf(rs.getString("rol")));
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }

    public ArrayList<Usuario> getAll() throws SQLException {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, name, email, status, rol FROM " + TABLE_NAME ;
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setName(rs.getString("name"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setStatus(rs.getByte("status"));
                    usuario.setRol(Rol.valueOf(rs.getString("rol")));
                    usuarios.add(usuario);
                }
            }
        }
        return usuarios;
    }
    public static int getIdByEmail(String email) throws SQLException {
        int userId = -1;
        String sql = "SELECT id FROM Users WHERE email = ?";
        try (Connection connection = ConnectionManager.getInstance().connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }
        }
        return userId;
    }
    public Usuario authenticate(String email, String plainPassword) throws SQLException {
        Usuario usuario = null;
        String sql = "SELECT id, name, passwordHash, email, status, rol FROM " + TABLE_NAME + " WHERE email = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("passwordHash");
                    String inputHash = PasswordHasher.hashPassword(plainPassword);

                    if (storedHash.equals(inputHash)) {
                        usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setName(rs.getString("name"));
                        usuario.setPasswordHash(storedHash);
                        usuario.setEmail(rs.getString("email"));
                        usuario.setStatus(rs.getByte("status"));
                        usuario.setRol(Rol.valueOf(rs.getString("rol")));
                    }
                }
            }
        }

        return usuario;
    }
}