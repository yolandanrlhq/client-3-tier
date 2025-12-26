package view.konten;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import view.FrameUtama;
import model.Pesanan;
import controller.PesananController;

public class PanelAddPesanan extends JPanel {

    private JTextField txtIDSewa, txtTotal;
    private JComboBox<String> cbKostum, cbPenyewa;
    private JSpinner txtJumlah;
    private JButton btnSimpan;
    private double hargaPerUnit = 0;

    // View memanggil Controller, Controller memanggil Worker
    private PesananController controller;
    private FrameUtama frameUtama;

    private MigLayout mainLayout;
    private JLabel lblTitle;

    public PanelAddPesanan(FrameUtama frame) {
        this.frameUtama = frame;
        this.controller = new PesananController(null); // View di set null karena ini panel input

        mainLayout = new MigLayout("fillx, insets 40", "[right]20[grow, fill]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        setupStaticComponents();
        
        // Memuat data melalui Controller (API-based)
        loadDataFromServer();

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
        
        // Listener untuk update harga saat pilih kostum
        cbKostum.addActionListener(e -> updateHargaOtomatis());

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
        btnSimpan.addActionListener(e -> aksiSimpan());
    }

    private void loadDataFromServer() {
        // Panggil controller untuk mengisi combo via API
        controller.isiComboPelanggan(cbPenyewa);
        controller.isiComboKostum(cbKostum, "");
    }

    private void updateHargaOtomatis() {
        if (cbKostum.getSelectedIndex() <= 0) {
            hargaPerUnit = 0;
            hitungTotal();
            return;
        }
        
        // Logika: Ambil harga dari string combo atau minta ke controller
        // Misal format combo: "ID - Nama - Harga"
        String selected = cbKostum.getSelectedItem().toString();
        try {
            String[] parts = selected.split(" - ");
            if (parts.length >= 3) {
                hargaPerUnit = Double.parseDouble(parts[2].replace("Rp", "").replace(".", "").trim());
            }
        } catch (Exception e) {
            hargaPerUnit = 0;
        }
        hitungTotal();
    }

    private void hitungTotal() {
        int j = (int) txtJumlah.getValue();
        txtTotal.setText(String.valueOf((int) (hargaPerUnit * j)));
    }

    private void aksiSimpan() {
        // 1. Validasi Input
        if (cbKostum.getSelectedIndex() <= 0 || cbPenyewa.getSelectedIndex() <= 0 || txtIDSewa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
            return;
        }

        // 2. Siapkan Objek Model
        Pesanan p = new Pesanan();
        p.setIdSewa(txtIDSewa.getText().trim());
        p.setNamaPenyewa(cbPenyewa.getSelectedItem().toString());

        String[] kostumData = cbKostum.getSelectedItem().toString().split(" - ");
        p.setIdKostum(kostumData[0]);
        p.setNamaKostum(kostumData[1]);

        p.setJumlah((int) txtJumlah.getValue());
        p.setTglPinjam(new Date());
        p.setTotalBiaya(Double.parseDouble(txtTotal.getText()));
        p.setStatus("Disewa");

        // 3. Jalankan melalui Controller (3-Tier Way)
        btnSimpan.setEnabled(false);
        controller.simpanData(p, () -> {
            // Callback setelah berhasil simpan
            btnSimpan.setEnabled(true);
            resetForm();
            if (frameUtama != null) {
                frameUtama.gantiPanel("pesanan"); // Pindah ke daftar transaksi
            }
        });
    }

    private void resetForm() {
        txtIDSewa.setText("");
        cbPenyewa.setSelectedIndex(0);
        cbKostum.setSelectedIndex(0);
        txtJumlah.setValue(1);
        txtTotal.setText("");
        hargaPerUnit = 0;
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