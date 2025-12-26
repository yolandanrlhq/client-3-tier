package worker.pelanggan;

import java.sql.*;
import javax.swing.*;
import config.DBConfig;

public class DeletePelangganWorker extends SwingWorker<Boolean, Void> {
    private int id;
    private Runnable onSuccess;

    public DeletePelangganWorker(int id, Runnable onSuccess) {
        this.id = id;
        this.onSuccess = onSuccess;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        Connection conn = DBConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM pelanggan WHERE id_pelanggan = ?");
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    }

    @Override
    protected void done() {
        try {
            if (get()) {
                JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
                onSuccess.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Hapus: " + e.getMessage());
        }
    }
}