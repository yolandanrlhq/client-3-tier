package dao;

import java.util.List;
import model.Pesanan;

public interface PesananDao {
    /**
     * Menyimpan transaksi baru, mengurangi stok kostum, 
     * dan mengubah status kostum menjadi 'Disewa' jika stok habis.
     */
    boolean insert(Pesanan p) throws Exception; 
    
    /**
     * Mengambil daftar pesanan menggunakan LEFT JOIN agar data tetap muncul 
     * meskipun status kostum berubah.
     */
    List<Pesanan> findAll(String keyword) throws Exception;
    
    /**
     * Menghapus transaksi dan mengembalikan stok kostum secara otomatis.
     */
    boolean delete(String id) throws Exception;
    
    /**
     * Memperbarui data transaksi dan mengembalikan stok/status kostum 
     * jika status transaksi berubah menjadi 'Selesai' atau 'Dibatalkan'.
     */
    boolean update(Pesanan p) throws Exception;
    
    /**
     * Mengambil ID Kostum berdasarkan ID Sewa. 
     * Sangat penting untuk mencegah ID Kostum menjadi NULL saat proses update.
     */
    String getIdKostumByIdSewa(String idSewa) throws Exception;
    
    /**
     * Mencari detail pesanan berdasarkan ID.
     */
    Pesanan findById(String id) throws Exception;
}