package worker.pelanggan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.sql.*;
import config.DBConfig;

public class LoadPelangganWorker extends SwingWorker<List<Object[]>, Void> {
    private DefaultTableModel model;

    public LoadPelangganWorker(DefaultTableModel model) {
        this.model = model;
    }

    @Override
    protected List<Object[]> doInBackground() throws Exception {
        List<Object[]> data = new ArrayList<>();
        Connection conn = DBConfig.getConnection();
        String sql = "SELECT id_pelanggan, nama_pelanggan, no_wa, alamat FROM pelanggan ORDER BY id_pelanggan DESC";
        ResultSet rs = conn.createStatement().executeQuery(sql);

        while (rs.next()) {
            data.add(new Object[]{
                rs.getInt("id_pelanggan"),
                rs.getString("nama_pelanggan"),
                rs.getString("no_wa"),
                rs.getString("alamat"),
                "Aksi"
            });
        }
        return data;
    }

    @Override
    protected void done() {
        try {
            List<Object[]> result = get();
            model.setRowCount(0);
            for (Object[] row : result) {
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}