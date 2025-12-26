package worker.pelanggan;

import java.sql.*;
import javax.swing.*;
import config.DBConfig;

public class UpdatePelangganWorker extends SwingWorker<Boolean, Void> {
    private int id;
    private String nama, wa, alamat;
    private Runnable onSuccess;

    public UpdatePelangganWorker(int id, String nama, String wa, String alamat, Runnable onSuccess) {
        this.id = id;
        this.nama = nama;
        this.wa = wa;
        this.alamat = alamat;
        this.onSuccess = onSuccess;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        Connection conn = DBConfig.getConnection();
        String sql = "UPDATE pelanggan SET nama_pelanggan=?, no_wa=?, alamat=? WHERE id_pelanggan=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nama);
        ps.setString(2, wa);
        ps.setString(3, alamat);
        ps.setInt(4, id);
        return ps.executeUpdate() > 0;
    }

    @Override
    protected void done() {
        try {
            if (get()) {
                JOptionPane.showMessageDialog(null, "Data Berhasil Diperbarui");
                onSuccess.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Update: " + e.getMessage());
        }
    }
}