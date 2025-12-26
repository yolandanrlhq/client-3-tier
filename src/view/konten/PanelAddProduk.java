package view.konten;

import controller.ProdukController;
import model.Kostum;
import worker.AddProdukWorker;

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
        setLayout(new MigLayout(
            "fillx, insets 40, wrap 2",
            "[right,120!]15[grow,fill]"
        ));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Registrasi Kostum Baru");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        add(title, "span 2, center, wrap");

        add(new JLabel("ID Kostum:"));
        txtID = new JTextField();
        add(txtID, "growx");

        add(new JLabel("Nama Kostum:"));
        txtNama = new JTextField();
        add(txtNama, "growx");

        add(new JLabel("Kategori:"));
        cbKategori = new JComboBox<>(new String[]{
            "Anime", "Superhero", "Tradisional", "Game"
        });
        add(cbKategori, "growx");

        add(new JLabel("Jumlah Stok:"));
        txtStok = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        add(txtStok, "w 120!");

        add(new JLabel("Ukuran:"));
        cbUkuran = new JComboBox<>(new String[]{
            "S", "M", "L", "XL", "All Size"
        });
        add(cbUkuran, "growx");

        add(new JLabel("Harga Sewa:"));
        txtHarga = new JTextField();
        add(txtHarga, "growx");

        JButton btnSimpan = new JButton("Simpan ke Katalog");
        btnSimpan.setBackground(new Color(131,188,160));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);

        btnSimpan.addActionListener(e -> simpanAsync(btnSimpan));
        add(btnSimpan, "span 2, center, w 240!, h 45!");
    }

    private void simpanAsync(JButton btn) {

        Kostum k = new Kostum();
        k.setId(txtID.getText());
        k.setNama(txtNama.getText());
        k.setKategori(cbKategori.getSelectedItem().toString());
        k.setStok((int) txtStok.getValue());
        k.setUkuran(cbUkuran.getSelectedItem().toString());
        k.setHarga(Double.parseDouble(txtHarga.getText()));

       

        // ===== SATU-SATUNYA deklarasi bar =====
JProgressBar bar = new JProgressBar(0, 100);
bar.setStringPainted(true);
bar.setPreferredSize(new Dimension(300, 22));

// ===== LABEL =====
JLabel lblInfo = new JLabel("Menyimpan data kostum...");
lblInfo.setFont(new Font("Inter", Font.PLAIN, 14));

// ===== PANEL KONTEN =====
JPanel content = new JPanel(new BorderLayout(10, 10));
content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
content.add(lblInfo, BorderLayout.NORTH);
content.add(bar, BorderLayout.CENTER);

// ===== DIALOG =====
JDialog dialog = new JDialog(
    SwingUtilities.getWindowAncestor(this),
    "Memproses...",
    Dialog.ModalityType.MODELESS
);
dialog.setContentPane(content);
dialog.pack();
dialog.setMinimumSize(new Dimension(360, 120));
dialog.setLocationRelativeTo(this);
dialog.setResizable(false);

        dialog.add(bar);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        AddProdukWorker worker = controller.simpanAsync(k);

        // progress
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                bar.setValue((Integer) evt.getNewValue());
            }

            // 🔥 INI PENGGANTI addDoneListener
            if ("state".equals(evt.getPropertyName())
                    && evt.getNewValue() == SwingWorker.StateValue.DONE) {

                dialog.dispose();
                btn.setEnabled(true);

                try {
                    worker.get(); // tangkap exception jika ada
                    JOptionPane.showMessageDialog(
                        PanelAddProduk.this,
                        "Kostum berhasil didaftarkan!"
                    );
                    resetForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        PanelAddProduk.this,
                        "Gagal menyimpan: " + ex.getMessage()
                    );
                }
            }
        });

        btn.setEnabled(false);
        worker.execute();
        dialog.setVisible(true);
    }

    private void resetForm() {
        txtID.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setValue(1);
    }
}
