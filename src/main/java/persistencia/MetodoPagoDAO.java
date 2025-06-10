package persistencia;

import dominio.MetodoPago;
import java.sql.*;
import java.util.ArrayList;

public class MetodoPagoDAO {
    private final ConnectionManager conn;
    private static final String TABLE_NAME = "MetodoPago"; // nombre correcto de la tabla

    public MetodoPagoDAO() {
        conn = ConnectionManager.getInstance();
    }

    public MetodoPago create(MetodoPago metodo) throws SQLException {
        MetodoPago res = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (nombreMetodo) VALUES (?)";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, metodo.getNombreMetodo());
            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        res = getById(idGenerado);
                    }
                }
            }
        }
        return res;
    }

    public boolean update(MetodoPago metodo) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET nombreMetodo = ? WHERE metodoPagoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, metodo.getNombreMetodo());
            ps.setInt(2, metodo.getMetodoPagoId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE metodoPagoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public MetodoPago getById(int id) throws SQLException {
        MetodoPago metodo = null;
        String sql = "SELECT metodoPagoId, nombreMetodo FROM " + TABLE_NAME + " WHERE metodoPagoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metodo = new MetodoPago();
                    metodo.setMetodoPagoId(rs.getInt("metodoPagoId"));
                    metodo.setNombreMetodo(rs.getString("nombreMetodo"));
                }
            }
        }
        return metodo;
    }
    public ArrayList<MetodoPago> getAll() throws SQLException {
        ArrayList<MetodoPago> lista = new ArrayList<>();
        String sql = "SELECT metodoPagoId, nombreMetodo FROM " + TABLE_NAME;
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MetodoPago metodo = new MetodoPago();
                metodo.setMetodoPagoId(rs.getInt("metodoPagoId"));
                metodo.setNombreMetodo(rs.getString("nombreMetodo"));
                lista.add(metodo);
            }
        }
        return lista;
    }

    public ArrayList<MetodoPago> search(String nombreMetodo) throws SQLException {
        ArrayList<MetodoPago> lista = new ArrayList<>();
        String sql = "SELECT metodoPagoId, nombreMetodo FROM " + TABLE_NAME + " WHERE nombreMetodo LIKE ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + nombreMetodo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MetodoPago metodo = new MetodoPago();
                    metodo.setMetodoPagoId(rs.getInt("metodoPagoId"));
                    metodo.setNombreMetodo(rs.getString("nombreMetodo"));
                    lista.add(metodo);
                }
            }
        }
        return lista;
    }

    public MetodoPago authenticate(String nombreMetodo) throws SQLException {
        MetodoPago metodo = null;
        String sql = "SELECT metodoPagoId, nombreMetodo FROM " + TABLE_NAME + " WHERE nombreMetodo = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nombreMetodo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metodo = new MetodoPago();
                    metodo.setMetodoPagoId(rs.getInt("metodoPagoId"));
                    metodo.setNombreMetodo(rs.getString("nombreMetodo"));
                }
            }
        }
        return metodo;
    }
}