package worker.pesanan;

import javax.swing.SwingWorker;
import model.Pesanan;
import api.PesananApi; // Pakai API Client

public class UpdatePesananWorker extends SwingWorker<Boolean, Void> {
    private Pesanan pesanan;
    private PesananApi apiClient = new PesananApi();
    private java.util.function.Consumer<Boolean> callback;

    public UpdatePesananWorker(Pesanan pesanan, java.util.function.Consumer<Boolean> callback) {
        this.pesanan = pesanan;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        // Panggil method update yang baru kita buat di ApiClient
        apiClient.update(pesanan);
        return true;
    }

    @Override
    protected void done() {
        try {
            callback.accept(get());
        } catch (Exception e) {
            System.err.println("Gagal update data via API: " + e.getMessage());
            callback.accept(false);
        }
    }
}