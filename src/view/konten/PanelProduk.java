package view.konten;

import controller.ProdukController;
import model.Kostum;
import worker.ProdukLoadWorker;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;

public class PanelProduk extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private MigLayout mainLayout;

    private final ProdukController controller = new ProdukController();

    public PanelProduk() {
        initializeUI();
        refresh(); // load awal TANPA delay

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveTable();
            }
        });
    }

    // =========================
    // UI
    // =========================
    private void initializeUI() {
        mainLayout = new MigLayout("fill, insets 30", "[grow]", "[]20[]20[grow]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Katalog Kostum");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "wrap");

        // ===== TOOLBAR =====
        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow]10[]10[]"));
        toolbar.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.putClientProperty(
                "JTextField.placeholderText",
                "Cari ID / Nama / Kategori..."
        );
        txtSearch.addActionListener(e -> search()); // ENTER

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> search());

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            refresh();
        });

        toolbar.add(txtSearch, "grow");
        toolbar.add(btnSearch, "w 90!");
        toolbar.add(btnRefresh, "w 120!");

        add(toolbar, "growx, wrap");

        // ===== TABLE =====
        String[] columns = {
                "ID", "Nama Kostum", "Kategori", "Stok",
                "Ukuran", "Harga Sewa", "Aksi"
        };

        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // kolom AKSI
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);

        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(new Color(60, 60, 60));

        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));

        table.setRowSorter(new TableRowSorter<>(model));
        table.setDefaultRenderer(Object.class, new ZebraRenderer());

        table.getColumn("Aksi").setCellRenderer(new ActionRenderer());
        table.getColumn("Aksi").setCellEditor(new ActionEditor());
        table.getColumn("Aksi").setMaxWidth(90);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(sp, "grow");
    }

    // =========================
    // API PUBLIK
    // =========================
    public void refresh() {
        loadAsync("");
    }

    private void search() {
        String keyword = txtSearch.getText().trim();

        JDialog loading = createLoadingDialog();
        loading.setVisible(true);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(2000); // delay UX
                return null;
            }

            @Override
            protected void done() {
                loading.dispose();
                loadAsync(keyword);
            }
        }.execute();
    }

    // =========================
    // LOADING DIALOG
    // =========================
    private JDialog createLoadingDialog() {
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);

        JLabel lbl = new JLabel("Memuat data produk...");
        lbl.setFont(new Font("Inter", Font.PLAIN, 14));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(bar, BorderLayout.CENTER);

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Loading",
                Dialog.ModalityType.MODELESS
        );
        dialog.setContentPane(panel);
        dialog.setSize(360, 120);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        return dialog;
    }

    // =========================
    // LOAD DATA
    // =========================
    private void loadAsync(String keyword) {
        model.setRowCount(0);

        ProdukLoadWorker worker = controller.loadAsync(keyword);
        worker.execute();

        worker.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())
                    && evt.getNewValue() == SwingWorker.StateValue.DONE) {
                try {
                    List<Kostum> list = worker.get();
                    for (Kostum k : list) {
                        model.addRow(new Object[]{
                                k.getId(),
                                k.getNama(),
                                k.getKategori(),
                                k.getStok(),
                                k.getUkuran(),
                                "Rp " + String.format("%,.0f", k.getHarga()),
                                "Aksi"
                        });
                    }
                    applyResponsiveTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        });
    }

    // =========================
    // RESPONSIVE
    // =========================
    private void applyResponsiveTable() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w == null) return;

        int width = w.getWidth();
        TableColumnModel tcm = table.getColumnModel();

        if (width <= 768) {
            hideColumn(tcm, 4);
            hideColumn(tcm, 5);
            hideColumn(tcm, 6);
        } else if (width <= 1200) {
            showColumn(tcm, 4, 70);
            hideColumn(tcm, 5);
            showColumn(tcm, 6, 90);
        } else {
            showColumn(tcm, 4, 70);
            showColumn(tcm, 5, 120);
            showColumn(tcm, 6, 100);
        }
    }

    private void hideColumn(TableColumnModel tcm, int index) {
        tcm.getColumn(index).setMinWidth(0);
        tcm.getColumn(index).setMaxWidth(0);
        tcm.getColumn(index).setPreferredWidth(0);
    }

    private void showColumn(TableColumnModel tcm, int index, int width) {
        tcm.getColumn(index).setMinWidth(50);
        tcm.getColumn(index).setMaxWidth(1000);
        tcm.getColumn(index).setPreferredWidth(width);
    }

    // =========================
    // RENDERER
    // =========================
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

    // =========================
    // AKSI (EDIT & HAPUS REAL)
    // =========================
    class ActionRenderer extends JButton implements TableCellRenderer {
        public ActionRenderer() {
            setText("Aksi");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Aksi");

        public ActionEditor() {
            button.addActionListener(e -> {
                int row = table.getEditingRow();
                fireEditingStopped();

                String id = model.getValueAt(row, 0).toString();
                String[] opsi = {"Edit", "Hapus"};

                int pilih = JOptionPane.showOptionDialog(
                        PanelProduk.this,
                        "Pilih aksi untuk kostum " + id,
                        "Aksi Produk",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        opsi,
                        opsi[0]
                );

                if (pilih == 0) editDialog(row);
                if (pilih == 1) hapusData(id);
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    // =========================
    // EDIT & HAPUS
    // =========================
    private void hapusData(String id) {
        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus data?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            controller.hapus(id);
            refresh();
        }
    }

    private void editDialog(int row) {
        JTextField txtNama = new JTextField(model.getValueAt(row, 1).toString());
        JTextField txtStok = new JTextField(model.getValueAt(row, 3).toString());
        JTextField txtHarga = new JTextField(
                model.getValueAt(row, 5).toString()
                        .replace("Rp", "")
                        .replace(",", "")
                        .trim()
        );

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Nama"));
        panel.add(txtNama);
        panel.add(new JLabel("Stok"));
        panel.add(txtStok);
        panel.add(new JLabel("Harga"));
        panel.add(txtHarga);

        int ok = JOptionPane.showConfirmDialog(
                this, panel, "Edit Kostum", JOptionPane.OK_CANCEL_OPTION
        );

        if (ok != JOptionPane.OK_OPTION) return;

        Kostum k = new Kostum();
        k.setId(model.getValueAt(row, 0).toString());
        k.setNama(txtNama.getText());
        k.setStok(Integer.parseInt(txtStok.getText()));
        k.setHarga(Double.parseDouble(txtHarga.getText()));

        controller.update(k);
        refresh();
    }
}
