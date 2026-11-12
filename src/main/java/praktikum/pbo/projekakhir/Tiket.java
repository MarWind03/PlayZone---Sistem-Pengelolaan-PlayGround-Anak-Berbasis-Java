package praktikum.pbo.projekakhir;

public class Tiket {
    private String idTiket;
    private String namaAnak;
    private PaketBermain paket;
    private String status; // "Menunggu", "Aktif", "Jeda", "Waktu Habis"
    private int sisaWaktuMenit;

    public Tiket(String idTiket, String namaAnak, PaketBermain paket) {
        this.idTiket = idTiket;
        this.namaAnak = namaAnak;
        this.paket = paket;
        
        // SESUAI PERMINTAAN: Default awal SELALU "Menunggu" (Belum masuk)
        this.status = "Menunggu"; 
        
        this.sisaWaktuMenit = paket.getDurasiMenit();
    }

    public String getIdTiket() { return idTiket; }
    public String getNamaAnak() { return namaAnak; }
    public PaketBermain getPaket() { return paket; }
    public String getStatus() { return status; }
    public int getSisaWaktuMenit() { return sisaWaktuMenit; }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Tambahkan di bawah fungsi setStatus()
    public void setSisaWaktuMenit(int waktu) {
        this.sisaWaktuMenit = waktu;
    }
    
    // Waktu hanya berkurang saat statusnya benar-benar "Aktif"
    public void kurangiWaktu(int menit) {
        if (this.status.equals("Aktif") && this.sisaWaktuMenit > 0) {
            this.sisaWaktuMenit -= menit;
            if (this.sisaWaktuMenit <= 0) {
                this.sisaWaktuMenit = 0;
                this.status = "Waktu Habis"; 
            }
        }
    }

    public String getFormatSisaWaktu() {
        if (status.equals("Menunggu")) return "Belum Mulai"; 
        if (sisaWaktuMenit <= 0) return "Waktu Habis";
        
        int jam = sisaWaktuMenit / 60;
        int menit = sisaWaktuMenit % 60;
        
        if (jam > 0 && menit > 0) return jam + " Jam " + menit + " Menit";
        else if (jam > 0) return jam + " Jam";
        else return menit + " Menit";
    }
}