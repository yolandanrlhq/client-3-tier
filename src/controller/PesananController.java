package controller;

import java.util.List;
import javax.swing.JOptionPane;
import model.Pesanan;
import service.PesananService;
import view.konten.PanelPesanan;
import worker.pesanan.*; // Pastikan worker sudah ada di package ini

public class PesananController {
    
    private PanelPesanan view;
    private PesananService service;

    public PesananController(PanelPesanan view) {
        this.view = view;
        this.service = new PesananService();
    }

    // Method ini wajib ada karena dipanggil di PanelPesanan baris 140
    public PesananService getService() {
        return this.service;
    }

    // Mengambil data dari database (Background Thread)
    public void muatData(String keyword) {
        new LoadPesananWorker(keyword, listPesanan -> {
            view.updateTabel(listPesanan);
        }).execute();
    }

    // Update data (Background Thread)
    public void ubahData(Pesanan p) {
        new UpdatePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(view, "Data berhasil diperbarui!");
                muatData(""); // Refresh tabel
            } else {
                JOptionPane.showMessageDialog(view, "Gagal memperbarui data.");
            }
        }).execute();
    }

    // Hapus data berdasarkan ID String (Background Thread)
    public void hapusDataString(String id) {
        int confirm = JOptionPane.showConfirmDialog(view, 
            "Hapus transaksi " + id + "?\nStok kostum akan dikembalikan otomatis.", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new DeletePesananWorker(id, sukses -> {
                if (sukses) {
                    JOptionPane.showMessageDialog(view, "Data dihapus!");
                    muatData(""); // Refresh tabel
                } else {
                    JOptionPane.showMessageDialog(view, "Gagal menghapus data.");
                }
            }).execute();
        }
    }

    // === PesananController.java (FIX FINAL fungsi simpanData) ===
    public void simpanData(Pesanan p, Runnable callback) {

        // JANGAN tampilkan alert di awal
        new SavePesananWorker(p, sukses -> {

            // ALERT MUNCUL SETELAH PROSES DB SELESAI
            if (sukses) {
                JOptionPane.showMessageDialog(
                    null,
                    "Transaksi Berhasil Disimpan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Gagal menyimpan transaksi.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            // CALLBACK (tutup loading, reset form, pindah panel)
            if (callback != null) {
                callback.run();
            }

        }).execute();
    }
}