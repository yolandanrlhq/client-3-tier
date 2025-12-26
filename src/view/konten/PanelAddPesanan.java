package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import view.FrameUtama;
import config.DBConfig;
import model.Pesanan;
import controller.PesananController;

public class PanelAddPesanan extends JPanel {

    private JTextField txtIDSewa, txtTotal;
    private JComboBox<String> cbKostum, cbPenyewa;
    private JSpinner txtJumlah;
    private JButton btnSimpan;
    private double hargaPerUnit = 0;

    private PesananController controller = new PesananController(null);
    private FrameUtama frameUtama;

    private MigLayout mainLayout;
    private JLabel lblTitle;

    public PanelAddPesanan(FrameUtama frame) {
        this.frameUtama = frame;

        mainLayout = new MigLayout("fillx, insets 40", "[right]20[grow, fill]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        setupStaticComponents();
        loadKostumCombo();
        loadPelangganCombo();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshLayout();
            }
        });
    }

    private void setupStaticComponents() {
        lblTitle = new JLabel("Input Penyewaan Baru");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));

        txtIDSewa = new JTextField();
        cbPenyewa = new JComboBox<>();
        cbKostum = new JComboBox<>();
        cbKostum.addActionListener(e -> ambilHargaKostum());

        txtJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        txtJumlah.addChangeListener(e -> hitungTotal());

        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtTotal.setFont(new Font("Inter", Font.BOLD, 16));
        txtTotal.setForeground(new Color(20, 100, 40));
        txtTotal.setBackground(new Color(245, 245, 245));

        btnSimpan = new JButton("Simpan & Sewakan");
        btnSimpan.setBackground(new Color(76, 175, 80));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        btnSimpan.addActionListener(e -> simpanPesananAsync());
    }

    private void simpanPesananAsync() {

        if (cbKostum.getSelectedIndex() <= 0 ||
            cbPenyewa.getSelectedIndex() <= 0 ||
            txtIDSewa.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
            return;
        }

        Pesanan p = new Pesanan();
        p.setIdSewa(txtIDSewa.getText().trim());
        p.setNamaPenyewa(cbPenyewa.getSelectedItem().toString());

        String[] kostum = cbKostum.getSelectedItem().toString().split(" - ");
        p.setIdKostum(kostum[0]);
        p.setNamaKostum(kostum[1]);

        p.setJumlah((int) txtJumlah.getValue());
        p.setTglPinjam(new java.util.Date());
        p.setTotalBiaya(Double.parseDouble(txtTotal.getText()));
        p.setStatus("Disewa");

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JLabel lblInfo = new JLabel("Menyiapkan transaksi...");
        JDialog loadingDialog = createProgressDialog(progressBar, lblInfo);

        btnSimpan.setEnabled(false);
        loadingDialog.setVisible(true);

        SwingWorker<Boolean, Integer> worker = new SwingWorker<>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                publish(10);
                Thread.sleep(500);

                publish(40);
                Thread.sleep(500);

                boolean result = controller.getService().simpanPesanan(p);

                publish(80);
                Thread.sleep(500);

                return result;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int val = chunks.get(chunks.size() - 1);
                progressBar.setValue(val);
                lblInfo.setText("Proses " + val + "%");
            }

            @Override
            protected void done() {
                try {
                    progressBar.setValue(100);
                    lblInfo.setText("Selesai 100%");
                    Thread.sleep(400);

                    if (get()) {
                        JOptionPane.showMessageDialog(PanelAddPesanan.this,
                                "Transaksi berhasil disimpan!");
                        resetForm();
                        if (frameUtama != null) {
                            frameUtama.gantiPanel("pesanan");
                        }
                    } else {
                        JOptionPane.showMessageDialog(PanelAddPesanan.this,
                                "Gagal menyimpan transaksi.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelAddPesanan.this,
                            "Terjadi kesalahan saat menyimpan data.");
                } finally {
                    btnSimpan.setEnabled(true);
                    loadingDialog.dispose();
                }
            }
        };

        worker.execute();
    }

    public void loadPelangganCombo() {
        cbPenyewa.removeAllItems();
        cbPenyewa.addItem("-- Pilih Pelanggan --");
        try (Connection c = DBConfig.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT nama_pelanggan FROM pelanggan")) {

            while (r.next()) cbPenyewa.addItem(r.getString("nama_pelanggan"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadKostumCombo() {
        cbKostum.removeAllItems();
        cbKostum.addItem("-- Pilih Kostum --");
        try (Connection c = DBConfig.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                     "SELECT id_kostum,nama_kostum FROM kostum WHERE stok>0")) {

            while (r.next()) {
                cbKostum.addItem(r.getString(1) + " - " + r.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ambilHargaKostum() {
        if (cbKostum.getSelectedIndex() <= 0) {
            hargaPerUnit = 0;
            hitungTotal();
            return;
        }

        String id = cbKostum.getSelectedItem().toString().split(" - ")[0];
        try (Connection c = DBConfig.getConnection();
             PreparedStatement p =
                     c.prepareStatement("SELECT harga_sewa FROM kostum WHERE id_kostum=?")) {

            p.setString(1, id);
            ResultSet r = p.executeQuery();
            if (r.next()) hargaPerUnit = r.getDouble(1);
            hitungTotal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hitungTotal() {
        int j = (int) txtJumlah.getValue();
        txtTotal.setText(String.valueOf((int) (hargaPerUnit * j)));
    }

    private void resetForm() {
        txtIDSewa.setText("");
        cbPenyewa.setSelectedIndex(0);
        cbKostum.setSelectedIndex(0);
        txtJumlah.setValue(1);
        txtTotal.setText("");
        hargaPerUnit = 0;
    }

    private JDialog createProgressDialog(JProgressBar bar, JLabel label) {
        JDialog d = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Proses Penyimpanan",
                Dialog.ModalityType.MODELESS
        );
        d.setResizable(true);
        d.setLayout(new BorderLayout(10, 10));

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(label, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);

        d.add(p);
        d.setSize(350, 150);
        d.setLocationRelativeTo(this);
        return d;
    }

    private void refreshLayout() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w == null) return;

        removeAll();
        if (w.getWidth() <= 768) {
            mainLayout.setLayoutConstraints("fillx, insets 20");
            add(lblTitle, "span 2, center, wrap 30");
            add(new JLabel("ID Sewa")); add(txtIDSewa, "wrap");
            add(new JLabel("Penyewa")); add(cbPenyewa, "wrap");
            add(new JLabel("Kostum")); add(cbKostum, "wrap");
            add(new JLabel("Jumlah")); add(txtJumlah, "wrap");
            add(new JLabel("Total")); add(txtTotal, "wrap 30");
            add(btnSimpan, "span 2, growx, h 45!");
        } else {
            mainLayout.setLayoutConstraints("fillx, insets 80 50");
            add(lblTitle, "span 2, center, wrap 40");
            add(new JLabel("ID Sewa")); add(txtIDSewa, "wrap");
            add(new JLabel("Nama Pelanggan")); add(cbPenyewa, "wrap");
            add(new JLabel("Pilih Kostum")); add(cbKostum, "wrap");
            add(new JLabel("Jumlah Unit")); add(txtJumlah, "wrap");
            add(new JLabel("Total Biaya")); add(txtTotal, "wrap 30");
            add(btnSimpan, "span 2, center, w 250!, h 50!");
        }

        revalidate();
        repaint();
    }
}
