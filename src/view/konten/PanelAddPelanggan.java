package view.konten;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import view.FrameUtama;
import model.Pelanggan;
import controller.PelangganController;

public class PanelAddPelanggan extends JPanel {
    private JTextField txtNama, txtNoWa;
    private JTextArea txtAlamat;
    private JButton btnSimpan, btnBatal;
    private PelangganController controller;
    private FrameUtama frame;
    private MigLayout layout;
    private JLabel lblTitle;

    public PanelAddPelanggan(FrameUtama frame) {
        this.frame = frame;
        // Inisialisasi controller (null karena tidak mengontrol tabel di sini)
        this.controller = new PelangganController(null);
        this.layout = new MigLayout("fillx, insets 40", "[right]20[grow, fill]");
        
        setLayout(layout);
        setBackground(Color.WHITE);
        initUI();
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { refreshLayout(); }
        });
    }

    private void initUI() {
        lblTitle = new JLabel("Tambah Pelanggan Baru");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));

        txtNama = new JTextField();
        txtNama.setFont(new Font("Inter", Font.PLAIN, 14));

        txtNoWa = new JTextField();
        txtNoWa.setFont(new Font("Inter", Font.PLAIN, 14));

        txtAlamat = new JTextArea(4, 20);
        txtAlamat.setFont(new Font("Inter", Font.PLAIN, 14));
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        
        btnSimpan = new JButton("Simpan Pelanggan");
        btnSimpan.setBackground(new Color(76, 175, 80));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> simpan());

        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Inter", Font.PLAIN, 14));
        btnBatal.addActionListener(e -> frame.gantiPanel("pelanggan"));
    }

    private void simpan() {
        String nama = txtNama.getText().trim();
        String wa = txtNoWa.getText().trim();
        String alamat = txtAlamat.getText().trim();

        if (nama.isEmpty() || wa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan WhatsApp tidak boleh kosong!");
            return;
        }

        // PERBAIKAN: ID dikirim "" karena akan di-generate otomatis oleh PelangganDaoMysql
        Pelanggan p = new Pelanggan("", nama, wa, alamat);
        
        btnSimpan.setEnabled(false);
        
        // Progress Dialog Sederhana
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        JDialog loading = new JDialog(SwingUtilities.getWindowAncestor(this), "Proses...", Dialog.ModalityType.APPLICATION_MODAL);
        loading.add(bar);
        loading.setSize(200, 50);
        loading.setLocationRelativeTo(this);

        controller.saveData(p, () -> {
            loading.dispose();
            btnSimpan.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Pelanggan Berhasil Ditambahkan!");
            reset();
            frame.gantiPanel("pelanggan");
        });

        loading.setVisible(true);
    }

    private void reset() { 
        txtNama.setText(""); 
        txtNoWa.setText(""); 
        txtAlamat.setText(""); 
    }

    private void refreshLayout() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window == null) return;
        int w = window.getWidth();
        removeAll();

        if (w <= 768) {
            // MODE RESPONSIF (MOBILE/KECIL)
            layout.setLayoutConstraints("fillx, insets 20");
            add(lblTitle, "span 2, center, wrap 30");
            add(new JLabel("Nama:"), "wrap 5");
            add(txtNama, "growx, wrap 15");
            add(new JLabel("WhatsApp:"), "wrap 5");
            add(txtNoWa, "growx, wrap 15");
            add(new JLabel("Alamat:"), "wrap 5");
            add(new JScrollPane(txtAlamat), "growx, h 100!, wrap 30");
            add(btnSimpan, "span 2, growx, h 45!, wrap 10");
            add(btnBatal, "span 2, growx, h 40!");
        } else {
            // MODE DESKTOP
            layout.setLayoutConstraints("fillx, insets 80 150 80 150");
            add(lblTitle, "span 2, center, wrap 40");
            add(new JLabel("Nama Lengkap:")); add(txtNama, "h 35!, wrap 15");
            add(new JLabel("No WhatsApp:")); add(txtNoWa, "h 35!, wrap 15");
            add(new JLabel("Alamat Lengkap:")); add(new JScrollPane(txtAlamat), "h 100!, wrap 30");
            add(btnBatal, "split 2, skip 1, w 100!, h 45!");
            add(btnSimpan, "w 200!, h 45!");
        }
        revalidate(); repaint();
    }
}