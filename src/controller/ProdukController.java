package controller;

import model.Kostum;
import worker.AddProdukWorker;
import worker.ProdukLoadWorker;
import api.ProdukApi;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

public class ProdukController {

    // 1. LOAD DATA (Menggunakan ProdukLoadWorker)
    public void loadData(String keyword, Consumer<List<Kostum>> callback) {
        // Keyword dikirim ke worker, hasil list dikirim ke callback (view)
        new ProdukLoadWorker(keyword, list -> {
            if (callback != null) {
                callback.accept(list);
            }
        }).execute();
    }

    // 2. SIMPAN DATA BARU (Menggunakan AddProdukWorker)
    public void simpan(Kostum k, Consumer<Boolean> callback) {
        new AddProdukWorker(k, sukses -> {
            if (callback != null) {
                callback.accept(sukses);
            }
        }).execute();
    }

    // 3. UPDATE DATA (Juga menggunakan AddProdukWorker)
    // Karena PHP menggunakan 'ON DUPLICATE KEY UPDATE', logic-nya sama dengan simpan
    public void update(Kostum k, Consumer<Boolean> callback) {
        new AddProdukWorker(k, sukses -> {
            if (callback != null) {
                callback.accept(sukses);
            }
        }).execute();
    }

    // 4. HAPUS DATA (Inline Worker agar tidak perlu file tambahan)
    public void hapus(String id, Consumer<Boolean> callback) {
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Langsung panggil API delete
                return new ProdukApi().delete(id);
            }

            @Override
            protected void done() {
                try {
                    boolean sukses = get();
                    if (callback != null) callback.accept(sukses);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) callback.accept(false);
                }
            }
        }.execute();
    }
}