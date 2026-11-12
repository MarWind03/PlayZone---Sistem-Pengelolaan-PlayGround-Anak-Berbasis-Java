package praktikum.pbo.projekakhir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LaporanPanel extends JPanel {
    private DataStore dataStore;
    private JLabel lblTotalTiket;
    private JLabel lblTotalPendapatan;
    private JLabel lblStatusKapasitas;

    // ===================== STYLING (UI SAJA) =====================
    private static final Color WARNA_NAVY  = new Color(18, 38, 84);
    private static final Color WARNA_MUTED = new Color(70, 90, 130);
    private static final Color WARNA_GARIS = new Color(220, 231, 246);

    // Opsional: taruh Poppins-Regular/Medium/SemiBold/Bold.ttf di folder resources /fonts/
    static {
        for (String f : new String[]{"Poppins-Regular", "Poppins-Medium", "Poppins-SemiBold", "Poppins-Bold"}) {
            try (java.io.InputStream in = LaporanPanel.class.getResourceAsStream("/fonts/" + f + ".ttf")) {
                if (in != null) GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .registerFont(Font.createFont(Font.TRUETYPE_FONT, in));
            } catch (Exception ignored) { }
        }
    }
    private static final Set<String> FONT_TERSEDIA = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

    /** Poppins (Regular/Medium/SemiBold/Bold) -> fallback Inter -> Segoe UI -> SansSerif */
    private static Font fontUI(String bobot, float ukuran) {
        boolean tebal = bobot.equals("Bold") || bobot.equals("SemiBold");
        if (FONT_TERSEDIA.contains("Poppins")) {
            String nama = bobot.equals("Regular") ? "Poppins" : "Poppins " + bobot;
            if (FONT_TERSEDIA.contains(nama)) return new Font(nama, Font.PLAIN, 12).deriveFont(ukuran);
            return new Font("Poppins", tebal ? Font.BOLD : Font.PLAIN, 12).deriveFont(ukuran);
        }
        String family = FONT_TERSEDIA.contains("Inter") ? "Inter"
                      : FONT_TERSEDIA.contains("Segoe UI") ? "Segoe UI" : "SansSerif";
        return new Font(family, tebal ? Font.BOLD : Font.PLAIN, 12).deriveFont(ukuran);
    }

    private static void aaOn(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /** Kecilkan font sampai teks (1 atau 2 baris) muat di lebar & tinggi yang tersedia. */
    private static float[] fitUkuran(Graphics2D g2, String t1, String b1, float u1,
                                     String t2, String b2, float u2,
                                     int gap, int maxW, int maxH) {
        float skala = 1f;
        while (skala > 0.4f) {
            Font f1 = fontUI(b1, u1 * skala), f2 = fontUI(b2, u2 * skala);
            FontMetrics m1 = g2.getFontMetrics(f1), m2 = g2.getFontMetrics(f2);
            int w = Math.max(m1.stringWidth(t1), t2.isEmpty() ? 0 : m2.stringWidth(t2));
            int h = m1.getHeight() + (t2.isEmpty() ? 0 : gap + m2.getHeight());
            if (w <= maxW && h <= maxH) break;
            skala -= 0.05f;
        }
        return new float[]{u1 * skala, u2 * skala};
    }

    /** Ikon bulat biru muda: 0 = pengunjung, 1 = grafik batang. */
    private static void gambarIkon(Graphics2D g2, int tipe, int x, int y, int d) {
        g2.setPaint(new GradientPaint(x, y, new Color(206, 232, 255), x, y + d, new Color(150, 200, 248)));
        g2.fillOval(x, y, d, d);
        g2.setColor(new Color(30, 110, 210));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (tipe == 0) {
            g2.fill(new Ellipse2D.Double(x + d * 0.36, y + d * 0.2, d * 0.28, d * 0.28));
            g2.fill(new Arc2D.Double(x + d * 0.24, y + d * 0.52, d * 0.52, d * 0.44, 0, 180, Arc2D.PIE));
        } else {
            g2.fill(new RoundRectangle2D.Double(x + d * 0.24, y + d * 0.52, d * 0.14, d * 0.24, 3, 3));
            g2.fill(new RoundRectangle2D.Double(x + d * 0.43, y + d * 0.38, d * 0.14, d * 0.38, 3, 3));
            g2.fill(new RoundRectangle2D.Double(x + d * 0.62, y + d * 0.24, d * 0.14, d * 0.52, 3, 3));
        }
    }

    /** Panel kartu putih membulat; ikon + judul dilukis di bagian atas (judul = pengganti TitledBorder). */
    private static class KartuPanel extends JPanel {
        private final int arc, ikon;
        private final String judul;
        KartuPanel(LayoutManager lm, int arc, String judul, int ikon) {
            super(lm);
            this.arc = arc; this.judul = judul; this.ikon = ikon;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(58, 18, 14, 18));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aaOn(g2);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(WARNA_GARIS);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            gambarIkon(g2, ikon, 18, 16, 34);
            g2.setFont(fontUI("SemiBold", 17f));
            g2.setColor(WARNA_NAVY);
            g2.drawString(judul, 62, 39);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * JLabel yang teksnya tetap "Keterangan: nilai" (diisi dari logika asli),
     * hanya cara menggambarnya yang diubah: keterangan kecil di atas, nilai besar di bawah.
     * Ukuran font otomatis mengecil agar teks selalu muat.
     */
    private static class LabelAngka extends JLabel {
        private final Color latar;
        LabelAngka(String teks, Color latar) {
            super(teks);
            this.latar = latar;
            setOpaque(false);
            setMinimumSize(new Dimension(120, 70));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aaOn(g2);
            g2.setColor(latar);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

            String t = getText() == null ? "" : getText();
            int i = t.indexOf(": ");
            String ket = i >= 0 ? t.substring(0, i) : "";
            String nilai = i >= 0 ? t.substring(i + 2) : t;

            int x = 24, gap = 2;
            float[] u = fitUkuran(g2, ket, "Medium", 15f, nilai, "Bold", 32f,
                    gap, getWidth() - x * 2, getHeight() - 10);
            Font fKet = fontUI("Medium", u[0]), fNilai = fontUI("Bold", u[1]);
            FontMetrics fmK = g2.getFontMetrics(fKet), fmN = g2.getFontMetrics(fNilai);

            int total = fmK.getHeight() + gap + fmN.getHeight();
            int y0 = Math.max(0, (getHeight() - total) / 2);

            g2.setColor(WARNA_NAVY);
            g2.setFont(fKet);
            g2.drawString(ket, x, y0 + fmK.getAscent());
            g2.setFont(fNilai);
            g2.drawString(nilai, x, y0 + fmK.getHeight() + gap + fmN.getAscent());
            g2.dispose();
        }
    }

    /**
     * JLabel status kapasitas: teks & warna tetap dari logika asli (setText / setForeground).
     * Latar dan judul besar mengikuti warna foreground; bagian "(Tersedia: ...)" dilukis lebih kecil.
     * Ukuran font otomatis mengecil agar teks selalu muat.
     */
    private static class LabelStatus extends JLabel {
        LabelStatus(String teks, int rata) {
            super(teks, rata);
            setOpaque(false);
            setMinimumSize(new Dimension(120, 70));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aaOn(g2);
            Color fg = getForeground();
            g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 28));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

            String t = getText() == null ? "" : getText();
            int i = t.indexOf(" (");
            String judul = i >= 0 ? t.substring(0, i) : t;
            String sub = i >= 0 ? t.substring(i + 1) : "";

            int x = 24, gap = 4;
            float[] u = fitUkuran(g2, judul, "Bold", 26f, sub, "Medium", 17f,
                    gap, getWidth() - x * 2, getHeight() - 10);
            Font fJudul = fontUI("Bold", u[0]), fSub = fontUI("Medium", u[1]);
            FontMetrics fmJ = g2.getFontMetrics(fJudul), fmS = g2.getFontMetrics(fSub);

            int total = fmJ.getHeight() + (sub.isEmpty() ? 0 : gap + fmS.getHeight());
            int y0 = Math.max(0, (getHeight() - total) / 2);

            g2.setColor(fg);
            g2.setFont(fJudul);
            g2.drawString(judul, x, y0 + fmJ.getAscent());
            if (!sub.isEmpty()) {
                g2.setFont(fSub);
                g2.drawString(sub, x, y0 + fmJ.getHeight() + gap + fmS.getAscent());
            }
            g2.dispose();
        }
    }

    public LaporanPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header (banner berwarna; subjudul dilukis, bukan komponen baru)
        JLabel lblTitle = new JLabel("Kapasitas & Laporan Operasional") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                aaOn(g2);
                g2.setPaint(new GradientPaint(0, 0, new Color(176, 214, 248), getWidth(), 0, new Color(214, 236, 253)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setFont(fontUI("Regular", 14f));
                g2.setColor(new Color(40, 70, 130));
                g2.drawString("Pantau penggunaan fasilitas dan lihat laporan operasional.", 28, getHeight() - 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblTitle.setFont(fontUI("Bold", 28f));
        lblTitle.setForeground(WARNA_NAVY);
        lblTitle.setVerticalAlignment(SwingConstants.TOP);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 28, 52, 28));
        add(lblTitle, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        centerPanel.setOpaque(false);

        // Bagian Laporan Keuangan/Tiket
        JPanel laporanCard = new KartuPanel(new GridLayout(1, 2, 16, 0), 24, "Rekapitulasi Hari Ini", 0);
        laporanCard.setBackground(Color.WHITE);

        lblTotalTiket = new LabelAngka("Total Tiket Terjual: 0 Tiket", new Color(222, 238, 254));
        lblTotalTiket.setFont(fontUI("Regular", 18f));

        lblTotalPendapatan = new LabelAngka("Total Pendapatan: Rp0", new Color(255, 243, 222));
        lblTotalPendapatan.setFont(fontUI("Bold", 18f));

        laporanCard.add(lblTotalTiket);
        laporanCard.add(lblTotalPendapatan);
        centerPanel.add(laporanCard);

        // Bagian Status Kapasitas
        JPanel kapasitasCard = new KartuPanel(new BorderLayout(), 24, "Status Kapasitas Saat Ini", 1);
        kapasitasCard.setBackground(Color.WHITE);

        lblStatusKapasitas = new LabelStatus("Kapasitas Aman (Tersedia: 30 / 30)", JLabel.CENTER);
        lblStatusKapasitas.setFont(fontUI("Bold", 20f));
        lblStatusKapasitas.setForeground(new Color(40, 167, 69)); // Hijau

        kapasitasCard.add(lblStatusKapasitas, BorderLayout.CENTER);
        centerPanel.add(kapasitasCard);

        add(centerPanel, BorderLayout.CENTER);
    }

    // Latar halaman: gradasi biru muda lembut
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, new Color(240, 247, 255), getWidth(), getHeight(), new Color(224, 237, 252)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    public void refreshData() {
        lblTotalTiket.setText("Total Tiket Terjual: " + dataStore.getTotalTiketTerjual() + " Tiket");
        lblTotalPendapatan.setText("Total Pendapatan: Rp" + String.format("%,.0f", dataStore.getTotalPendapatan()));

        int tersedia = dataStore.getKapasitasTersedia();
        if (tersedia > 10) {
            lblStatusKapasitas.setText("Kapasitas Aman (Tersedia: " + tersedia + ")");
            lblStatusKapasitas.setForeground(new Color(40, 167, 69)); // Hijau
        } else if (tersedia > 0) {
            lblStatusKapasitas.setText("Kapasitas Hampir Penuh (Tersedia: " + tersedia + ")");
            lblStatusKapasitas.setForeground(new Color(255, 193, 7)); // Kuning/Oranye
        } else {
            lblStatusKapasitas.setText("Kapasitas PENUH! (Tersedia: 0)");
            lblStatusKapasitas.setForeground(new Color(220, 53, 69)); // Merah
        }
    }
}