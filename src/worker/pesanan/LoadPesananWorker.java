package worker.pesanan;

import javax.swing.*;
import java.util.List;
import model.Pesanan;
import api.PesananApi; // Pakai API Client baru

public class LoadPesananWorker extends SwingWorker<List<Pesanan>, Void> {
    // Ganti Service dengan ApiClient
    private PesananApi apiClient = new PesananApi();
    private String keyword;
    private java.util.function.Consumer<List<Pesanan>> callback;

    public LoadPesananWorker(String keyword, java.util.function.Consumer<List<Pesanan>> callback) {
        this.keyword = keyword;
        this.callback = callback;
    }

    @Override
    protected List<Pesanan> doInBackground() throws Exception {
        // Sekarang memanggil data dari URL PHP melalui ApiClient
        return apiClient.findAll(keyword); 
    }

    @Override
    protected void done() {
        try {
            // Mengirimkan hasil List<Pesanan> ke UI melalui callback
            callback.accept(get()); 
        } catch (Exception e) {
            System.err.println("Gagal memuat data pesanan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}