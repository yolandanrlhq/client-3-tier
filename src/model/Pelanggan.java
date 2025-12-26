package model;

public class Pelanggan {
    private String id; 
    private String nama;
    private String noWa;
    private String alamat;

    // 1. WAJIB: Constructor Kosong untuk GSON
    public Pelanggan() {
    }

    // 2. Constructor Lengkap (untuk penggunaan manual)
    public Pelanggan(String id, String nama, String noWa, String alamat) {
        this.id = id;
        this.nama = nama;
        this.noWa = noWa;
        this.alamat = alamat;
    }

    // 3. Getter
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getNoWa() { return noWa; }
    public String getAlamat() { return alamat; }

    // 4. Setter (Sangat disarankan agar GSON bekerja lebih optimal)
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setNoWa(String noWa) { this.noWa = noWa; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
}