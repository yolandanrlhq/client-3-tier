package controller;

import worker.AddProdukWorker;
import worker.ProdukLoadWorker;
import model.Kostum;
import service.KostumService;

public class ProdukController {

    private final KostumService service = new KostumService();

    public void hapus(String id) {
        try {
            service.hapusKostum(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void update(Kostum k) {
        try {
            service.updateKostum(k);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    

    public ProdukLoadWorker loadAsync(String keyword) {
        return new ProdukLoadWorker(service, keyword);
    }

    public AddProdukWorker simpanAsync(Kostum k) {
        return new AddProdukWorker(service, k);
    }
}
