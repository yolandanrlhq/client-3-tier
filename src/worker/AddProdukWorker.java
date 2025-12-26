package worker;

import javax.swing.SwingWorker;
import model.Kostum;
import api.ProdukApi; // Ganti Service dengan API
import java.util.function.Consumer;

public class AddProdukWorker extends SwingWorker<Boolean, Integer> {

    private final ProdukApi api; // Gunakan API
    private final Kostum kostum;
    private final Consumer<Boolean> onResult; // Gunakan Consumer agar tahu sukses/gagal

    public AddProdukWorker(Kostum kostum, Consumer<Boolean> onResult) {
        this.api = new ProdukApi();
        this.kostum = kostum;
        this.onResult = onResult;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        // Simulasi progress bar (opsional)
        for (int i = 0; i <= 100; i += 20) {
            Thread.sleep(100); 
            setProgress(i);
        }
        
        // Panggil API untuk simpan ke PHP
        return api.save(kostum);
    }

    @Override
    protected void done() {
        try {
            boolean sukses = get();
            if (onResult != null) {
                onResult.accept(sukses);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onResult != null) onResult.accept(false);
        }
    }
}