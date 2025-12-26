package controller;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Pelanggan;
import view.konten.PanelPelanggan;
import worker.pelanggan.*; 
import java.util.List;

/**
 * PelangganController (Client-Tier)
 * Bertanggung jawab menjembatani UI Java dengan API PHP via Worker.
 */
public class PelangganController {
    private PanelPelanggan view;

    public PelangganController(PanelPelanggan view) {
        this.view = view;
    }

    /**
     * Memuat data dari server dan memperbarui tabel di View.
     */
    public void displayData() {
        new LoadPelangganWorker(list -> {
            // Cek apakah view dan model tabel tersedia
            if (view != null && view.getModel() != null) {
                DefaultTableModel model = view.getModel();
                model.setRowCount(0); // Bersihkan tabel
                
                if (list != null && !list.isEmpty()) {
                    for (Pelanggan p : list) {
                        model.addRow(new Object[]{
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
     * Menyimpan data pelanggan baru.
     * @param callback dijalankan setelah operasi sukses (misal: tutup dialog)
     */
    public void saveData(Pelanggan p, Runnable callback) {
        new SavePelangganWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Pelanggan berhasil disimpan!");
                displayData(); // Refresh data di tabel utama
                if (callback != null) callback.run();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menyimpan data pelanggan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).execute();
    }

    /**
     * Memperbarui data pelanggan yang sudah ada.
     */
    public void updateData(Pelanggan p, Runnable callback) {
        new UpdatePelangganWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Data pelanggan diperbarui!");
                displayData(); 
                if (callback != null) callback.run();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal memperbarui data.");
            }
        }).execute();
    }

    /**
     * Menghapus pelanggan berdasarkan ID.
     */
    public void deleteData(String id) {
        int confirm = JOptionPane.showConfirmDialog(view, 
            "Apakah Anda yakin ingin menghapus pelanggan ID: " + id + "?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new DeletePelangganWorker(id, sukses -> {
                if (sukses) {
                    JOptionPane.showMessageDialog(view, "Pelanggan berhasil dihapus.");
                    displayData(); 
                } else {
                    JOptionPane.showMessageDialog(view, "Gagal menghapus pelanggan.");
                }
            }).execute();
        }
    }
}