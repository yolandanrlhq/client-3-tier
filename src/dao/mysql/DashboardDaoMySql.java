package dao.mysql;

import java.sql.*;
import config.DBConfig;
import dao.DashboardDao;
import model.DashboardModel; // Import model baru kamu

public class DashboardDaoMySql implements DashboardDao {
    @Override
    public DashboardModel getStatistics() {
        // Inisialisasi variabel dengan nilai default
        int totalKostum = 0;
        int sedangDisewa = 0;
        double totalPendapatan = 0.0;

        String sql1 = "SELECT COUNT(*) FROM kostum";
        String sql2 = "SELECT COUNT(*) FROM pesanan WHERE status = 'Disewa'";
        String sql3 = "SELECT SUM(total_biaya) FROM pesanan";

        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement()) {
            
            // Eksekusi Query 1
            ResultSet rs1 = st.executeQuery(sql1);
            if (rs1.next()) totalKostum = rs1.getInt(1);

            // Eksekusi Query 2
            ResultSet rs2 = st.executeQuery(sql2);
            if (rs2.next()) sedangDisewa = rs2.getInt(1);

            // Eksekusi Query 3
            ResultSet rs3 = st.executeQuery(sql3);
            if (rs3.next()) totalPendapatan = rs3.getDouble(1);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Kembalikan dalam bentuk Objek Model (Inilah inti 2-Tier yang rapi)
        return new DashboardModel(totalKostum, sedangDisewa, totalPendapatan);
    }
}