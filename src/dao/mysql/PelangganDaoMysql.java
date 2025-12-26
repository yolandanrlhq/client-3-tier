package dao.mysql;

import config.DBConfig;
import dao.PelangganDao;
import model.Pelanggan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PelangganDaoMysql implements PelangganDao {

    private String generateID() {
        String newID = "HD001";
        String sql = "SELECT MAX(id_pelanggan) AS max_id FROM pelanggan";
        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next() && rs.getString("max_id") != null) {
                String maxId = rs.getString("max_id");
                int numericPart = Integer.parseInt(maxId.substring(2));
                numericPart++;
                newID = String.format("HD%03d", numericPart);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return newID;
    }

    @Override
    public void save(Pelanggan p) {
        // Query ini otomatis UPDATE jika ID sudah ada di database
        String sql = "INSERT INTO pelanggan (id_pelanggan, nama_pelanggan, no_wa, alamat) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nama_pelanggan=?, no_wa=?, alamat=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String finalID = (p.getId() == null || p.getId().isEmpty()) ? generateID() : p.getId();
            
            ps.setString(1, finalID);
            ps.setString(2, p.getNama());
            ps.setString(3, p.getNoWa());
            ps.setString(4, p.getAlamat());
            // Parameter untuk Update
            ps.setString(5, p.getNama());
            ps.setString(6, p.getNoWa());
            ps.setString(7, p.getAlamat());
            
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Pelanggan> getAll() {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT * FROM pelanggan ORDER BY id_pelanggan DESC";
        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Pelanggan(
                    rs.getString("id_pelanggan"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("no_wa"),
                    rs.getString("alamat")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM pelanggan WHERE id_pelanggan = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}