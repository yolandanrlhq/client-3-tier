package worker;

import javax.swing.SwingWorker;
import model.Kostum;
import service.KostumService;

public class AddProdukWorker extends SwingWorker<Void, Integer> {

    private final KostumService service;
    private final Kostum kostum;
    private final Runnable onSuccess;

    // ✅ CONSTRUCTOR BARU (dengan callback)
    public AddProdukWorker(KostumService service, Kostum kostum, Runnable onSuccess) {
        this.service = service;
        this.kostum = kostum;
        this.onSuccess = onSuccess;
    }

    // ✅ CONSTRUCTOR LAMA (AGAR TIDAK ERROR)
    public AddProdukWorker(KostumService service, Kostum kostum) {
        this(service, kostum, null);
    }

    @Override
    protected Void doInBackground() throws Exception {
        for (int i = 0; i <= 100; i += 5) {
            Thread.sleep(250);
            setProgress(i);
        }
        service.simpan(kostum);
        return null;
    }

    @Override
    protected void done() {
        if (onSuccess != null) {
            onSuccess.run();
        }
    }
}
