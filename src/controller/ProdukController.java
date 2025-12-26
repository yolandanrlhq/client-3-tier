package controller;

import model.Kostum;
import worker.produk.AddProdukWorker;
import worker.produk.ProdukLoadWorker;
import worker.produk.DeleteProdukWorker;
import worker.produk.UpdateProdukWorker;
import java.util.List;
import java.util.function.Consumer;

/**
 * ProdukController (Client-Tier)
 * Bertugas mengelola data Kostum/Produk via Application-Tier (PHP).
 */
public class ProdukController {

    // KostumService DIHAPUS karena logika SQL pindah ke PHP.

    /**
     * Menghapus produk melalui API.
     */
    public void hapus(String id, Consumer<Boolean> callback) {
        // Menggunakan DeleteProdukWorker untuk request DELETE ke PHP
        new DeleteProdukWorker(id, sukses -> {
            if (callback != null) {
                callback.accept(sukses);
            }
        }).execute();
    }

    /**
     * Memperbarui data produk melalui API.
     */
    public void update(Kostum k, Consumer<Boolean> callback) {
        // Menggunakan UpdateProdukWorker untuk request POST/PUT ke PHP
        new UpdateProdukWorker(k, sukses -> {
            if (callback != null) {
                callback.accept(sukses);
            }
        }).execute();
    }

    /**
     * Memuat data secara Asynchronous (GET).
     */
    public void loadData(String keyword, Consumer<List<Kostum>> callback) {
        // ProdukLoadWorker sekarang tidak lagi menerima 'service' di constructor-nya,
        // melainkan akan memanggil folder 'api' di dalamnya.
        new ProdukLoadWorker(keyword, list -> {
            if (callback != null) {
                callback.accept(list);
            }
        }).execute();
    }

    /**
     * Menyimpan data produk baru secara Asynchronous (POST).
     */
    public void simpan(Kostum k, Consumer<Boolean> callback) {
        new AddProdukWorker(k, sukses -> {
            if (callback != null) {
                callback.accept(sukses);
            }
        }).execute();
    }
}