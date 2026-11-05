package praktikum.pbo.projekakhir;

public class PaketBermain {
    private String idPaket;
    private String namaPaket;
    private int durasiMenit;
    private int sisaWaktuMenit;
    private double harga;

    public PaketBermain(String idPaket, String namaPaket, int durasiMenit, double harga) {
        this.idPaket = idPaket;
        this.namaPaket = namaPaket;
        this.durasiMenit = durasiMenit;
        this.harga = harga;
        this.sisaWaktuMenit = durasiMenit;
    }

    public String getNamaPaket() { return namaPaket; }
    public int getDurasiMenit() { return durasiMenit; }
    public double getHarga() { return harga; }
    
    // Penerapan Encapsulation
    public void ubahNama(String namaBaru) {
        if (namaBaru != null && !namaBaru.trim().isEmpty()) {
            this.namaPaket = namaBaru;
        }
    }

    public void ubahDurasi(int durasiBaru) {
        if (durasiBaru > 0) {
            this.durasiMenit = durasiBaru;
        }
    }

    public void ubahHarga(double hargaBaru) {
        if (hargaBaru >= 0) {
            this.harga = hargaBaru;
        }
    }
    
    public void setSisaWaktuMenit(int waktu) { this.sisaWaktuMenit = waktu; }   

    // FUNGSI BARU: Untuk mendapatkan format durasi yang presisi
    public String getFormatDurasi() {
        int jam = durasiMenit / 60;
        int menit = durasiMenit % 60;
        
        if (jam > 0 && menit > 0) {
            return jam + " Jam " + menit + " Menit";
        } else if (jam > 0) {
            return jam + " Jam";
        } else {
            return menit + " Menit";
        }
    }
    
    public String getIdPaket() { return idPaket; }

    // Mengubah tampilan teks pada Radio Button di panel Transaksi
    @Override
    public String toString() {
        return namaPaket + " (" + getFormatDurasi() + ") - Rp" + String.format("%,.0f", harga);
    }
}