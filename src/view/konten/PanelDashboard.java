package view.konten;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import model.DashboardModel; // Import model
import controller.DashboardController; // Import controller

public class PanelDashboard extends JPanel {

    private JLabel lblTotalKostum, lblSedangDisewa, lblTotalPendapatan;
    private JPanel cardContainer;
    private JLabel title;
    private MigLayout mainLayout, containerLayout;
    
    // Inisialisasi Controller
    private DashboardController controller;

    public PanelDashboard() {
        mainLayout = new MigLayout("fillx, insets 40", "[grow]", "[]30[]");
        setLayout(mainLayout);
        setBackground(Color.WHITE);
        setMinimumSize(new Dimension(600, 400));

        // Buat objek controller
        this.controller = new DashboardController(this);

        initializeUI();
        refreshData(); // Panggil data pertama kali

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustResponsiveness();
            }
        });
    }

    private void initializeUI() {
        title = new JLabel("Statistik Rental");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(0, 48, 73));
        add(title, "wrap");

        containerLayout = new MigLayout("fillx, gap 25, insets 0", "[grow][grow][grow]");
        cardContainer = new JPanel(containerLayout);
        cardContainer.setOpaque(false);

        lblTotalKostum = new JLabel("0");
        lblSedangDisewa = new JLabel("0");
        lblTotalPendapatan = new JLabel("0");

        add(cardContainer, "growx");
    }

    // Tugas refreshData sekarang cuma perintah ke Controller
    public void refreshData() {
        controller.muatDataDashboard();
    }

    /**
     * Method ini akan dipanggil oleh Controller saat data sudah siap.
     * Tidak ada lagi Map, sekarang pakai DashboardModel.
     */
    public void updateStatistik(DashboardModel model) {
        if (model != null) {
            lblTotalKostum.setText(String.valueOf(model.getTotalKostum()));
            lblSedangDisewa.setText(String.valueOf(model.getSedangDisewa()));
            lblTotalPendapatan.setText(String.format("%,.0f", model.getTotalPendapatan()));
        }
    }

    private void adjustResponsiveness() {
        int w = getWidth();
        if (w <= 0) return;

        cardContainer.removeAll();
        if (w <= 768) {
            containerLayout.setColumnConstraints("[grow]");
            renderCardsStacked();
        } else if (w <= 1100) {
            containerLayout.setColumnConstraints("[grow][grow]");
            cardContainer.add(createStatCard("Total Kostum", lblTotalKostum, new Color(234, 242, 235), new Color(131, 188, 160)), "grow");
            cardContainer.add(createStatCard("Sedang Disewa", lblSedangDisewa, new Color(235, 241, 253), new Color(108, 155, 244)), "grow, wrap");
            cardContainer.add(createStatCard("Total Pendapatan (Rp)", lblTotalPendapatan, new Color(255, 250, 235), new Color(255, 193, 7)), "grow, span 2");
        } else {
            containerLayout.setColumnConstraints("[grow][grow][grow]");
            renderCardsNormal();
        }
        revalidate();
        repaint();
    }

    private void renderCardsNormal() {
        cardContainer.add(createStatCard("Total Kostum", lblTotalKostum, new Color(234, 242, 235), new Color(131, 188, 160)), "grow");
        cardContainer.add(createStatCard("Sedang Disewa", lblSedangDisewa, new Color(235, 241, 253), new Color(108, 155, 244)), "grow");
        cardContainer.add(createStatCard("Total Pendapatan (Rp)", lblTotalPendapatan, new Color(255, 250, 235), new Color(255, 193, 7)), "grow");
    }

    private void renderCardsStacked() {
        cardContainer.add(createStatCard("Total Kostum", lblTotalKostum, new Color(234, 242, 235), new Color(131, 188, 160)), "grow, wrap 15");
        cardContainer.add(createStatCard("Sedang Disewa", lblSedangDisewa, new Color(235, 241, 253), new Color(108, 155, 244)), "grow, wrap 15");
        cardContainer.add(createStatCard("Total Pendapatan (Rp)", lblTotalPendapatan, new Color(255, 250, 235), new Color(255, 193, 7)), "grow");
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor, Color accentColor) {
        JPanel card = new JPanel(new MigLayout("insets 20", "[]", "[]10[]"));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createLineBorder(accentColor, 2));
        valueLabel.setFont(new Font("Inter", Font.BOLD, 32));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 14));
        card.add(valueLabel, "wrap");
        card.add(lblTitle);
        return card;
    }
}