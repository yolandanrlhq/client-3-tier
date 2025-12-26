package view;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import model.MenuItem;
import view.konten.*;
import view.menu.PanelMenu;

public class FrameUtama extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelKonten;
    private PanelMenu panelMenu;

    private JLayeredPane layeredPane;
    private JButton btnHamburgerOverlay;

    private PanelDashboard pDashboard;
    private PanelProduk pProduk;
    private PanelPesanan pPesanan;
    private PanelPelanggan pPelanggan;
    private PanelAddPesanan pAddPesanan;

    private boolean isMenuShow = false;

    public FrameUtama() {
        initializeUI();
        setupPanelKonten();
        setupPanelMenu();
        setupFloatingButton();
        addComponents();
        setupResponsiveListener();
        refreshLayout();
    }

    // =========================
    // INIT UI
    // =========================
    private void initializeUI() {
        setTitle("Sistem Penyewaan Kostum");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(600, 500));

        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);
    }

    // =========================
    // HAMBURGER BUTTON
    // =========================
    private void setupFloatingButton() {
        btnHamburgerOverlay = new JButton("≡");
        btnHamburgerOverlay.setFont(new Font("Inter", Font.BOLD, 22));
        btnHamburgerOverlay.setFocusable(false);
        btnHamburgerOverlay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHamburgerOverlay.setBackground(Color.WHITE);
        btnHamburgerOverlay.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        btnHamburgerOverlay.addActionListener(e -> toggleMenu());


        layeredPane.add(btnHamburgerOverlay, JLayeredPane.DRAG_LAYER);

        
        // Gunakan DRAG_LAYER agar selalu di depan konten, tapi di bawah sidebar jika diperlukan
        getLayeredPane().add(btnHamburgerOverlay, JLayeredPane.DRAG_LAYER);
        btnHamburgerOverlay.setBounds(15, 25, 50, 45);
        btnHamburgerOverlay.setVisible(false);
    }

    // =========================
    // PANEL KONTEN
    // =========================
    private void setupPanelKonten() {
        cardLayout = new CardLayout();
        panelKonten = new JPanel(cardLayout);

        // 🔥 KUNCI UTAMA
        panelKonten.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (getWidth() < 900 && isMenuShow) {
                    isMenuShow = false;
                    refreshLayout();
                }
            }
        });

        pDashboard = new PanelDashboard();
        pProduk = new PanelProduk();
        pPesanan = new PanelPesanan();
        pPelanggan = new PanelPelanggan();
        pAddPesanan = new PanelAddPesanan(this);

        panelKonten.add(pDashboard, "dashboard");
        panelKonten.add(pProduk, "produk");
        panelKonten.add(new PanelAddProduk(), "add_produk");
        panelKonten.add(pPesanan, "pesanan");
        panelKonten.add(pAddPesanan, "add_pesanan");
        panelKonten.add(pPelanggan, "pelanggan");
        panelKonten.add(new PanelAddPelanggan(this), "add_pelanggan");
    }

    // =========================
    // PANEL MENU
    // =========================
    private void setupPanelMenu() {
        List<MenuItem> listMenu = new ArrayList<>();

        listMenu.add(new MenuItem("Dashboard", "dashboard"));

        MenuItem menuProduk = new MenuItem("Produk");
        menuProduk.addSubMenuItem(new MenuItem("Daftar Kostum", "produk"));
        menuProduk.addSubMenuItem(new MenuItem("Tambah Kostum", "add_produk"));
        listMenu.add(menuProduk);

        MenuItem menuPesanan = new MenuItem("Pesanan");
        menuPesanan.addSubMenuItem(new MenuItem("Data Pesanan", "pesanan"));
        menuPesanan.addSubMenuItem(new MenuItem("Tambah Pesanan", "add_pesanan"));
        listMenu.add(menuPesanan);

        MenuItem menuPelanggan = new MenuItem("Pelanggan");
        menuPelanggan.addSubMenuItem(new MenuItem("Daftar Pelanggan", "pelanggan"));
        menuPelanggan.addSubMenuItem(new MenuItem("Tambah Pelanggan", "add_pelanggan"));
        listMenu.add(menuPelanggan);

        panelMenu = new PanelMenu(listMenu, cardLayout, panelKonten);
    }

    private void addComponents() {
        layeredPane.add(panelKonten, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(panelMenu, JLayeredPane.POPUP_LAYER);
    }

    // =========================
    // TOGGLE MENU
    // =========================
    public void toggleMenu() {
        isMenuShow = !isMenuShow;
        refreshLayout();
    }

    // =========================
    // RESPONSIVE LAYOUT (FINAL FIX)
    // =========================
    private void refreshLayout() {
        int w = getWidth();
        int h = getHeight();

        panelKonten.setBounds(0, 0, w, h);

        if (w < 900) {
            if (isMenuShow) {
                panelMenu.setBounds(0, 0, 280, h);
                panelMenu.setVisible(true);
                layeredPane.moveToFront(panelMenu);
                btnHamburgerOverlay.setVisible(false);
            } else {
                panelMenu.setVisible(false);
                btnHamburgerOverlay.setVisible(true);
            }
        } else {
            panelMenu.setVisible(true);
            panelMenu.setBounds(0, 0, 280, h);
            panelKonten.setBounds(280, 0, w - 280, h);
            btnHamburgerOverlay.setVisible(false);
            isMenuShow = true;
        }


        btnHamburgerOverlay.setBounds(15, 15, 50, 45);

        layeredPane.revalidate();
        layeredPane.repaint();

        
        btnHamburgerOverlay.putClientProperty("JButton.arc", 15);
        btnHamburgerOverlay.putClientProperty("JButton.focusWidth", 0);
        // Jaga posisi tombol hamburger di pojok kiri atas
        btnHamburgerOverlay.setBounds(15, 30, 40, 40);
        
        // Refresh tampilan secara paksa
        revalidate();
        repaint();

    }

    // =========================
    // RESIZE LISTENER
    // =========================
    private void setupResponsiveListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() < 900) isMenuShow = false;
                refreshLayout();
            }
        });
    }

    // =========================
    // GANTI PANEL
    // =========================
    public void gantiPanel(String key) {
        cardLayout.show(panelKonten, key);

        if (getWidth() < 900) {
            isMenuShow = false;
            refreshLayout();
        }

        switch (key) {
            case "dashboard" -> pDashboard.refreshData();
            case "produk" -> pProduk.refresh();
            case "pesanan" -> pPesanan.loadData("");
            case "add_pesanan" -> pAddPesanan.loadKostumCombo();
            case "pelanggan" -> pPelanggan.loadData();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { com.formdev.flatlaf.FlatLightLaf.setup(); } catch (Exception ignored) {}
            FrameUtama frame = new FrameUtama();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
