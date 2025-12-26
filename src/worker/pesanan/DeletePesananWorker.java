package worker.pesanan;

import javax.swing.SwingWorker;
import api.PesananApi; // Pakai API Client, bukan Service

public class DeletePesananWorker extends SwingWorker<Boolean, Void> {
    private String id; 
    // Ganti Service menjadi ApiClient
    private PesananApi apiClient = new PesananApi();
    private java.util.function.Consumer<Boolean> callback;

    public DeletePesananWorker(String id, java.util.function.Consumer<Boolean> callback) {
        this.id = id;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        // Panggil method delete di ApiClient yang nembak ke PHP
        apiClient.delete(id);
        return true; // Jika tidak ada exception, berarti sukses
    }

    @Override
    protected void done() {
        try {
            // get() akan melemparkan exception jika doInBackground bermasalah
            callback.accept(get());
        } catch (Exception e) {
            System.err.println("Error saat hapus data via API: " + e.getMessage());
            callback.accept(false);
        }
    }
}