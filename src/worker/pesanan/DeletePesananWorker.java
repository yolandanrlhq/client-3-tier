package worker.pesanan;

import javax.swing.SwingWorker;
import service.PesananService;

public class DeletePesananWorker extends SwingWorker<Boolean, Void> {
    // Ubah dari int ke String agar cocok dengan ID Sewa di DB
    private String id; 
    private PesananService service = new PesananService();
    private java.util.function.Consumer<Boolean> callback;

    // Sesuaikan parameter constructor menjadi String
    public DeletePesananWorker(String id, java.util.function.Consumer<Boolean> callback) {
        this.id = id;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        // Karena service.hapusPesanan sekarang menerima String/int yang sesuai
        // Pastikan service-mu juga menerima tipe yang benar
        return service.hapusPesanan(id);
    }

    @Override
    protected void done() {
        try {
            callback.accept(get());
        } catch (Exception e) {
            e.printStackTrace();
            callback.accept(false);
        }
    }
}