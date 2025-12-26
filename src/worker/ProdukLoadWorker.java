package worker;

import javax.swing.*;
import java.util.List;

import model.Kostum;
import service.KostumService;

public class ProdukLoadWorker extends SwingWorker<List<Kostum>, Void> {

    private final KostumService service;
    private final String keyword;

    public ProdukLoadWorker(KostumService service, String keyword) {
        this.service = service;
        this.keyword = keyword;
    }

    @Override
    protected List<Kostum> doInBackground() {
        return service.getData(keyword);
    }
}
