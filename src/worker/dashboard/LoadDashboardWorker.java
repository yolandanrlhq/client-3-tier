package worker.dashboard;

import javax.swing.SwingWorker;
import model.DashboardModel; // Import model baru
import service.DashboardService;

public class LoadDashboardWorker extends SwingWorker<DashboardModel, Void> {
    private DashboardService service = new DashboardService();
    private java.util.function.Consumer<DashboardModel> callback;

    public LoadDashboardWorker(java.util.function.Consumer<DashboardModel> callback) {
        this.callback = callback;
    }

    @Override
    protected DashboardModel doInBackground() {
        // Sekarang memanggil service yang mengembalikan objek DashboardModel
        return service.muatStatistik();
    }

    @Override
    protected void done() {
        try {
            // Mengirim objek DashboardModel ke UI melalui callback
            callback.accept(get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}