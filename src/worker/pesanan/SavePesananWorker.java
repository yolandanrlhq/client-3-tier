package worker.pesanan;

import javax.swing.SwingWorker;
import model.Pesanan;
import service.PesananService;

public class SavePesananWorker extends SwingWorker<Boolean, Void> {
    private Pesanan pesanan;
    private PesananService service = new PesananService();
    private java.util.function.Consumer<Boolean> callback;

    public SavePesananWorker(Pesanan pesanan, java.util.function.Consumer<Boolean> callback) {
        this.pesanan = pesanan;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return service.simpanPesanan(pesanan);
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