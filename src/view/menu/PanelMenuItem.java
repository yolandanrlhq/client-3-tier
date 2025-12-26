package view.menu;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import model.MenuItem;
import view.FrameUtama;

public class PanelMenuItem extends JPanel {

    private static final Color BG_SIDEBAR = new Color(245, 247, 250);
    private static final Color BG_HOVER = new Color(224, 230, 235);
    private static final Color BG_SELECTED = new Color(234, 242, 235);
    private static final Color TEXT_NORMAL = new Color(98, 117, 138);
    private static final Color TEXT_SELECTED = new Color(131, 188, 160);

    private final String contentKey;
    private final String judul;
    private boolean selected = false;
    private final JPanel panelContainerSubMenu;
    private final JLabel labelMenu;
    private final PanelMenu panelMenu;
    private final MigLayout layout;

    public PanelMenuItem(MenuItem item, PanelMenu panelMenu) {
        this.panelMenu = panelMenu;
        this.judul = item.getJudul();
        this.contentKey = item.getContentKey();
        
        this.panelContainerSubMenu = new JPanel(new MigLayout("fillx, wrap 1, insets 0, gap 0"));
        this.panelContainerSubMenu.setOpaque(false);

        // Mengatur insets agar teks nempel ke kiri (gapleft 30 agar ada ruang manis di kiri)
        this.layout = new MigLayout("insets 0 30 0 20, hidemode 3", "[grow]", "[grow]");
        setLayout(this.layout);
        setBackground(BG_SIDEBAR);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        labelMenu = new JLabel(judul);
        labelMenu.setFont(new Font("Inter", Font.PLAIN, 14));
        labelMenu.setForeground(TEXT_NORMAL);

        // Langsung tambahkan teks menu saja
        add(labelMenu, "aligny center");

        if (item.hasSubMenuItem()) {
            for (MenuItem subItem : item.getSubMenuItems()) {
                // Untuk sub-menu, gap kiri ditambah sedikit agar ada hirarki (indentasi)
                PanelMenuItem subPanel = new PanelMenuItem(subItem, panelMenu);
                panelContainerSubMenu.add(subPanel, "growx, h 35!");
            }
        }

        addEvents();
    }

    private void addEvents() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) setBackground(BG_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selected) setBackground(BG_SIDEBAR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                panelMenu.selectMenuItem(PanelMenuItem.this);
                
                if (contentKey != null && !contentKey.isEmpty()) {
                    FrameUtama frame = (FrameUtama) SwingUtilities.getWindowAncestor(PanelMenuItem.this);
                    if (frame != null && frame.getWidth() < 900) {
                        frame.toggleMenu(); 
                    }
                }

                if (panelContainerSubMenu.getComponentCount() > 0) {
                    boolean isNowVisible = !panelContainerSubMenu.isVisible();
                    panelContainerSubMenu.setVisible(isNowVisible);
                    revalidate();
                }
            }
        });
    }

    public void setSelectedByParent(boolean selected) {
        this.selected = selected;
        setBackground(selected ? BG_SELECTED : BG_SIDEBAR);
        labelMenu.setForeground(selected ? TEXT_SELECTED : TEXT_NORMAL);
        labelMenu.setFont(new Font("Inter", selected ? Font.BOLD : Font.PLAIN, 14));
    }

    public String getContentKey() { return contentKey; }
    public JPanel getPanelCountainerSubMenu() { return panelContainerSubMenu; }
}