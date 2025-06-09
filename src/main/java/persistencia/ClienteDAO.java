package persistencia;

import dominio.Cliente;
import java.sql.*;
import java.util.ArrayList;

public class ClienteDAO {
    private final ConnectionManager conn;
    private static final String TABLE_NAME = "Clientes";
    public ClienteDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Cliente create(Cliente cliente) throws SQLException {
        Cliente res = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (userId, telefono, direccion) VALUES (?, ?, ?)";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cliente.getUserId());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getDireccion());

            int affectedRows = ps.executeUpdate();
            if (affectedRows != 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        res = getById(idGenerado);
                    } else {
                        throw new SQLException("Failed to create client, no ID obtained.");
                    }
                }
            }
        }
        return res;
    }

    public boolean update(Cliente cliente) throws SQLException {
        boolean res;
        String sql = "UPDATE " + TABLE_NAME + " SET userId = ?, telefono = ?, direccion = ? WHERE clienteId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, cliente.getUserId());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getDireccion());
            ps.setInt(4, cliente.getClienteId());

            res = ps.executeUpdate() > 0;
        }
        return res;
    }

    public boolean delete(int clienteId) throws SQLException {
        boolean res;
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE clienteId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            res = ps.executeUpdate() > 0;
        }
        return res;
    }

    public Cliente getById(int clienteId) throws SQLException {
        Cliente cliente = null;
        String sql = "SELECT clienteId, userId, telefono, direccion FROM " + TABLE_NAME + " WHERE clienteId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setClienteId(rs.getInt("clienteId"));
                    cliente.setUserId(rs.getInt("userId"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setDireccion(rs.getString("direccion"));
                }
            }
        }
        return cliente;
    }

    public ArrayList<Cliente> search(String telefono) throws SQLException {
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT clienteId, userId, telefono, direccion FROM " + TABLE_NAME + " WHERE telefono LIKE ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + telefono + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setClienteId(rs.getInt("clienteId"));
                    cliente.setUserId(rs.getInt("userId"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setDireccion(rs.getString("direccion"));
                    clientes.add(cliente);
                }
            }
        }
        return clientes;
    }

    // 🔐 Método de autenticación
    public Cliente authenticate(int userId, String telefono) throws SQLException {
        Cliente cliente = null;
        String sql = "SELECT clienteId, userId, telefono, direccion FROM " + TABLE_NAME + " WHERE userId = ? AND telefono = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setClienteId(rs.getInt("clienteId"));
                    cliente.setUserId(rs.getInt("userId"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setDireccion(rs.getString("direccion"));
                }
            }
        }
        return cliente;
    }
}