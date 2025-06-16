package persistencia;

import dominio.Destino;
import java.sql.*;
import java.util.ArrayList;

public class DestinoDAO {
    private final ConnectionManager conn;
    private static final String TABLE_NAME = "Destinos";

    public DestinoDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Destino create(Destino destino) throws SQLException {
        Destino res = null;
        String sql = "INSERT INTO " + TABLE_NAME + " (nombre, pais, descripcion, imagen) VALUES (?, ?, ?, ?)";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, destino.getNombre());
            ps.setString(2, destino.getPais());
            ps.setString(3, destino.getDescripcion());
            ps.setBytes(4, destino.getImagen());

            int affectedRows = ps.executeUpdate();
            if (affectedRows != 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerado = generatedKeys.getInt(1);
                        res = getById(idGenerado);
                    } else {
                        throw new SQLException("Fallo al crear destino, no se obtuvo ID.");
                    }
                }
            }
        }
        return res;
    }

    public boolean update(Destino destino) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET nombre = ?, pais = ?, descripcion = ?, imagen = ? WHERE destinoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, destino.getNombre());
            ps.setString(2, destino.getPais());
            ps.setString(3, destino.getDescripcion());
            ps.setBytes(4, destino.getImagen());
            ps.setInt(5, destino.getDestinoId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int destinoId) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE destinoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, destinoId);
            return ps.executeUpdate() > 0;
        }
    }

    public Destino getById(int destinoId) throws SQLException {
        Destino destino = null;
        String sql = "SELECT destinoId, nombre, pais, descripcion, imagen FROM " + TABLE_NAME + " WHERE destinoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, destinoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    destino = new Destino();
                    destino.setDestinoId(rs.getInt("destinoId"));
                    destino.setNombre(rs.getString("nombre"));
                    destino.setPais(rs.getString("pais"));
                    destino.setDescripcion(rs.getString("descripcion"));
                    destino.setImagen(rs.getBytes("imagen"));
                }
            }
        }
        return destino;
    }

    // ✅ Método para obtener el nombre de un destino por su ID
    public String getNombreById(int id) throws SQLException {
        String nombre = "Sin destino";
        String sql = "SELECT nombre FROM " + TABLE_NAME + " WHERE destinoId = ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombre = rs.getString("nombre");
                }
            }
        }
        return nombre;
    }

    public ArrayList<Destino> search(String nombre) throws SQLException {
        ArrayList<Destino> destinos = new ArrayList<>();
        String sql = "SELECT destinoId, nombre, pais, descripcion, imagen FROM " + TABLE_NAME + " WHERE nombre LIKE ?";
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Destino destino = new Destino();
                    destino.setDestinoId(rs.getInt("destinoId"));
                    destino.setNombre(rs.getString("nombre"));
                    destino.setPais(rs.getString("pais"));
                    destino.setDescripcion(rs.getString("descripcion"));
                    destino.setImagen(rs.getBytes("imagen"));
                    destinos.add(destino);
                }
            }
        }
        return destinos;
    }

    public ArrayList<Destino> getAll() throws SQLException {
        ArrayList<Destino> destinos = new ArrayList<>();
        String sql = "SELECT destinoId, nombre, pais, descripcion, imagen FROM " + TABLE_NAME;
        try (Connection connection = conn.connect();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Destino destino = new Destino();
                destino.setDestinoId(rs.getInt("destinoId"));
                destino.setNombre(rs.getString("nombre"));
                destino.setPais(rs.getString("pais"));
                destino.setDescripcion(rs.getString("descripcion"));
                destino.setImagen(rs.getBytes("imagen"));
                destinos.add(destino);
            }
        }
        return destinos;
    }
}