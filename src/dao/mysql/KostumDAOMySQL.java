package dao.mysql;

import dao.KostumDAO;
import model.Kostum;
import config.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KostumDAOMySQL implements KostumDAO {

    // =========================
    // DIGUNAKAN OLEH PanelProduk
    // =========================
    @Override
    public List<Kostum> getAll(String keyword) {
        List<Kostum> list = new ArrayList<>();

        String sql = """
            SELECT id_kostum,
                   nama_kostum,
                   kategori,
                   stok,
                   ukuran,
                   harga_sewa,
                   status
            FROM kostum
            WHERE id_kostum LIKE ?
               OR nama_kostum LIKE ?
               OR kategori LIKE ?
            ORDER BY nama_kostum ASC
        """;

        try (
            Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Kostum k = new Kostum();
                    k.setId(rs.getString("id_kostum"));
                    k.setNama(rs.getString("nama_kostum"));
                    k.setKategori(rs.getString("kategori"));
                    k.setStok(rs.getInt("stok"));
                    k.setUkuran(rs.getString("ukuran"));
                    k.setHarga(rs.getDouble("harga_sewa"));
                    k.setStatus(rs.getString("status"));
                    list.add(k);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // DIGUNAKAN OLEH AKSI PANELPRODUK
    // =========================
    @Override
    public void update(Kostum k) {
        String sql = """
            UPDATE kostum
            SET nama_kostum = ?,
                stok        = ?,
                harga_sewa  = ?
            WHERE id_kostum = ?
        """;

        try (
            Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, k.getNama());
            ps.setInt(2, k.getStok());
            ps.setDouble(3, k.getHarga());
            ps.setString(4, k.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM kostum WHERE id_kostum = ?";

        try (
            Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // METHOD TAMBAHAN (AMAN – TIDAK MERUSAK PANELPRODUK)
    // =====================================================

    // Dipakai PanelAddProduk / Service
    public void insert(Kostum k) throws SQLException {
        String sql = """
            INSERT INTO kostum
            (id_kostum, nama_kostum, kategori, stok, ukuran, harga_sewa, status)
            VALUES (?, ?, ?, ?, ?, ?, 'Tersedia')
        """;

        try (
            Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, k.getId());
            ps.setString(2, k.getNama());
            ps.setString(3, k.getKategori());
            ps.setInt(4, k.getStok());
            ps.setString(5, k.getUkuran());
            ps.setDouble(6, k.getHarga());
            ps.executeUpdate();
        }
    }

    // Untuk kebutuhan stok (pesanan)
    public int getStokById(String idKostum) throws SQLException {
        String sql = "SELECT stok FROM kostum WHERE id_kostum = ?";

        try (
            Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, idKostum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stok");
                }
            }
        }
        return 0;
    }

    public void updateStok(String idKostum, int stokBaru) throws SQLException {
        String sql = "UPDATE kostum SET stok = ? WHERE id_kostum = ?";

        try (
            Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, stokBaru);
            ps.setString(2, idKostum);
            ps.executeUpdate();
        }
    }
}
