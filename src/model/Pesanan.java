package model;

import java.util.Date;

public class Pesanan {

    private String idSewa;
    private String namaPenyewa;

    // WAJIB ADA (FK)
    private String idKostum;

    // SNAPSHOT NAMA KOSTUM (BIAR TIDAK HILANG DI TABEL)
    private String namaKostum;

    private int jumlah;
    private double totalBiaya;
    private String status;
    private Date tglPinjam;

    public Pesanan() {}

    // ================= GETTER & SETTER =================

    public String getIdSewa() {
        return idSewa;
    }

    public void setIdSewa(String idSewa) {
        this.idSewa = idSewa;
    }

    public String getNamaPenyewa() {
        return namaPenyewa;
    }

    public void setNamaPenyewa(String namaPenyewa) {
        this.namaPenyewa = namaPenyewa;
    }

    public String getIdKostum() {
        return idKostum;
    }

    public void setIdKostum(String idKostum) {
        this.idKostum = idKostum;
    }

    public String getNamaKostum() {
        return namaKostum;
    }

    public void setNamaKostum(String namaKostum) {
        this.namaKostum = namaKostum;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public double getTotalBiaya() {
        return totalBiaya;
    }

    public void setTotalBiaya(double totalBiaya) {
        this.totalBiaya = totalBiaya;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTglPinjam() {
        return tglPinjam;
    }

    public void setTglPinjam(Date tglPinjam) {
        this.tglPinjam = tglPinjam;
    }

    @Override
    public String toString() {
        return "Pesanan{" +
                "idSewa='" + idSewa + '\'' +
                ", namaPenyewa='" + namaPenyewa + '\'' +
                ", namaKostum='" + namaKostum + '\'' +
                ", jumlah=" + jumlah +
                ", totalBiaya=" + totalBiaya +
                ", status='" + status + '\'' +
                '}';
    }
}
