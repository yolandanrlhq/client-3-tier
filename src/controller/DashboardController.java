package controller;

import model.DashboardModel;
import service.DashboardService;
import view.konten.PanelDashboard;
import worker.dashboard.LoadDashboardWorker;

public class DashboardController {
    private PanelDashboard view;
    private DashboardService service;

    public DashboardController(PanelDashboard view) {
        this.view = view;
        this.service = new DashboardService();
    }

    /**
     * Mengatur alur pemuatan data dashboard
     */
    public void muatDataDashboard() {
        // Controller memerintahkan Worker untuk bekerja
        new LoadDashboardWorker(model -> {
            if (model != null) {
                // Controller memberikan data ke View untuk ditampilkan
                view.updateStatistik(model);
            }
        }).execute();
    }
}