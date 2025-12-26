package controller;

import view.konten.PanelDashboard;
import worker.dashboard.LoadDashboardWorker;

/**
 * DashboardController (Client-Tier)
 * Tugas: Mengatur navigasi data antara View dan API (via Worker)
 */
public class DashboardController {
    private PanelDashboard view;

    /**
     * Constructor hanya membutuhkan View.
     * DashboardService dihapus dari sini karena Service sekarang berada di Application-tier (PHP).
     */
    public DashboardController(PanelDashboard view) {
        this.view = view;
    }

    /**
     * Mengatur alur pemuatan data dashboard secara Asynchronous.
     * Memanggil API PHP melalui LoadDashboardWorker.
     */
    public void muatDataDashboard() {
        // Tampilkan loading state jika perlu (opsional)
        // view.setLoading(true);

        // Controller memerintahkan Worker untuk melakukan HTTP Request ke PHP
        new LoadDashboardWorker(model -> {
            if (model != null) {
                // Jika data berhasil didapat dari API PHP, update tampilan
                view.updateStatistik(model);
            } else {
                // Logika jika gagal (bisa tampilkan pesan error di UI)
                System.err.println("DashboardController: Gagal mengambil data dari Application-tier.");
            }
        }).execute();
    }
}