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
    private JLabel title;
    private MigLayout mainLayout;
    private PesananController controller;

    public PanelPesanan() {
        // Inisialisasi controller
        this.controller = new PesananController(this);
        
        initializeUI();
        loadData(""); 

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyTableResponsiveness();
            }
        });
    }

    private void initializeUI() {
        mainLayout = new MigLayout("fill, insets 30", "[grow]", "[]15[]20[grow]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        title = new JLabel("Daftar Transaksi Sewa");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        JPanel toolbar = new JPanel(new MigLayout("insets 0", "[grow]10[]10[]"));
        toolbar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari ID Sewa / Nama Penyewa / Kostum...");
        txtSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));

        JButton btnSearch = new JButton("Cari");
        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setBackground(new Color(245, 245, 245));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData("");
        });

        toolbar.add(txtSearch, "growx, h 35!");
        toolbar.add(btnSearch, "w 80!, h 35!");
        toolbar.add(btnRefresh, "w 120!, h 35!");

        add(toolbar, "growx, wrap");

        String[] columns = {"ID Sewa", "Penyewa", "Kostum", "Jumlah", "Tgl Pinjam", "Total", "Status", "Aksi"};
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        
        table.setDefaultRenderer(Object.class, new ZebraRenderer());
        table.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        table.getColumn("Aksi").setCellEditor(new ActionEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, "grow");
    }

    public void loadData(String keyword) {
        controller.muatData(keyword);
    }

    public void updateTabel(List<Pesanan> listPesanan) {
        model.setRowCount(0);
        for (Pesanan p : listPesanan) {
            model.addRow(new Object[]{
                p.getIdSewa(),
                p.getNamaPenyewa(),
                p.getNamaKostum(),
                p.getJumlah(),
                p.getTglPinjam(),
                "Rp " + String.format("%,.0f", p.getTotalBiaya()),
                p.getStatus(),
                "Aksi"
            });
        }
    }

    private void hapusPesanan(int row) {
        Object idObj = model.getValueAt(row, 0);
        if (idObj != null) {
            controller.hapusDataString(idObj.toString()); 
        }
    }

    private void editPesanan(int row) {
        try {
            // 1. Ambil data dari tabel
            String idSewa = model.getValueAt(row, 0).toString();
            String penyewaLama = model.getValueAt(row, 1).toString();
            String namaKostumLama = model.getValueAt(row, 2).toString();
            
            String jmlRaw = model.getValueAt(row, 3).toString().replaceAll("[^0-9]", "");
            int jumlahLama = Integer.parseInt(jmlRaw);
            String statusLama = model.getValueAt(row, 6).toString();

            // 2. Siapkan Komponen Form
            JTextField txtPenyewa = new JTextField(penyewaLama);
            JSpinner txtJumlah = new JSpinner(new SpinnerNumberModel(jumlahLama, 1, 100, 1));
            JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Disewa", "Selesai", "Dibatalkan"});
            cbStatus.setSelectedItem(statusLama);

            // LOGIKA 3-TIER: Ambil list kostum via Controller (bukan SQL)
            JComboBox<String> cbKostum = new JComboBox<>();
            cbKostum.addItem(namaKostumLama); 

            // Di sini kita panggil controller untuk mengisi dropdown
            // Controller nanti akan memanggil KostumApiClient
            controller.isiComboKostum(cbKostum, namaKostumLama);

            // 3. Tampilkan Dialog
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
                
                // Jika user pilih kostum baru "ID - NAMA"
                if (selectedK.contains(" - ")) {
                    p.setIdKostum(selectedK.split(" - ")[0].trim());
                } else {
                    // Jika tetap kostum lama, ID dikelola di sisi PHP (Server-side)
                    p.setIdKostum(null); 
                }
                
                controller.ubahData(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memproses data: " + e.getMessage());
        }
    }

    // --- Helper UI tetap di sini ---
    private void applyTableResponsiveness() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window == null) return;
        int w = window.getWidth();
        TableColumnModel tcm = table.getColumnModel();
        if (w <= 768) {
            hideColumn(tcm, 3); hideColumn(tcm, 4); hideColumn(tcm, 5);
        } else {
            showColumn(tcm, 3, 70); showColumn(tcm, 4, 130); showColumn(tcm, 5, 120);
        }
        this.revalidate();
    }

    private void hideColumn(TableColumnModel tcm, int index) {
        tcm.getColumn(index).setMinWidth(0);
        tcm.getColumn(index).setMaxWidth(0);
    }

    private void showColumn(TableColumnModel tcm, int index, int width) {
        tcm.getColumn(index).setMinWidth(50);
        tcm.getColumn(index).setMaxWidth(1000);
        tcm.getColumn(index).setPreferredWidth(width);
    }

    class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = table.getValueAt(row, 6).toString();
            if (isSelected) { setBackground(new Color(200, 220, 255)); }
            else {
                if ("Selesai".equalsIgnoreCase(status)) setBackground(new Color(240, 240, 240));
                else setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 250));
            }
            return this;
        }
    }

    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() {
            setText("Aksi");
            setBackground(new Color(108, 155, 244));
            setForeground(Color.WHITE);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Aksi");
        public ActionEditor() {
            button.addActionListener(e -> {
                int row = table.getEditingRow();
                if (row != -1) {
                    String[] opsi = {"Edit", "Hapus"};
                    int pilih = JOptionPane.showOptionDialog(table, "Pilih aksi:", "Menu", 0, JOptionPane.PLAIN_MESSAGE, null, opsi, opsi[0]);
                    if (pilih == 0) editPesanan(row);
                    else if (pilih == 1) hapusPesanan(row);
                }
                fireEditingStopped();
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) { return button; }
        @Override
        public Object getCellEditorValue() { return null; }
    }
}