package controller;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import model.Pesanan;
import view.konten.PanelPesanan;
import worker.pesanan.*; 
import worker.kostum.LoadKostumWorker; 
import worker.pelanggan.LoadPelangganWorker; // Pastikan worker ini ada

/**
 * PesananController (Client-Tier)
 * Menangani logika interaksi antara UI dan API melalui Worker.
 */
public class PesananController {
    
    private PanelPesanan view;

    public PesananController(PanelPesanan view) {
        this.view = view;
    }

    /**
     * Mengisi JComboBox Kostum via API.
     * Format item: "ID - Nama - Harga" agar PanelAddPesanan bisa hitung total otomatis.
     */
    public void isiComboKostum(JComboBox<String> combo, String namaLama) {
        new LoadKostumWorker(listKostum -> {
            combo.removeAllItems();
            combo.addItem("-- Pilih Kostum --");
            
            // Jika dalam mode edit, masukkan nilai lama di awal
            if (namaLama != null && !namaLama.isEmpty()) {
                combo.addItem(namaLama);
            }

            for (var k : listKostum) {
                // Tambahkan harga ke dalam string agar UI bisa parsing harga
                String item = k.getId() + " - " + k.getNama() + " - Rp" + (int)k.getHarga();
                if (!k.getNama().equals(namaLama)) {
                    combo.addItem(item);
                }
            }
        }).execute();
    }

    /**
     * Mengisi JComboBox Pelanggan via API.
     */
    public void isiComboPelanggan(JComboBox<String> combo) {
        new LoadPelangganWorker(listPelanggan -> {
            combo.removeAllItems();
            combo.addItem("-- Pilih Pelanggan --");
            for (var p : listPelanggan) {
                combo.addItem(p.getNama());
            }
        }).execute();
    }

    /**
     * Memuat data pesanan ke tabel.
     */
    public void muatData(String keyword) {
        new LoadPesananWorker(keyword, listPesanan -> {
            if (view != null) {
                view.updateTabel(listPesanan);
            }
        }).execute();
    }

    /**
     * Memperbarui data pesanan yang sudah ada.
     */
    public void ubahData(Pesanan p) {
        new UpdatePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(view, "Data berhasil diperbarui!");
                muatData(""); 
            } else {
                JOptionPane.showMessageDialog(view, "Gagal memperbarui data melalui server.");
            }
        }).execute();
    }

    /**
     * Menghapus data transaksi.
     */
    public void hapusDataString(String id) {
        int confirm = JOptionPane.showConfirmDialog(view, 
            "Hapus transaksi " + id + "?\nStok kostum akan dikelola otomatis oleh server.", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new DeletePesananWorker(id, sukses -> {
                if (sukses) {
                    JOptionPane.showMessageDialog(view, "Data dihapus dari server!");
                    muatData(""); 
                } else {
                    JOptionPane.showMessageDialog(view, "Gagal menghapus data.");
                }
            }).execute();
        }
    }

    /**
     * Menyimpan transaksi penyewaan baru.
     */
    public void simpanData(Pesanan p, Runnable callback) {
        new SavePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Transaksi Berhasil Disimpan di Server!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                if (callback != null) callback.run();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal mengirim data ke server.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).execute();
    }
}