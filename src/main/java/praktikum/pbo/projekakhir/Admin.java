package praktikum.pbo.projekakhir;

public class Admin extends Pengguna {
    public Admin(String idPengguna, String username, String namaLengkap) {
        super(idPengguna, username, namaLengkap, "Admin");
    }

    @Override
    public String dapatkanAksesMenu() {
        return "Akses Penuh: Dashboard, Transaksi, Kelola Paket, Tiket & Sesi, Laporan";
    }
}