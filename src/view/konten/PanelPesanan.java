package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;
import model.Pesanan;
import controller.PesananController;

public class PanelPesanan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private PesananController controller;

    public PanelPesanan() {
        this.controller = new PesananController(this);
        initializeUI();
        
        // Load data pertama kali dengan animasi loading
        // searchWithLoading(""); 
        controller.muatData("");

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyTableResponsiveness();
            }
        });
    }

     private void initializeUI() {
        // Layout identik dengan Produk
        setLayout(new MigLayout("fill, insets 30", "[grow]", "[]20[]20[grow]"));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Daftar Transaksi Sewa");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        // ===== TOOLBAR (Gaya Produk) =====
        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow]10[]10[]"));
        toolbar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari ID Sewa / Nama Penyewa / Kostum...");
        txtSearch.addActionListener(e -> searchWithLoading(txtSearch.getText().trim()));

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchWithLoading(txtSearch.getText().trim()));

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            searchWithLoading("");
        });

        toolbar.add(txtSearch, "grow");
        toolbar.add(btnSearch, "w 90!");
        toolbar.add(btnRefresh, "w 120!");
        add(toolbar, "growx, wrap");

        // ===== TABLE (Gaya Produk) =====
        String[] columns = {"ID Sewa", "Penyewa", "Kostum", "Jumlah", "Tgl Pinjam", "Total", "Status", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 7; }
        };

        table = new JTable(model);
        table.setRowHeight(40); // Sesuai Produk

        // Header Style
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(new Color(60, 60, 60));

        // Grid Style (IDENTIK PRODUK)
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));

        table.setRowSorter(new TableRowSorter<>(model));
        table.setDefaultRenderer(Object.class, new ZebraRenderer());

        table.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        table.getColumn("Aksi").setCellEditor(new ActionEditor());
        table.getColumn("Aksi").setMaxWidth(90);

        JScrollPane sp = new JScrollPane(table);
        // Border scrollpane identik produk
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(sp, "grow");
    }

    // ==========================================
    // RENDERER (Update ZebraRenderer agar sama)
    // ==========================================
    class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            setBackground(isSelected
                    ? new Color(200, 220, 255)
                    : (row % 2 == 0 ? Color.WHITE : new Color(245, 248, 250))
            );
            return this;
        }
    }

    // ==========================================
    // MULTITHREADING & LOADING LOGIC
    // ==========================================
    public void searchWithLoading(String keyword) {
        final Window parentWindow = SwingUtilities.getWindowAncestor(this);
        final JDialog loading = createLoadingDialog(parentWindow); 

        // Listener agar dialog "menempel" saat parent window digeser (Ciri Modeless)
        ComponentListener moveListener = new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                loading.setLocationRelativeTo(parentWindow);
            }
        };

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (parentWindow != null) parentWindow.addComponentListener(moveListener);
                
                // Simulasi delay 1.5 detik agar multithreading terlihat jelas
                Thread.sleep(1500); 
                
                // Ambil data via API
                controller.muatData(keyword); 
                return null;
            }

            @Override
            protected void done() {
                if (parentWindow != null) parentWindow.removeComponentListener(moveListener);
                loading.dispose();
                applyTableResponsiveness();
            }
        };

        worker.execute();
        loading.setVisible(true); 
    }

    private JDialog createLoadingDialog(Window parent) {
        // Menggunakan MODELESS sesuai permintaan agar tidak nge-freeze
        JDialog dialog = new JDialog(parent, "Proses", Dialog.ModalityType.MODELESS);
        
        JProgressBar pb = new JProgressBar();
        pb.setIndeterminate(true);
        
        JPanel p = new JPanel(new MigLayout("fill, insets 25"));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        JLabel lbl = new JLabel("Mengambil data server...");
        lbl.setFont(new Font("Inter", Font.PLAIN, 14));
        
        p.add(lbl, "center, wrap 10");
        p.add(pb, "growx, w 220!");
        
        dialog.add(p);
        dialog.setUndecorated(true); 
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }

    public void updateTabel(List<Pesanan> list) {
        model.setRowCount(0);
        for (Pesanan p : list) {
            model.addRow(new Object[]{
                p.getIdSewa(), p.getNamaPenyewa(), p.getNamaKostum(),
                p.getJumlah(), p.getTglPinjam(), 
                "Rp " + String.format("%,.0f", p.getTotalBiaya()),
                p.getStatus(), "Aksi"
            });
        }
    }

    // ==========================================
    // ACTION & EDIT LOGIC
    // ==========================================
    private void editPesanan(int row) {
        try {
            String idSewa = model.getValueAt(row, 0).toString();
            String penyewaLama = model.getValueAt(row, 1).toString();
            String namaKostumLama = model.getValueAt(row, 2).toString();
            int jumlahLama = Integer.parseInt(model.getValueAt(row, 3).toString().replaceAll("[^0-9]", ""));
            String statusLama = model.getValueAt(row, 6).toString();

            JTextField txtPenyewa = new JTextField(penyewaLama);
            JSpinner txtJumlah = new JSpinner(new SpinnerNumberModel(jumlahLama, 1, 100, 1));
            JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Disewa", "Selesai", "Dibatalkan"});
            cbStatus.setSelectedItem(statusLama);

            JComboBox<String> cbKostum = new JComboBox<>();
            cbKostum.addItem(namaKostumLama); 
            
            // Panggil API Kostum via Controller
            controller.isiComboKostum(cbKostum, namaKostumLama);

            JPanel form = new JPanel(new MigLayout("fillx, insets 10", "[right]10[grow, fill]"));
            form.add(new JLabel("Penyewa:"));     form.add(txtPenyewa, "wrap");
            form.add(new JLabel("Ganti Kostum:")); form.add(cbKostum, "wrap");
            form.add(new JLabel("Jumlah Unit:"));  form.add(txtJumlah, "wrap");
            form.add(new JLabel("Status:"));       form.add(cbStatus, "wrap");

            int ok = JOptionPane.showConfirmDialog(this, form, "Edit Transaksi " + idSewa, JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                Pesanan p = new Pesanan();
                p.setIdSewa(idSewa);
                p.setNamaPenyewa(txtPenyewa.getText());
                p.setJumlah((int) txtJumlah.getValue());
                p.setStatus(cbStatus.getSelectedItem().toString());
                
                String selectedK = cbKostum.getSelectedItem().toString();
                if (selectedK.contains(" - ")) {
                    p.setIdKostum(selectedK.split(" - ")[0].trim());
                } else {
                    p.setIdKostum(null); // Server akan handle ID lama jika null
                }
                
                controller.ubahData(p);
                searchWithLoading(""); 
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal: " + e.getMessage());
        }
    }

    private void applyTableResponsiveness() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w == null) return;
        TableColumnModel tcm = table.getColumnModel();
        boolean compact = w.getWidth() <= 900;
        int[] hideIndexes = {3, 4, 5}; 

        for (int idx : hideIndexes) {
            TableColumn col = tcm.getColumn(idx);
            if (compact) {
                col.setMinWidth(0); col.setMaxWidth(0); col.setPreferredWidth(0);
            } else {
                col.setMinWidth(50); col.setMaxWidth(1000); col.setPreferredWidth(100);
            }
        }
    }

    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() { 
            setText("Aksi");
            setBackground(new Color(108, 155, 244));
            setForeground(Color.WHITE);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hF, int r, int c) { return this; }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton btn = new JButton("Aksi");
        public ActionEditor() {
            btn.addActionListener(e -> {
                int row = table.getEditingRow();
                fireEditingStopped();
                showActionMenu(row);
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) { return btn; }
        public Object getCellEditorValue() { return null; }
    }

    private void showActionMenu(int row) {
        String[] options = {"Edit", "Hapus"};
        int pick = JOptionPane.showOptionDialog(this, "Pilih Aksi", "Menu", 0, 
                                           JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (pick == 0) editPesanan(row);
        else if (pick == 1) {
            if (JOptionPane.showConfirmDialog(this, "Hapus data ini?") == 0) {
                controller.hapusDataString(model.getValueAt(row, 0).toString());
                searchWithLoading(""); 
            }
        }
    }

    public PesananController getController() {
        return this.controller;
    }
}