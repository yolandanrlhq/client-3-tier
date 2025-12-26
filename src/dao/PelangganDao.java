package dao;

import java.util.List;
import model.Pelanggan;

public interface PelangganDao {
    List<Pelanggan> getAll();
    void save(Pelanggan p);
    void delete(String id); // PASTIKAN String
}