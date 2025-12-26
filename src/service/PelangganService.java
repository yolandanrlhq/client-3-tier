package service;

import dao.PelangganDao;
import dao.mysql.PelangganDaoMysql;
import model.Pelanggan;
import java.util.List;

public class PelangganService {
    private PelangganDao dao = new PelangganDaoMysql();

    public List<Pelanggan> findAll() {
        return dao.getAll();
    }

    public void save(Pelanggan p) {
        dao.save(p);
    }

    public void remove(String id) { // PASTIKAN String
        dao.delete(id);
    }
}