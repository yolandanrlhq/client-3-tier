package dao.mysql;

import dao.PesananDao;
import model.Pesanan;
import config.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PesananDaoMySql implements PesananDao {

    // =========================
    // INSERT PESANAN (AMAN)
    // =========================
    @Override
    public boolean insert(Pesanan p) throws Exception {
        Connection conn = DBConfig.getConnection();
        conn.setAutoCommit(false);

        try {
            // 1. Ambil nama kostum (snapshot)
            String namaKostum = null;
            PreparedStatement psNama = conn.prepareStatement(
                "SELECT nama_kostum FROM kostum WHERE id_kostum=?"
            );
            psNama.setString(1, p.getIdKostum());
            ResultSet rsNama = psNama.executeQuery();

            if (rsNama.next()) {
                namaKostum = rsNama.getString("nama_kostum");
            } else {
                throw new Exception("Kostum tidak ditemukan");
            }

            // 2. Insert pesanan (WAJIB id_kostum & nama_kostum)
            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO pesanan " +
                "(id_sewa, nama_penyewa, id_kostum, nama_kostum, jumlah, tgl_pinjam, total_biaya, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            pst.setString(1, p.getIdSewa());
            pst.setString(2, p.getNamaPenyewa());
            pst.setString(3, p.getIdKostum());
            pst.setString(4, namaKostum);
            pst.setInt(5, p.getJumlah());
            pst.setDate(6, new java.sql.Date(p.getTglPinjam().getTime()));
            pst.setDouble(7, p.getTotalBiaya());
            pst.setString(8, "Disewa");
            pst.executeUpdate();

            // 3. Kurangi stok
            PreparedStatement pstStok = conn.prepareStatement(
                "UPDATE kostum SET stok = stok - ? WHERE id_kostum=?"
            );
            pstStok.setInt(1, p.getJumlah());
            pstStok.setString(2, p.getIdKostum());
            pstStok.executeUpdate();

            // 4. Update status kostum
            conn.createStatement().executeUpdate(
                "UPDATE kostum SET status='Disewa' WHERE id_kostum='" + p.getIdKostum() + "' AND stok <= 0"
            );

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    // =========================
    // FIND ALL (TANPA JOIN ❗)
    // =========================
    @Override
    public List<Pesanan> findAll(String keyword) throws Exception {
        List<Pesanan> list = new ArrayList<>();

        String sql =
            "SELECT * FROM pesanan " +
            "WHERE id_sewa LIKE ? OR nama_penyewa LIKE ? OR nama_kostum LIKE ? " +
            "ORDER BY tgl_pinjam DESC";

        Connection conn = DBConfig.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);

        String key = "%" + keyword + "%";
        pst.setString(1, key);
        pst.setString(2, key);
        pst.setString(3, key);

        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Pesanan p = new Pesanan();
            p.setIdSewa(rs.getString("id_sewa"));
            p.setNamaPenyewa(rs.getString("nama_penyewa"));
            p.setIdKostum(rs.getString("id_kostum"));
            p.setNamaKostum(rs.getString("nama_kostum")); // 🔥 AMAN
            p.setJumlah(rs.getInt("jumlah"));
            p.setTglPinjam(rs.getDate("tgl_pinjam"));
            p.setTotalBiaya(rs.getDouble("total_biaya"));
            p.setStatus(rs.getString("status"));
            list.add(p);
        }

        conn.close();
        return list;
    }

    // =========================
    // UPDATE STATUS PESANAN
    // =========================
    @Override
    public boolean update(Pesanan p) throws Exception {
        Connection conn = DBConfig.getConnection();
        conn.setAutoCommit(false);

        try {
            // 1. Update status pesanan
            String sqlUpdatePesanan =
                "UPDATE pesanan SET status=? WHERE id_sewa=?";
            PreparedStatement pst = conn.prepareStatement(sqlUpdatePesanan);
            pst.setString(1, p.getStatus());
            pst.setString(2, p.getIdSewa());
            pst.executeUpdate();

            // 2. JIKA SELESAI / DIBATALKAN → KEMBALIKAN STOK
            if (p.getStatus().equalsIgnoreCase("Selesai")
                || p.getStatus().equalsIgnoreCase("Dibatalkan")) {

                // ambil id_kostum & jumlah
                String sqlGet =
                    "SELECT id_kostum, jumlah FROM pesanan WHERE id_sewa=?";
                pst = conn.prepareStatement(sqlGet);
                pst.setString(1, p.getIdSewa());
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String idKostum = rs.getString("id_kostum");
                    int jumlah = rs.getInt("jumlah");

                    // kembalikan stok
                    String sqlUpdateStok =
                        "UPDATE kostum SET stok = stok + ?, status='Tersedia' " +
                        "WHERE id_kostum=?";
                    pst = conn.prepareStatement(sqlUpdateStok);
                    pst.setInt(1, jumlah);
                    pst.setString(2, idKostum);
                    pst.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    // =========================
    // DELETE PESANAN
    // =========================
    @Override
    public boolean delete(String id) throws Exception {
        Connection conn = DBConfig.getConnection();
        conn.setAutoCommit(false);

        try {
            Pesanan p = findById(id);

            conn.createStatement().executeUpdate(
                "DELETE FROM pesanan WHERE id_sewa='" + id + "'"
            );

            conn.createStatement().executeUpdate(
                "UPDATE kostum SET stok = stok + " + p.getJumlah() +
                ", status='Tersedia' WHERE id_kostum='" + p.getIdKostum() + "'"
            );

            conn.commit();
            return true;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    @Override
    public String getIdKostumByIdSewa(String idSewa) throws Exception {
        Connection conn = DBConfig.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT id_kostum FROM pesanan WHERE id_sewa='" + idSewa + "'"
        );
        if (rs.next()) {
            String id = rs.getString("id_kostum");
            conn.close();
            return id;
        }
        conn.close();
        throw new Exception("ID Kostum tidak ditemukan");
    }

    @Override
    public Pesanan findById(String id) throws Exception {
        Connection conn = DBConfig.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT * FROM pesanan WHERE id_sewa='" + id + "'"
        );
        if (rs.next()) {
            Pesanan p = new Pesanan();
            p.setIdSewa(rs.getString("id_sewa"));
            p.setNamaPenyewa(rs.getString("nama_penyewa"));
            p.setIdKostum(rs.getString("id_kostum"));
            p.setNamaKostum(rs.getString("nama_kostum"));
            p.setJumlah(rs.getInt("jumlah"));
            p.setTglPinjam(rs.getDate("tgl_pinjam"));
            p.setTotalBiaya(rs.getDouble("total_biaya"));
            p.setStatus(rs.getString("status"));
            conn.close();
            return p;
        }
        conn.close();
        throw new Exception("Pesanan tidak ditemukan");
    }
}
