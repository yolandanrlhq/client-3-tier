package service;

import dao.PesananDao;
import dao.mysql.PesananDaoMySql;
import model.Pesanan;
import java.util.List;

public class PesananService {

    private PesananDao dao = new PesananDaoMySql();

    public List<Pesanan> muatSemuaData(String keyword) throws Exception {
        return dao.findAll(keyword);
    }

    public boolean simpanPesanan(Pesanan p) throws Exception {
        return dao.insert(p);
    }

    public boolean ubahPesanan(Pesanan p) throws Exception {

        // FIX: jaga data lama agar tidak ter-reset (total jadi 0)
        Pesanan dataLama = dao.findById(p.getIdSewa());
        if (dataLama != null) {
            if (p.getTotalBiaya() <= 0) {
                p.setTotalBiaya(dataLama.getTotalBiaya());
            }
            if (p.getJumlah() <= 0) {
                p.setJumlah(dataLama.getJumlah());
            }
            if (p.getIdKostum() == null) {
                p.setIdKostum(dataLama.getIdKostum());
            }
            if (p.getNamaKostum() == null) {
                p.setNamaKostum(dataLama.getNamaKostum());
            }
            if (p.getNamaPenyewa() == null) {
                p.setNamaPenyewa(dataLama.getNamaPenyewa());
            }
            if (p.getTglPinjam() == null) {
                p.setTglPinjam(dataLama.getTglPinjam());
            }
        }

        return dao.update(p);
    }

    // Dipakai PanelPesanan
    public String getIdKostum(String idSewa) throws Exception {
        return dao.getIdKostumByIdSewa(idSewa);
    }

    public boolean hapusPesanan(String id) throws Exception {
        // FIX: jangan gagalkan hapus karena validasi status
        // Validasi stok sudah ditangani di DAO
        return dao.delete(id);
    }
}
