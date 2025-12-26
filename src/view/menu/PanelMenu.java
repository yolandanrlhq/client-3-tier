package view.menu;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.MenuItem;
import net.miginfocom.swing.MigLayout;
import view.FrameUtama;

public class PanelMenu extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel panelKonten;
    private PanelMenuItem panelMenuItem;
    private PanelMenuItem panelDashboard = null;
    
    private JLabel labelJudul;
    private JPanel panelJudul;

    public PanelMenu(List<MenuItem> listDaftarMenuItem, CardLayout cardLayout, JPanel panelKonten) {
        this.cardLayout = cardLayout;
        this.panelKonten = panelKonten;

        initializeUI();
        buildMenu(listDaftarMenuItem);
        selectDefaultMenu();
    }

    private void initializeUI() {
        // Menggunakan hidemode 3 agar komponen invisible tidak memakan space
        setLayout(new MigLayout("fillx, wrap 1, insets 0, gap 0, hidemode 3", "[grow]", ""));
        setBackground(new Color(245, 247, 250));
        setPreferredSize(new Dimension(280, 0));

        // Panel judul: Rata kiri sesuai request (Foto 1)
        panelJudul = new JPanel(new MigLayout("insets 20 30 20 25", "[grow]")); // Insets kiri 30 agar sejajar teks menu
        panelJudul.setOpaque(false);

        labelJudul = new JLabel("COSTUME RENTAL");
        labelJudul.setFont(new Font("Inter", Font.BOLD, 18));
        labelJudul.setForeground(new Color(0, 48, 73));

        panelJudul.add(labelJudul, "pushx, align left"); 
        
        add(panelJudul, "growx");
        
        // Pemisah dengan sedikit jarak bawah
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        add(separator, "growx, gapbottom 15");
    }

    private void buildMenu(List<MenuItem> listDaftarMenuItem) {
        for (MenuItem menu : listDaftarMenuItem) {
            PanelMenuItem itemPanel = new PanelMenuItem(menu, this);
            // Tinggi tiap item menu diatur 50px agar enak diklik jari di layar sentuh/mobile
            add(itemPanel, "growx, h 50!");

            if (menu.hasSubMenuItem()) {
                JPanel panelSubMenu = itemPanel.getPanelCountainerSubMenu();
                panelSubMenu.setName("sub"); 
                // Indentasi untuk sub-menu tanpa icon
                add(panelSubMenu, "growx, gapleft 20");
                panelSubMenu.setVisible(false);
            }

            if ("Dashboard".equals(menu.getJudul())) {
                panelDashboard = itemPanel;                
            }
        }
    }

    private void selectDefaultMenu() {
        if (panelDashboard != null) {
            selectMenuItem(panelDashboard);
        }
    }

    public void selectMenuItem(PanelMenuItem clickedPanel) {
        if (panelMenuItem != null) {
            panelMenuItem.setSelectedByParent(false);
        }
        clickedPanel.setSelectedByParent(true);
        panelMenuItem = clickedPanel;

        String key = clickedPanel.getContentKey();
        if (key != null && !key.isEmpty()) {
            FrameUtama frame = (FrameUtama) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                // gantiPanel akan menangani penutupan menu otomatis di layar kecil
                frame.gantiPanel(key); 
            } else {
                cardLayout.show(panelKonten, key);
            }
        }
    }
}