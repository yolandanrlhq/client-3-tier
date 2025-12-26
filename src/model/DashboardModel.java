package model;

public class DashboardModel {
    private int totalKostum;
    private int sedangDisewa;
    private double totalPendapatan;

    // WAJIB: Constructor kosong agar GSON bisa instansiasi
    public DashboardModel() {
    }

    public DashboardModel(int totalKostum, int sedangDisewa, double totalPendapatan) {
        this.totalKostum = totalKostum;
        this.sedangDisewa = sedangDisewa;
        this.totalPendapatan = totalPendapatan;
    }

    // Getter
    public int getTotalKostum() { return totalKostum; }
    public int getSedangDisewa() { return sedangDisewa; }
    public double getTotalPendapatan() { return totalPendapatan; }
    
    // Setter (Juga disarankan untuk GSON)
    public void setTotalKostum(int totalKostum) { this.totalKostum = totalKostum; }
    public void setSedangDisewa(int sedangDisewa) { this.sedangDisewa = sedangDisewa; }
    public void setTotalPendapatan(double totalPendapatan) { this.totalPendapatan = totalPendapatan; }
}