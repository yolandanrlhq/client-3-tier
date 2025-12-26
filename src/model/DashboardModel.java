package model;

/**
 * Model ini berfungsi sebagai kontainer data statistik dashboard
 */
public class DashboardModel {
    private int totalKostum;
    private int sedangDisewa;
    private double totalPendapatan;

    public DashboardModel(int totalKostum, int sedangDisewa, double totalPendapatan) {
        this.totalKostum = totalKostum;
        this.sedangDisewa = sedangDisewa;
        this.totalPendapatan = totalPendapatan;
    }

    // Getter (Sangat penting untuk mengambil data di View)
    public int getTotalKostum() { return totalKostum; }
    public int getSedangDisewa() { return sedangDisewa; }
    public double getTotalPendapatan() { return totalPendapatan; }
}