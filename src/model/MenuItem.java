package model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon; // Import wajib untuk Icon

public class MenuItem {
    private String judul;
    private String contentKey; // Digunakan untuk memanggil panel di CardLayout
    private List<MenuItem> subMenuItems;

    // Konstruktor 1: Menu utama yang punya Icon (Sangat disarankan untuk menu level 1)
    public MenuItem(String judul, Icon icon, String contentKey) {
        this.judul = judul;
        this.contentKey = contentKey;
        this.subMenuItems = new ArrayList<>();
    }

    // Konstruktor 2: Menu utama yang punya sub-menu (Struktur lama kamu)
    public MenuItem(String judul) {
        this.judul = judul;
        this.subMenuItems = new ArrayList<>();
    }

    // Konstruktor 3: Menu yang langsung membuka panel (Struktur lama kamu)
    public MenuItem(String judul, String contentKey) {
        this.judul = judul;
        this.contentKey = contentKey;
        this.subMenuItems = new ArrayList<>();
    }

    public void addSubMenuItem(MenuItem item) {
        subMenuItems.add(item);
    }

    public boolean hasSubMenuItem() {
        return !subMenuItems.isEmpty();
    }

    // --- Getter dan Setter ---
    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public List<MenuItem> getSubMenuItems() {
        return subMenuItems;
    }
}