package worker.dashboard;

import api.DashboardApi; // MEMANGGIL API, BUKAN SERVICE
import javax.swing.SwingWorker;
import model.DashboardModel;
import java.util.function.Consumer;

/**
 * LoadDashboardWorker (Client-Tier)
 * Tugas: Menjalankan request network ke PHP di background agar UI tidak freeze.
 */
public class LoadDashboardWorker extends SwingWorker<DashboardModel, Void> {
    
    // Panggil DashboardApi untuk mengambil data dari PHP
    private DashboardApi api = new DashboardApi(); 
    private Consumer<DashboardModel> callback;

    public LoadDashboardWorker(Consumer<DashboardModel> callback) {
        this.callback = callback;
    }

    @Override
    protected DashboardModel doInBackground() throws Exception {
        // Melakukan HTTP Request ke Application-tier (PHP)
        return api.getStatistics(); 
    }

    @Override
    protected void done() {
        try {
            // Memberikan hasil (DashboardModel) kembali ke View melalui Controller
            callback.accept(get());
        } catch (Exception e) {
            e.printStackTrace();
            callback.accept(null); // Kirim null jika koneksi ke server gagal
        }
    }
}