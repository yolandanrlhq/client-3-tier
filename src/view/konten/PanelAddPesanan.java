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
    private int stokTersedia = 0; // Tambahkan kembali variabel stok

    private PesananController controller;
    private FrameUtama frameUtama;
    private MigLayout mainLayout;
    private JLabel lblTitle;

    public PanelAddPesanan(FrameUtama frame) {
        this.frameUtama = frame;
        this.controller = new PesananController(null); 

        mainLayout = new MigLayout("fillx, insets 40", "[right]20[grow, fill]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);

        setupStaticComponents();
        loadDataFromServer(); 

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshLayout();
            }
        });
    }

    public void loadPelangganCombo() {
        controller.isiComboPelanggan(cbPenyewa);
    }

    public void loadKostumCombo() {
        // Keyword kosong untuk ambil semua kostum tersedia
        controller.isiComboKostum(cbKostum, "");
    }

    private void loadDataFromServer() {
        loadPelangganCombo();
        loadKostumCombo();
    }

    private void setupStaticComponents() {
        lblTitle = new JLabel("Input Penyewaan Baru");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));

        txtIDSewa = new JTextField();
        cbPenyewa = new JComboBox<>();
        cbKostum = new JComboBox<>();
        
        // Listener untuk update harga DAN stok saat pilih kostum
        cbKostum.addActionListener(e -> updateInfoKostum());

        // Spinner awal (Default 1)
        txtJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        ((JSpinner.DefaultEditor) txtJumlah.getEditor()).getTextField().setEditable(false);
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

    private void updateInfoKostum() {
        if (cbKostum.getSelectedIndex() <= 0 || cbKostum.getSelectedItem() == null) {
            hargaPerUnit = 0;
            stokTersedia = 0;
            txtJumlah.setModel(new SpinnerNumberModel(0, 0, 0, 0)); // Kunci spinner
            hitungTotal();
            return;
        }
        
        String selected = cbKostum.getSelectedItem().toString();
        try {
            // Format dari Controller: [0]ID - [1]Nama - [2]Harga - [3]Stok:10
            String[] parts = selected.split(" - ");
            if (parts.length >= 4) {
                // Parsing Harga
                String hargaStr = parts[2].replaceAll("[^0-9]", "");
                hargaPerUnit = Double.parseDouble(hargaStr);
                
                // Parsing Stok (Ambil angka dari "Stok:10")
                String stokStr = parts[3].replaceAll("[^0-9]", "");
                stokTersedia = Integer.parseInt(stokStr);

                // VALIDASI: Jika stok 0, tampilkan peringatan
                if (stokTersedia <= 0) {
                    JOptionPane.showMessageDialog(this, "Maaf, stok kostum ini sedang habis!");
                    txtJumlah.setModel(new SpinnerNumberModel(0, 0, 0, 0));
                } else {
                    // Batasi JSpinner: Minimal 1, Maksimal stokTersedia
                    txtJumlah.setModel(new SpinnerNumberModel(1, 1, stokTersedia, 1));
                }
            }
        } catch (Exception e) {
            hargaPerUnit = 0;
            stokTersedia = 0;
        }
        hitungTotal();
        refreshLayout(); // Panggil agar label "Max: Stok" di UI terupdate
    }

    private void hitungTotal() {
        int j = (int) txtJumlah.getValue();
        txtTotal.setText(String.format("%.0f", hargaPerUnit * j));
    }

    private void aksiSimpan() {
        if (cbKostum.getSelectedIndex() <= 0 || cbPenyewa.getSelectedIndex() <= 0 || txtIDSewa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data!");
            return;
        }

        Pesanan p = new Pesanan();
        p.setIdSewa(txtIDSewa.getText().trim());
        p.setNamaPenyewa(cbPenyewa.getSelectedItem().toString());

        String[] kostumData = cbKostum.getSelectedItem().toString().split(" - ");
        p.setIdKostum(kostumData[0]);
        // Nama kostum diambil dari part kedua
        p.setNamaKostum(kostumData[1]);

        p.setJumlah((int) txtJumlah.getValue());
        p.setTglPinjam(new Date());
        p.setTotalBiaya(Double.parseDouble(txtTotal.getText()));
        p.setStatus("Disewa");

        btnSimpan.setEnabled(false);
        
        // Menggunakan method simpanData dari controller (3-Tier Worker)
        controller.simpanData(p, () -> {
            btnSimpan.setEnabled(true);
            resetForm();
            if (frameUtama != null) {
                frameUtama.gantiPanel("pesanan");
            }
        });
    }

    private void resetForm() {
        txtIDSewa.setText("");
        if (cbPenyewa.getItemCount() > 0) cbPenyewa.setSelectedIndex(0);
        if (cbKostum.getItemCount() > 0) cbKostum.setSelectedIndex(0);
        txtJumlah.setModel(new SpinnerNumberModel(1, 1, 1, 1));
        txtTotal.setText("");
        hargaPerUnit = 0;
        stokTersedia = 0;
    }

    private void refreshLayout() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w == null) return;

        removeAll();
        boolean isMobile = w.getWidth() <= 900;
        
        mainLayout.setLayoutConstraints(isMobile ? "fillx, insets 20" : "fillx, insets 60 15% 60 15%");

        add(lblTitle, "span 2, center, wrap 40");
        add(new JLabel("ID Sewa")); add(txtIDSewa, "wrap");
        add(new JLabel("Nama Pelanggan")); add(cbPenyewa, "wrap");
        add(new JLabel("Pilih Kostum")); add(cbKostum, "wrap");
        add(new JLabel("Jumlah Unit (Stok: " + stokTersedia + ")")); add(txtJumlah, "wrap");
        add(new JLabel("Total Biaya")); add(txtTotal, "wrap 30");
        
        String btnSize = isMobile ? "span 2, growx, h 45!" : "span 2, center, w 250!, h 50!";
        add(btnSimpan, btnSize);

        revalidate();
        repaint();
    }
}