package service;

import dao.mysql.DashboardDaoMySql;
import model.DashboardModel; // Import model yang baru dibuat

public class DashboardService {
    // Tetap menggunakan DAO untuk ambil data
    private DashboardDaoMySql dao = new DashboardDaoMySql();
    
    /**
     * Memanggil DAO untuk mendapatkan data statistik yang sudah dibungkus Model
     * @return DashboardModel objek berisi data statistik
     */
    public DashboardModel muatStatistik() {
        // Sekarang mengembalikan objek DashboardModel, bukan Map lagi
        return dao.getStatistics();
    }
}