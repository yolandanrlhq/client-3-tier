package worker.pesanan;

import javax.swing.SwingWorker;
import model.Pesanan;
import api.PesananApi; // Ganti ke API Client

public class SavePesananWorker extends SwingWorker<Boolean, Void> {
    private Pesanan pesanan;
    // Gunakan ApiClient sebagai pengganti Service
    private PesananApi apiClient = new PesananApi();
    private java.util.function.Consumer<Boolean> callback;

    public SavePesananWorker(Pesanan pesanan, java.util.function.Consumer<Boolean> callback) {
        this.pesanan = pesanan;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        // Mengirim data pesanan ke PHP (App-Tier)
        apiClient.create(pesanan);
        return true; 
    }

    @Override
    protected void done() {
        try {
            callback.accept(get());
        } catch (Exception e) {
            System.err.println("Gagal menyimpan ke server: " + e.getMessage());
            callback.accept(false);
        }
    }
}