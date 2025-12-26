package dao;

import model.Kostum;
import java.util.List;

public interface KostumDAO {
    List<Kostum> getAll(String keyword);
    void delete(String id);
    void update(Kostum k);
    void insert(Kostum k) throws Exception;
}
