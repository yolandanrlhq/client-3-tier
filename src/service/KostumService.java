package service;

import dao.KostumDAO;
import dao.mysql.KostumDAOMySQL;
import model.Kostum;
import java.util.List;

public class KostumService {

    private final KostumDAO dao = new KostumDAOMySQL();

    public List<Kostum> getData(String keyword) {
        return dao.getAll(keyword);
    }

    public void hapus(String id) {
        dao.delete(id);
    }

    public void update(Kostum k) {
        dao.update(k);
    }

    public void simpan(Kostum k) throws Exception {
        dao.insert(k);
    }

    public void hapusKostum(String id) {
        dao.delete(id);
    }

    public void updateKostum(Kostum k) {
        dao.update(k);
    }
}
