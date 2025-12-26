package controller;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import model.Pesanan;
import model.Kostum;
import view.konten.PanelPesanan;
import worker.pesanan.*; 
import worker.ProdukLoadWorker; // Pastikan path package benar
import worker.pelanggan.LoadPelangganWorker; 
import java.util.List;

public class PesananController {
    
    private PanelPesanan view;

    public PesananController(PanelPesanan view) {
        this.view = view;
    }

    /**
     * FIX: Tambahkan "" sebagai keyword agar constructor match
     */
    public void isiComboKostum(JComboBox<String> combo, String namaLama) {
        new ProdukLoadWorker("", listKostum -> {
            combo.removeAllItems();
            combo.addItem("-- Pilih Kostum --");
            
            for (model.Kostum k : listKostum) {
                // Kita selipkan info Stok di string agar bisa di-parsing oleh View
                // Format: ID - Nama - Harga - Stok
                String item = k.getId() + " - " + k.getNama() + " - Rp" + (int)k.getHarga() + " - Stok:" + k.getStok();
                combo.addItem(item);
            }
        }).execute();
    }

    /**
     * FIX: Tambahkan "" jika LoadPelangganWorker juga butuh keyword
     */
    public void isiComboPelanggan(JComboBox<String> combo) {
        // HAPUS parameter "" di sini, karena LoadPelangganWorker cuma butuh callback
        new LoadPelangganWorker(listPelanggan -> {
            combo.removeAllItems();
            combo.addItem("-- Pilih Pelanggan --");
            if (listPelanggan != null) {
                for (var p : listPelanggan) {
                    combo.addItem(p.getNama());
                }
            }
        }).execute();
    }

    public void muatData(String keyword) {
        new LoadPesananWorker(keyword, listPesanan -> {
            if (view != null) {
                view.updateTabel(listPesanan);
            }
        }).execute();
    }

    public void ubahData(Pesanan p) {
        new UpdatePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(view, "Data berhasil diperbarui!");
                muatData(""); 
            } else {
                JOptionPane.showMessageDialog(view, "Gagal memperbarui data.");
            }
        }).execute();
    }

    public void hapusDataString(String id) {
        int confirm = JOptionPane.showConfirmDialog(view, 
            "Hapus transaksi " + id + "?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new DeletePesananWorker(id, sukses -> {
                if (sukses) {
                    JOptionPane.showMessageDialog(view, "Data dihapus!");
                    muatData(""); 
                } else {
                    JOptionPane.showMessageDialog(view, "Gagal menghapus data.");
                }
            }).execute();
        }
    }

    public void simpanData(Pesanan p, Runnable callback) {
        new SavePesananWorker(p, sukses -> {
            if (sukses) {
                JOptionPane.showMessageDialog(null, "Transaksi Berhasil!");
                if (callback != null) callback.run();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal simpan data.");
            }
        }).execute();
    }
}