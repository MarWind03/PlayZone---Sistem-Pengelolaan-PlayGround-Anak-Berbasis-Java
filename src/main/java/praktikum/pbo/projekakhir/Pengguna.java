package praktikum.pbo.projekakhir;

public abstract class Pengguna {
    protected String idPengguna;
    protected String username;
    protected String namaLengkap;
    protected String role;

    public Pengguna(String idPengguna, String username, String namaLengkap, String role) {
        this.idPengguna = idPengguna;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }

    public String getNamaLengkap() { return namaLengkap; }
    public String getRole() { return role; }
    
    // Abstract method untuk polymorphism (opsional)
    public abstract String dapatkanAksesMenu(); 
}