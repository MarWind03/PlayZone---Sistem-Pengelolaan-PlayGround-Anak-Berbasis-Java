package praktikum.pbo.projekakhir;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private List<PaketBermain> daftarPaket = new ArrayList<>();
    private List<Tiket> tiketAktif = new ArrayList<>();
    private int kapasitasMaksimal = 30;
    private double totalPendapatanHariIni = 0;
    private int totalTiketHariIni = 0;

    public DataStore() {
        pastikanFileAda(); // FUNGSI BARU: Mengecek dan membuat file jika belum ada
        muatDataDariFile();
    }

    // ==========================================
    // LOGIKA FILE I/O (TXT)
    // ==========================================

    // FUNGSI BARU: Otomatis membuat file fisik jika belum pernah dibuat
    private void pastikanFileAda() {
        try {
            File fileLaporan = new File("laporan.txt");
            if (!fileLaporan.exists()) {
                fileLaporan.createNewFile();
                try (PrintWriter pw = new PrintWriter(new FileWriter(fileLaporan))) {
                    pw.println("0;0.0"); // Nilai awal laporan
                }
            }

            File filePaket = new File("paket.txt");
            if (!filePaket.exists()) {
                filePaket.createNewFile();
                try (PrintWriter pw = new PrintWriter(new FileWriter(filePaket))) {
                    // Data paket bawaan (default)
                    pw.println("PKT-1;Mini Play;60;35000.0");
                    pw.println("PKT-2;Happy Kids;120;60000.0");
                    pw.println("PKT-3;Full Play;180;80000.0");
                }
            }

            File fileTiket = new File("tiket.txt");
            if (!fileTiket.exists()) {
                fileTiket.createNewFile();
                // Biarkan kosong, karena belum ada tiket yang terjual
            }

        } catch (IOException e) {
            System.out.println("Gagal membuat file database: " + e.getMessage());
        }
    }

    private void muatDataDariFile() {
        // 1. Load Data Laporan
        try (BufferedReader br = new BufferedReader(new FileReader("laporan.txt"))) {
            String line = br.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String[] data = line.split(";");
                totalTiketHariIni = Integer.parseInt(data[0]);
                totalPendapatanHariIni = Double.parseDouble(data[1]);
            }
        } catch (Exception e) {
        }

        // 2. Load Data Paket
        try (BufferedReader br = new BufferedReader(new FileReader("paket.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] d = line.split(";");
                    daftarPaket.add(new PaketBermain(d[0], d[1], Integer.parseInt(d[2]), Double.parseDouble(d[3])));
                }
            }
        } catch (Exception e) {
        }

        // 3. Load Data Tiket/Sesi
        try (BufferedReader br = new BufferedReader(new FileReader("tiket.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] d = line.split(";");
                    String idPaket = d[2];

                    PaketBermain p = daftarPaket.stream().filter(pkt -> pkt.getIdPaket().equals(idPaket)).findFirst()
                            .orElse(null);
                    if (p != null) {
                        Tiket t = new Tiket(d[0], d[1], p);
                        t.setStatus(d[3]);
                        t.setSisaWaktuMenit(Integer.parseInt(d[4]));
                        tiketAktif.add(t);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void simpanDataKeFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("laporan.txt"))) {
            pw.println(totalTiketHariIni + ";" + totalPendapatanHariIni);
        } catch (Exception e) {
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("paket.txt"))) {
            for (PaketBermain p : daftarPaket) {
                pw.println(p.getIdPaket() + ";" + p.getNamaPaket() + ";" + p.getDurasiMenit() + ";" + p.getHarga());
            }
        } catch (Exception e) {
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("tiket.txt"))) {
            for (Tiket t : tiketAktif) {
                pw.println(t.getIdTiket() + ";" + t.getNamaAnak() + ";" + t.getPaket().getIdPaket() + ";"
                        + t.getStatus() + ";" + t.getSisaWaktuMenit());
            }
        } catch (Exception e) {
        }
    }

    // ==========================================
    // PENGAMBILAN & MANIPULASI DATA
    // ==========================================

    public List<PaketBermain> getDaftarPaket() {
        return daftarPaket;
    }

    public List<Tiket> getTiketAktif() {
        return tiketAktif;
    }

    public Tiket getTiketById(String idTiket) {
        return tiketAktif.stream().filter(t -> t.getIdTiket().equals(idTiket)).findFirst().orElse(null);
    }

    public void updatePaketDB(PaketBermain p) {
        simpanDataKeFile();
    }

    public void tambahTiket(Tiket t) {
        tiketAktif.add(t);
        totalPendapatanHariIni += t.getPaket().getHarga();
        totalTiketHariIni++;
        simpanDataKeFile();
    }

    public void updateTiketDB(Tiket t) {
        simpanDataKeFile();
    }

    public void kurangiWaktuSemuaTiket() {
        boolean adaPerubahan = false;
        for (Tiket t : tiketAktif) {
            if (t.getStatus().equals("Aktif") && t.getSisaWaktuMenit() > 0) {
                t.kurangiWaktu(1);
                adaPerubahan = true;
            }
        }
        if (adaPerubahan) {
            simpanDataKeFile();
        }
    }

    public void konfirmasiKeluar(String idTiket) {
        tiketAktif.removeIf(t -> t.getIdTiket().equals(idTiket));
        simpanDataKeFile();
    }

    public int getAnakDiDalam() {
        int count = 0;
        for (Tiket t : tiketAktif) {
            if (t.getStatus().equals("Aktif") || t.getStatus().equals("Waktu Habis") || t.getStatus().equals("Jeda"))
                count++;
        }
        return count;
    }

    public int getKapasitasTersedia() {
        return kapasitasMaksimal - getAnakDiDalam();
    }

    public double getTotalPendapatan() {
        return totalPendapatanHariIni;
    }

    public int getTotalTiketTerjual() {
        return totalTiketHariIni;
    }
}