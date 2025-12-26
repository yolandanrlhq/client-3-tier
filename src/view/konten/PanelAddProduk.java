package view.konten;

import controller.ProdukController;
import model.Kostum;
import javax.swing.*;
import java.awt.*;
import net.miginfocom.swing.MigLayout;

public class PanelAddProduk extends JPanel {

    private JTextField txtID, txtNama, txtHarga;
    private JComboBox<String> cbKategori, cbUkuran;
    private JSpinner txtStok;

    private final ProdukController controller = new ProdukController();

    public PanelAddProduk() {
        initializeUI();
    }

    private void initializeUI() {
        // Layout rapi menggunakan MigLayout
        setLayout(new MigLayout(
            "fillx, insets 40, wrap 2",
            "[right,120!]15[grow,fill]"
        ));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Registrasi Kostum Baru");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "span 2, center, gapbottom 20");

        // Form Fields
        add(new JLabel("ID Kostum:"));
        txtID = new JTextField();
        add(txtID);

        add(new JLabel("Nama Kostum:"));
        txtNama = new JTextField();
        add(txtNama);

        add(new JLabel("Kategori:"));
        cbKategori = new JComboBox<>(new String[]{"Anime", "Superhero", "Tradisional", "Game"});
        add(cbKategori);

        add(new JLabel("Jumlah Stok:"));
        txtStok = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        add(txtStok, "w 120!");

        add(new JLabel("Ukuran:"));
        cbUkuran = new JComboBox<>(new String[]{"S", "M", "L", "XL", "All Size"});
        add(cbUkuran);

        add(new JLabel("Harga Sewa:"));
        txtHarga = new JTextField();
        add(txtHarga);

        // Tombol Simpan
        JButton btnSimpan = new JButton("Simpan ke Katalog");
        btnSimpan.setBackground(new Color(131, 188, 160));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setFont(new Font("Inter", Font.BOLD, 14));

        btnSimpan.addActionListener(e -> simpanData(btnSimpan));
        add(btnSimpan, "span 2, center, w 240!, h 45!, gaptop 20");
    }

    private void simpanData(JButton btn) {
        try {
            // Validasi Input Sederhana
            if (txtID.getText().isEmpty() || txtNama.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID dan Nama tidak boleh kosong!");
                return;
            }

            // Map UI ke Model
            Kostum k = new Kostum();
            k.setId(txtID.getText());
            k.setNama(txtNama.getText());
            k.setKategori(cbKategori.getSelectedItem().toString());
            k.setStok((int) txtStok.getValue());
            k.setUkuran(cbUkuran.getSelectedItem().toString());
            k.setHarga(Double.parseDouble(txtHarga.getText()));

            // Setup UI Loading
            btn.setEnabled(false);
            JProgressBar bar = new JProgressBar();
            bar.setIndeterminate(true);
            
            JDialog dialog = new JDialog((Window) SwingUtilities.getWindowAncestor(this), "Memproses...");
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            p.add(new JLabel("Sedang menyimpan data ke server..."), BorderLayout.NORTH);
            p.add(bar, BorderLayout.CENTER);
            
            dialog.setContentPane(p);
            dialog.pack();
            dialog.setLocationRelativeTo(this);

            // Eksekusi via Controller
            controller.simpan(k, sukses -> {
                dialog.dispose();
                btn.setEnabled(true);
                
                if (sukses) {
                    JOptionPane.showMessageDialog(this, "Kostum berhasil didaftarkan!");
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan ke server. Cek koneksi/database.");
                }
            });

            dialog.setVisible(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void resetForm() {
        txtID.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setValue(1);
        cbKategori.setSelectedIndex(0);
        cbUkuran.setSelectedIndex(0);
    }
}