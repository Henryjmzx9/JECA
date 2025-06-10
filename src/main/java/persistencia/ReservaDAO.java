package persistencia;

import dominio.Reserva;
import dominio.Cliente;
import dominio.Paquete;
import utils.EstadoReserva;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ReservaDAO {
    private final ConnectionManager conn;
    private static final String TABLE_NAME = "Reservas";

    public ReservaDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Reserva create(Reserva reserva) throws SQLException {
        Reserva res = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (clienteId, paqueteId, fechaReserva, estado) VALUES (?, ?, ?, ?)";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reserva.getCliente().getClienteId());
            ps.setInt(2, reserva.getPaquete().getPaqueteId());
            ps.setDate(3, Date.valueOf(reserva.getFechaReserva()));
            ps.setString(4, reserva.getEstado().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows != 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        res = getById(idGenerado);
                    } else {
                        throw new SQLException("Failed to create reserva, no ID obtained.");
                    }
                }
            }
        }
        return res;
    }

    public boolean update(Reserva reserva) throws SQLException {
        boolean res;
        String sql = "UPDATE " + TABLE_NAME + " SET clienteId = ?, paqueteId = ?, fechaReserva = ?, estado = ? WHERE reservaId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, reserva.getCliente().getClienteId());
            ps.setInt(2, reserva.getPaquete().getPaqueteId());
            ps.setDate(3, Date.valueOf(reserva.getFechaReserva()));
            ps.setString(4, reserva.getEstado().name());
            ps.setInt(5, reserva.getReservaId());

            res = ps.executeUpdate() > 0;
        }
        return res;
    }

    public boolean delete(int reservaId) throws SQLException {
        boolean res;
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE reservaId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, reservaId);
            res = ps.executeUpdate() > 0;
        }
        return res;
    }

    public Reserva getById(int reservaId) throws SQLException {
        Reserva reserva = null;
        String sql = "SELECT reservaId, clienteId, paqueteId, fechaReserva, estado FROM " + TABLE_NAME + " WHERE reservaId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reserva = new Reserva();
                    reserva.setReservaId(rs.getInt("reservaId"));
                    reserva.setCliente(new Cliente(rs.getInt("clienteId")));
                    reserva.setPaquete(new Paquete(rs.getInt("paqueteId")));
                    reserva.setFechaReserva(rs.getDate("fechaReserva").toLocalDate());
                    reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
                }
            }
        }
        return reserva;
    }

    public ArrayList<Reserva> getAll() throws SQLException {
        ArrayList<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT reservaId, clienteId, paqueteId, fechaReserva, estado FROM " + TABLE_NAME;
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setReservaId(rs.getInt("reservaId"));
                reserva.setCliente(new Cliente(rs.getInt("clienteId")));
                reserva.setPaquete(new Paquete(rs.getInt("paqueteId")));
                reserva.setFechaReserva(rs.getDate("fechaReserva").toLocalDate());
                reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
                reservas.add(reserva);
            }
        }
        return reservas;
    }

    public ArrayList<Reserva> search(String keyword) throws SQLException {
        ArrayList<Reserva> resultados = new ArrayList<>();
        String sql = """
            SELECT r.reservaId, r.clienteId, r.paqueteId, r.fechaReserva, r.estado
            FROM Reservas r
            JOIN Clientes c ON r.clienteId = c.clienteId
            JOIN Paquetes p ON r.paqueteId = p.paqueteId
            WHERE c.direccion LIKE ? OR p.nombre LIKE ?
        """;
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva();
                    reserva.setReservaId(rs.getInt("reservaId"));
                    reserva.setCliente(new Cliente(rs.getInt("clienteId")));
                    reserva.setPaquete(new Paquete(rs.getInt("paqueteId")));
                    reserva.setFechaReserva(rs.getDate("fechaReserva").toLocalDate());
                    reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
                    resultados.add(reserva);
                }
            }
        }
        return resultados;
    }
}