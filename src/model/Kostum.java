package model;

public class Kostum {
    private String id;
    private String nama;
    private String kategori;
    private int stok;
    private String ukuran;
    private double harga;
    private String status;

    // getter & setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getUkuran() { return ukuran; }
    public void setUkuran(String ukuran) { this.ukuran = ukuran; }

    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
