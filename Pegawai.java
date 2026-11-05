package praktikum.pbo.projekakhir;

public class Pegawai extends Pengguna {
    private String jenisTugas; // Contoh: "Kasir", "Penjaga"

    public Pegawai(String idPengguna, String username, String namaLengkap, String jenisTugas) {
        super(idPengguna, username, namaLengkap, "Pegawai");
        this.jenisTugas = jenisTugas;
    }

    public String getJenisTugas() { return jenisTugas; }

    @Override
    public String dapatkanAksesMenu() {
        if (jenisTugas.equalsIgnoreCase("Kasir")) {
            return "Akses Terbatas: Transaksi & Kasir";
        } else {
            return "Akses Terbatas: Tiket & Sesi";
        }
    }
}