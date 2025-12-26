package worker.pelanggan;

import api.PelangganApi;
import model.Pelanggan;
import javax.swing.*;
import java.util.function.Consumer;

public class UpdatePelangganWorker extends SwingWorker<Boolean, Void> {
    private Pelanggan pelanggan;
    private Consumer<Boolean> onResult;

    // Menggunakan objek Pelanggan agar konsisten dengan SaveWorker
    public UpdatePelangganWorker(Pelanggan p, Consumer<Boolean> onResult) {
        this.pelanggan = p;
        this.onResult = onResult;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        // Menggunakan PelangganApi untuk melakukan POST/PUT ke server
        PelangganApi api = new PelangganApi();
        return api.save(pelanggan); 
        // Catatan: Karena PHP kita pakai ON DUPLICATE KEY UPDATE, 
        // memanggil api.save() dengan ID yang sudah ada otomatis akan meng-update.
    }

    @Override
    protected void done() {
        try {
            boolean sukses = get();
            onResult.accept(sukses);
            
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Data Berhasil Diperbarui");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Update: " + e.getMessage());
            onResult.accept(false);
        }
    }
}