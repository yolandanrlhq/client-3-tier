package worker.pelanggan;

import java.sql.*;
import javax.swing.*;
import config.DBConfig;

public class SavePelangganWorker extends SwingWorker<Boolean, Void> {
    private String nama, wa, alamat;
    private Runnable onSuccess;

    public SavePelangganWorker(String nama, String wa, String alamat, Runnable onSuccess) {
        this.nama = nama;
        this.wa = wa;
        this.alamat = alamat;
        this.onSuccess = onSuccess;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        Connection conn = DBConfig.getConnection();
        String sql = "INSERT INTO pelanggan (nama_pelanggan, no_wa, alamat) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nama);
        ps.setString(2, wa);
        ps.setString(3, alamat);
        return ps.executeUpdate() > 0;
    }

    @Override
    protected void done() {
        try {
            if (get()) {
                JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
                onSuccess.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Simpan: " + e.getMessage());
        }
    }
}