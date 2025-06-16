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
            ps.setString(4, reserva.getEstado() != null ? reserva.getEstado().getValor() : EstadoReserva.PENDIENTE.getValor());

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
            ps.setString(4, reserva.getEstado() != null ? reserva.getEstado().getValor() : EstadoReserva.PENDIENTE.getValor());
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
                    reserva.setEstado(EstadoReserva.fromString(rs.getString("estado")));
                }
            }
        }
        return reserva;
    }
    public ArrayList<Reserva> getAll() throws SQLException {
        ArrayList<Reserva> lista = new ArrayList<>();

        String sql = "SELECT r.reservaId, " +
                "       u.name AS clienteNombre, " +
                "       c.clienteId, " +
                "       p.nombre AS paqueteNombre, " +
                "       p.paqueteId, " +
                "       r.estado, " +
                "       r.fechaReserva " +
                "FROM Reservas r " +
                "JOIN Clientes c ON r.clienteId = c.clienteId " +
                "JOIN Users u ON c.userId = u.id " +
                "JOIN Paquetes p ON r.paqueteId = p.paqueteId";

        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setReservaId(rs.getInt("reservaId"));

                Cliente cliente = new Cliente();
                cliente.setClienteId(rs.getInt("clienteId"));
                cliente.setNombre(rs.getString("clienteNombre"));
                reserva.setCliente(cliente);

                Paquete paquete = new Paquete();
                paquete.setPaqueteId(rs.getInt("paqueteId"));
                paquete.setNombre(rs.getString("paqueteNombre"));
                reserva.setPaquete(paquete);

                reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado").toUpperCase()));
                reserva.setFechaReserva(rs.getDate("fechaReserva").toLocalDate());

                lista.add(reserva);
            }
        }

        return lista;
    }

    public ArrayList<Reserva> searchByEstado(EstadoReserva estado) throws SQLException {
        ArrayList<Reserva> lista = new ArrayList<>();

        String sql = "SELECT r.reservaId, " +
                "       u.name AS clienteNombre, " +
                "       c.clienteId, " +
                "       p.nombre AS paqueteNombre, " +
                "       p.paqueteId, " +
                "       r.estado, " +
                "       r.fechaReserva " +
                "FROM Reservas r " +
                "JOIN Clientes c ON r.clienteId = c.clienteId " +
                "JOIN Users u ON c.userId = u.id " +
                "JOIN Paquetes p ON r.paqueteId = p.paqueteId " +
                "WHERE r.estado = ?";

        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, estado.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setReservaId(rs.getInt("reservaId"));

                Cliente cliente = new Cliente();
                cliente.setClienteId(rs.getInt("clienteId"));
                cliente.setNombre(rs.getString("clienteNombre"));
                reserva.setCliente(cliente);

                Paquete paquete = new Paquete();
                paquete.setPaqueteId(rs.getInt("paqueteId"));
                paquete.setNombre(rs.getString("paqueteNombre"));
                reserva.setPaquete(paquete);

                reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado").toUpperCase()));
                reserva.setFechaReserva(rs.getDate("fechaReserva").toLocalDate());

                lista.add(reserva);
            }
        }

        return lista;
    }
}
