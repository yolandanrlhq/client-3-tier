package worker.pelanggan;

import api.PelangganApi;
import model.Pelanggan;
import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class LoadPelangganWorker extends SwingWorker<List<Pelanggan>, Void> {
    private Consumer<List<Pelanggan>> onResult;

    // Kita tidak lagi mempassing DefaultTableModel ke sini
    // Kita gunakan Consumer (callback) agar data dikelola oleh Controller
    public LoadPelangganWorker(Consumer<List<Pelanggan>> onResult) {
        this.onResult = onResult;
    }

    @Override
    protected List<Pelanggan> doInBackground() throws Exception {
        // Panggil PelangganApi yang menembak PHP
        PelangganApi api = new PelangganApi();
        return api.getAll(); 
    }

    @Override
    protected void done() {
        try {
            // Ambil hasil list dari doInBackground
            List<Pelanggan> list = get();
            // Kirim hasilnya ke callback di Controller
            onResult.accept(list);
        } catch (Exception e) {
            e.printStackTrace();
            onResult.accept(null); // Kirim null jika error
        }
    }
}