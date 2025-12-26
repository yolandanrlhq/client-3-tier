package worker.pelanggan;

import api.PelangganApi;
import model.Pelanggan;
import javax.swing.*;
import java.util.function.Consumer;

/**
 * Worker untuk menyimpan data pelanggan melalui API (App-Tier).
 */
public class SavePelangganWorker extends SwingWorker<Boolean, Void> {
    private Pelanggan pelanggan;
    private Consumer<Boolean> onResult;

    // Sekarang kita terima Objek Pelanggan dan Consumer untuk callback
    public SavePelangganWorker(Pelanggan p, Consumer<Boolean> onResult) {
        this.pelanggan = p;
        this.onResult = onResult;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        // Panggil PelangganApi, biarkan API yang mengirim JSON ke PHP
        PelangganApi api = new PelangganApi();
        return api.save(pelanggan);
    }

    @Override
    protected void done() {
        try {
            boolean sukses = get();
            onResult.accept(sukses);
            
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Simpan: " + e.getMessage());
            onResult.accept(false);
        }
    }
}