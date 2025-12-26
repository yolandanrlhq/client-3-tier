package worker;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;
import model.Kostum;
import api.ProdukApi; // Menggunakan API, bukan Service

public class ProdukLoadWorker extends SwingWorker<List<Kostum>, Void> {

    private final String keyword;
    private final Consumer<List<Kostum>> onResult;
    private final ProdukApi api;

    // Constructor baru yang menggunakan Callback
    public ProdukLoadWorker(String keyword, Consumer<List<Kostum>> onResult) {
        this.keyword = keyword;
        this.onResult = onResult;
        this.api = new ProdukApi();
    }

    @Override
    protected List<Kostum> doInBackground() throws Exception {
        // Mengambil data dari PHP lewat API
        // Jika keyword kosong, ambil semua. Jika ada, filter di sisi PHP.
        return api.getAll(keyword);
    }

    @Override
    protected void done() {
        try {
            List<Kostum> list = get();
            if (onResult != null) {
                onResult.accept(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onResult != null) onResult.accept(null);
        }
    }
}