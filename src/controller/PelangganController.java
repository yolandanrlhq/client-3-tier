package controller;

import model.Pelanggan;
import view.konten.PanelPelanggan;
import worker.pelanggan.*; // Pastikan semua worker sudah dibuat di package ini

/**
 * PelangganController (Client-Tier)
 * Menghubungkan View dengan Business Logic di PHP melalui Worker.
 */
public class PelangganController {
    private PanelPelanggan view;

    /**
     * Constructor: PelangganService dihapus karena logika SQL pindah ke PHP.
     */
    public PelangganController(PanelPelanggan view) {
        this.view = view;
    }

    /**
     * Menampilkan data ke tabel dengan mengambil data dari API PHP.
     */
    public void displayData() {
        // Menggunakan LoadPelangganWorker untuk mengambil data JSON dari PHP
        new LoadPelangganWorker(list -> {
            if (view != null) {
                view.getModel().setRowCount(0);
                if (list != null) {
                    for (Pelanggan p : list) {
                        view.getModel().addRow(new Object[]{
                            p.getId(), 
                            p.getNama(), 
                            p.getNoWa(), 
                            p.getAlamat(), 
                            "Aksi"
                        });
                    }
                }
            }
        }).execute();
    }

    /**
     * Mengirim data pelanggan baru ke API PHP.
     */
    public void saveData(Pelanggan p, Runnable callback) {
        // Menggunakan SavePelangganWorker (menggantikan Thread manual)
        new SavePelangganWorker(p, sukses -> {
            if (sukses) {
                displayData(); // Refresh tabel setelah simpan
                if (callback != null) callback.run();
            }
        }).execute();
    }

    /**
     * Memperbarui data pelanggan melalui API PHP.
     */
    public void updateData(Pelanggan p, Runnable callback) {
        // Menggunakan UpdatePelangganWorker
        new UpdatePelangganWorker(p, sukses -> {
            if (sukses) {
                displayData(); // Refresh tabel
                if (callback != null) callback.run();
            }
        }).execute();
    }

    /**
     * Menghapus data pelanggan berdasarkan ID melalui API PHP.
     */
    public void deleteData(String id) {
        // Menggunakan DeletePelangganWorker
        new DeletePelangganWorker(id, sukses -> {
            if (sukses) {
                displayData(); // Refresh tabel setelah hapus
            }
        }).execute();
    }
}