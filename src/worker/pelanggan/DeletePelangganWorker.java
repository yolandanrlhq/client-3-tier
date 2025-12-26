package worker.pelanggan;

import api.PelangganApi;
import javax.swing.*;
import java.util.function.Consumer;

public class DeletePelangganWorker extends SwingWorker<Boolean, Void> {
    private String id; // Diubah ke String sesuai database (HD001)
    private Consumer<Boolean> onResult; // Gunakan Consumer agar lebih fleksibel

    public DeletePelangganWorker(String id, Consumer<Boolean> onResult) {
        this.id = id;
        this.onResult = onResult;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        // Panggil API, bukan JDBC/SQL!
        PelangganApi api = new PelangganApi();
        return api.delete(id);
    }

    @Override
    protected void done() {
        try {
            boolean sukses = get();
            onResult.accept(sukses);
            // Notifikasi (Optional: Bisa ditaruh di Controller agar Worker tetap bersih)
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Hapus: " + e.getMessage());
            onResult.accept(false);
        }
    }
}