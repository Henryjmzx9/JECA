package persistencia;

import dominio.Pago;
import java.sql.*;
import java.util.ArrayList;

public class PagoDAO {
    private final ConnectionManager conn;
    private static final String TABLE_NAME = "Pagos";

    public PagoDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Pago create(Pago pago) throws SQLException {
        Pago result = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (reservaId, monto, metodoPagoId, fechaPago) VALUES (?, ?, ?, ?)";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pago.getReservaId());
            ps.setDouble(2, pago.getMonto());
            ps.setInt(3, pago.getMetodoPagoId());
            ps.setDate(4, new java.sql.Date(pago.getFechaPago().getTime()));

            int rows = ps.executeUpdate();
            if (rows != 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        result = getById(idGenerado);
                    }
                }
            }
        }
        return result;
    }

    public boolean update(Pago pago) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET reservaId = ?, monto = ?, metodoPagoId = ?, fechaPago = ? WHERE pagoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, pago.getReservaId());
            ps.setDouble(2, pago.getMonto());
            ps.setInt(3, pago.getMetodoPagoId());
            ps.setDate(4, new java.sql.Date(pago.getFechaPago().getTime()));
            ps.setInt(5, pago.getPagoId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int pagoId) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE pagoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, pagoId);
            return ps.executeUpdate() > 0;
        }
    }

    public Pago getById(int pagoId) throws SQLException {
        Pago pago = null;
        String sql = "SELECT pagoId, reservaId, monto, metodoPagoId, fechaPago FROM " + TABLE_NAME + " WHERE pagoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, pagoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pago = new Pago();
                    pago.setPagoId(rs.getInt("pagoId"));
                    pago.setReservaId(rs.getInt("reservaId"));
                    pago.setMonto(rs.getDouble("monto"));
                    pago.setMetodoPagoId(rs.getInt("metodoPagoId"));
                    pago.setFechaPago(rs.getDate("fechaPago"));
                }
            }
        }
        return pago;
    }
    public ArrayList<Pago> searchByReservaId(int reservaId) throws SQLException {
        ArrayList<Pago> pagos = new ArrayList<>();
        String sql = "SELECT pagoId, reservaId, monto, metodoPagoId, fechaPago FROM " + TABLE_NAME + " WHERE reservaId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago pago = new Pago();
                    pago.setPagoId(rs.getInt("pagoId"));
                    pago.setReservaId(rs.getInt("reservaId"));
                    pago.setMonto(rs.getDouble("monto"));
                    pago.setMetodoPagoId(rs.getInt("metodoPagoId"));
                    pago.setFechaPago(rs.getDate("fechaPago"));
                    pagos.add(pago);
                }
            }
        }
        return pagos;
    }
    public ArrayList<Pago> searchByMetodoPagoId(int metodoPagoId) throws SQLException {
        ArrayList<Pago> pagos = new ArrayList<>();
        String sql = "SELECT pagoId, reservaId, monto, metodoPagoId, fechaPago FROM " + TABLE_NAME + " WHERE metodoPagoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, metodoPagoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago pago = new Pago();
                    pago.setPagoId(rs.getInt("pagoId"));
                    pago.setReservaId(rs.getInt("reservaId"));
                    pago.setMonto(rs.getDouble("monto"));
                    pago.setMetodoPagoId(rs.getInt("metodoPagoId"));
                    pago.setFechaPago(rs.getDate("fechaPago"));
                    pagos.add(pago);
                }
            }
        }
        return pagos;
    }

    public ArrayList<Pago> getAll() throws SQLException {
        ArrayList<Pago> pagos = new ArrayList<>();
        String sql = "SELECT pagoId, reservaId, monto, metodoPagoId, fechaPago FROM " + TABLE_NAME;
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pago pago = new Pago();
                pago.setPagoId(rs.getInt("pagoId"));
                pago.setReservaId(rs.getInt("reservaId"));
                pago.setMonto(rs.getDouble("monto"));
                pago.setMetodoPagoId(rs.getInt("metodoPagoId"));
                pago.setFechaPago(rs.getDate("fechaPago"));
                pagos.add(pago);
            }
        }
        return pagos;
    }
}

