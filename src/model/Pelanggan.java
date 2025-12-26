package model;

public class Pelanggan {
    private String id; // WAJIB String
    private String nama;
    private String noWa;
    private String alamat;

    public Pelanggan(String id, String nama, String noWa, String alamat) {
        this.id = id;
        this.nama = nama;
        this.noWa = noWa;
        this.alamat = alamat;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getNoWa() { return noWa; }
    public String getAlamat() { return alamat; }
}