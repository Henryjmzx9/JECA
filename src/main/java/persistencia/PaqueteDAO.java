package persistencia;

import dominio.Paquete;
import java.sql.*;
import java.util.ArrayList;

public class PaqueteDAO {
    private final ConnectionManager conn;
    private static final String TABLE_NAME = "Paquetes";

    public PaqueteDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Paquete create(Paquete paquete) throws SQLException {
        Paquete res = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (nombre, descripcion, precio, duracionDias, fechaInicio, fechaFin, destinoId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, paquete.getNombre());
            ps.setString(2, paquete.getDescripcion());
            ps.setDouble(3, paquete.getPrecio());
            ps.setInt(4, paquete.getDuracionDias());
            ps.setDate(5, Date.valueOf(paquete.getFechaInicio()));
            ps.setDate(6, Date.valueOf(paquete.getFechaFin()));
            ps.setInt(7, paquete.getDestinoId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows != 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        res = getById(idGenerado);
                    } else {
                        throw new SQLException("Failed to create package, no ID obtained.");
                    }
                }
            }
        }
        return res;
    }

    public boolean update(Paquete paquete) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET nombre = ?, descripcion = ?, precio = ?, duracionDias = ?, fechaInicio = ?, fechaFin = ?, destinoId = ? WHERE paqueteId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, paquete.getNombre());
            ps.setString(2, paquete.getDescripcion());
            ps.setDouble(3, paquete.getPrecio());
            ps.setInt(4, paquete.getDuracionDias());
            ps.setDate(5, Date.valueOf(paquete.getFechaInicio()));
            ps.setDate(6, Date.valueOf(paquete.getFechaFin()));
            ps.setInt(7, paquete.getDestinoId());
            ps.setInt(8, paquete.getPaqueteId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE paqueteId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    public ArrayList<Paquete> search(String nombre, String descripcion) throws SQLException {
        ArrayList<Paquete> paquetes = new ArrayList<>();
        String sql = "SELECT paqueteId, nombre, descripcion, precio, duracionDias, fechaInicio, fechaFin, destinoId FROM " + TABLE_NAME + " WHERE nombre LIKE ? OR descripcion LIKE ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            ps.setString(2, "%" + descripcion + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Paquete paquete = new Paquete();
                    paquete.setPaqueteId(rs.getInt("paqueteId"));
                    paquete.setNombre(rs.getString("nombre"));
                    paquete.setDescripcion(rs.getString("descripcion"));
                    paquete.setPrecio(rs.getDouble("precio"));
                    paquete.setDuracionDias(rs.getInt("duracionDias"));
                    paquete.setFechaInicio(rs.getDate("fechaInicio").toLocalDate());
                    paquete.setFechaFin(rs.getDate("fechaFin").toLocalDate());
                    paquete.setDestinoId(rs.getInt("destinoId"));
                    paquetes.add(paquete);
                }
            }
        }
        return paquetes;
    }

    public Paquete getById(int id) throws SQLException {
        Paquete paquete = null;
        String sql = "SELECT paqueteId, nombre, descripcion, precio, duracionDias, fechaInicio, fechaFin, destinoId FROM " + TABLE_NAME + " WHERE paqueteId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    paquete = new Paquete();
                    paquete.setPaqueteId(rs.getInt("paqueteId"));
                    paquete.setNombre(rs.getString("nombre"));
                    paquete.setDescripcion(rs.getString("descripcion"));
                    paquete.setPrecio(rs.getDouble("precio"));
                    paquete.setDuracionDias(rs.getInt("duracionDias"));
                    paquete.setFechaInicio(rs.getDate("fechaInicio").toLocalDate());
                    paquete.setFechaFin(rs.getDate("fechaFin").toLocalDate());
                    paquete.setDestinoId(rs.getInt("destinoId"));
                }
            }
        }
        return paquete;
    }
    public Paquete getByNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM Paquetes WHERE nombre = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Paquete paquete = new Paquete();
                    paquete.setPaqueteId(rs.getInt("paqueteId"));
                    paquete.setNombre(rs.getString("nombre"));
                    paquete.setDescripcion(rs.getString("descripcion"));
                    paquete.setPrecio(rs.getDouble("precio"));
                    // Si quieres obtener el destino también, puedes buscarlo por ID.
                    return paquete;
                }
            }
        }
        return null;
    }


    public ArrayList<Paquete> getAll() throws SQLException {
        ArrayList<Paquete> paquetes = new ArrayList<>();
        String sql = "SELECT paqueteId, nombre, descripcion, precio, duracionDias, fechaInicio, fechaFin, destinoId FROM " + TABLE_NAME;
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Paquete paquete = new Paquete();
                paquete.setPaqueteId(rs.getInt("paqueteId"));
                paquete.setNombre(rs.getString("nombre"));
                paquete.setDescripcion(rs.getString("descripcion"));
                paquete.setPrecio(rs.getDouble("precio"));
                paquete.setDuracionDias(rs.getInt("duracionDias"));
                paquete.setFechaInicio(rs.getDate("fechaInicio").toLocalDate());
                paquete.setFechaFin(rs.getDate("fechaFin").toLocalDate());
                paquete.setDestinoId(rs.getInt("destinoId"));
                paquetes.add(paquete);
            }
        }
        return paquetes;
    }

    public ArrayList<Paquete> searchPaquete(String nombre) throws SQLException {
        ArrayList<Paquete> paquetes = new ArrayList<>();
        String sql = "SELECT paqueteId, nombre, descripcion, precio, duracionDias, fechaInicio, fechaFin, destinoId FROM " + TABLE_NAME + " WHERE nombre LIKE ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Paquete paquete = new Paquete();
                    paquete.setPaqueteId(rs.getInt("paqueteId"));
                    paquete.setNombre(rs.getString("nombre"));
                    paquete.setDescripcion(rs.getString("descripcion"));
                    paquete.setPrecio(rs.getDouble("precio"));
                    paquete.setDuracionDias(rs.getInt("duracionDias"));
                    paquete.setFechaInicio(rs.getDate("fechaInicio").toLocalDate());
                    paquete.setFechaFin(rs.getDate("fechaFin").toLocalDate());
                    paquete.setDestinoId(rs.getInt("destinoId"));
                    paquetes.add(paquete);
                }
            }
        }
        return paquetes;
    }


}
