package praktikum.pbo.projekakhir;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransaksiPanel extends JPanel {
    private DataStore dataStore;
    private List<PaketBermain> paketTerpilihList = new ArrayList<>();
    private List<String> namaAnakList = new ArrayList<>();
    private double totalBiaya = 0;
    
    private JTextField txtNamaAnak;
    private ButtonGroup bgPaket;
    private JPanel radioPanel; // Wadah khusus untuk daftar paket
    private JTextArea txtRingkasan;
    private JLabel lblTotalBiayaHeader;
    private JLabel lblAnakHeader;

    // ===================== STYLING (UI SAJA) =====================
    private static final Color WARNA_NAVY  = new Color(18, 38, 84);
    private static final Color WARNA_BIRU  = new Color(31, 95, 176);
    private static final Color WARNA_GARIS = new Color(220, 231, 246);
    private static final Color[] KARTU_KIRI  = { new Color(222, 238, 254), new Color(254, 238, 220), new Color(238, 230, 254) };
    private static final Color[] KARTU_KANAN = { new Color(238, 247, 255), new Color(255, 246, 236), new Color(248, 243, 255) };
    private static final Color[] KARTU_AKSEN = { new Color(36, 140, 236), new Color(244, 150, 20), new Color(124, 77, 220) };

    // Opsional: taruh Poppins-Regular/Medium/SemiBold/Bold.ttf di folder resources /fonts/
    static {
        for (String f : new String[]{"Poppins-Regular", "Poppins-Medium", "Poppins-SemiBold", "Poppins-Bold"}) {
            try (java.io.InputStream in = TransaksiPanel.class.getResourceAsStream("/fonts/" + f + ".ttf")) {
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

    private static Color cerah(Color c) {
        return new Color(c.getRed() + (255 - c.getRed()) * 35 / 100,
                         c.getGreen() + (255 - c.getGreen()) * 35 / 100,
                         c.getBlue() + (255 - c.getBlue()) * 35 / 100);
    }

    private static Path2D bintang(double cx, double cy, double R, double r) {
        Path2D p = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double a = -Math.PI / 2 + i * Math.PI / 5, rad = (i % 2 == 0) ? R : r;
            double x = cx + rad * Math.cos(a), y = cy + rad * Math.sin(a);
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        return p;
    }

    /** Ikon bulat biru muda: 0 = pengunjung, 1 = paket, 2 = dokumen, 3 = dompet. */
    private static void gambarIkon(Graphics2D g2, int tipe, int x, int y, int d) {
        g2.setPaint(new GradientPaint(x, y, new Color(206, 232, 255), x, y + d, new Color(150, 200, 248)));
        g2.fillOval(x, y, d, d);
        g2.setColor(new Color(30, 110, 210));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (tipe) {
            case 0:
                g2.fill(new Ellipse2D.Double(x + d * 0.36, y + d * 0.2, d * 0.28, d * 0.28));
                g2.fill(new Arc2D.Double(x + d * 0.24, y + d * 0.52, d * 0.52, d * 0.44, 0, 180, Arc2D.PIE));
                break;
            case 1:
                Path2D hex = new Path2D.Double();
                hex.moveTo(x + d * 0.5, y + d * 0.22); hex.lineTo(x + d * 0.78, y + d * 0.36);
                hex.lineTo(x + d * 0.78, y + d * 0.64); hex.lineTo(x + d * 0.5, y + d * 0.78);
                hex.lineTo(x + d * 0.22, y + d * 0.64); hex.lineTo(x + d * 0.22, y + d * 0.36);
                hex.closePath();
                g2.draw(hex);
                g2.draw(new Line2D.Double(x + d * 0.5, y + d * 0.5, x + d * 0.5, y + d * 0.78));
                g2.draw(new Line2D.Double(x + d * 0.5, y + d * 0.5, x + d * 0.22, y + d * 0.36));
                g2.draw(new Line2D.Double(x + d * 0.5, y + d * 0.5, x + d * 0.78, y + d * 0.36));
                break;
            case 2:
                g2.draw(new RoundRectangle2D.Double(x + d * 0.3, y + d * 0.22, d * 0.4, d * 0.56, 5, 5));
                g2.draw(new Line2D.Double(x + d * 0.4, y + d * 0.42, x + d * 0.6, y + d * 0.42));
                g2.draw(new Line2D.Double(x + d * 0.4, y + d * 0.54, x + d * 0.6, y + d * 0.54));
                g2.draw(new Line2D.Double(x + d * 0.4, y + d * 0.66, x + d * 0.52, y + d * 0.66));
                break;
            default:
                g2.draw(new RoundRectangle2D.Double(x + d * 0.22, y + d * 0.32, d * 0.56, d * 0.4, 6, 6));
                g2.draw(new Line2D.Double(x + d * 0.3, y + d * 0.32, x + d * 0.62, y + d * 0.22));
                g2.fill(new Ellipse2D.Double(x + d * 0.6, y + d * 0.46, d * 0.1, d * 0.1));
        }
    }

    private static class IkonLabel implements Icon {
        private final int tipe;
        IkonLabel(int tipe) { this.tipe = tipe; }
        public int getIconWidth() { return 30; }
        public int getIconHeight() { return 30; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            aaOn(g2);
            gambarIkon(g2, tipe, x, y, 30);
            g2.dispose();
        }
    }

    /** Panel kartu putih membulat; bila judul diisi, ikon + judul dilukis di bagian atas. */
    private static class KartuPanel extends JPanel {
        private final int arc, ikon;
        private final String judul;
        KartuPanel(LayoutManager lm, int arc, String judul, int ikon) {
            super(lm);
            this.arc = arc; this.judul = judul; this.ikon = ikon;
            setOpaque(false);
            if (judul != null) setBorder(BorderFactory.createEmptyBorder(60, 18, 18, 18));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aaOn(g2);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(WARNA_GARIS);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            if (judul != null) {
                gambarIkon(g2, ikon, 18, 16, 30);
                g2.setFont(fontUI("SemiBold", 16f));
                g2.setColor(WARNA_NAVY);
                g2.drawString(judul, 58, 38);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Pilihan paket: kartu gradasi + ikon bulat berwarna (tanpa gambar wahana). Tetap JRadioButton. */
    private static class PaketRadio extends JRadioButton {
        private final PaketBermain paket;
        private final int idx;
        PaketRadio(PaketBermain paket, int idx) {
            super(paket.toString());
            this.paket = paket;
            this.idx = idx % 3;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 74));
        }

        // Ubah total menit jadi teks "(X jam Y menit)"; menit 0 -> "(X jam)"
        private static String formatDurasi(int totalMenit) {
            int jam = totalMenit / 60;
            int menit = totalMenit % 60;
            if (jam == 0) return "(" + menit + " menit)";
            if (menit == 0) return "(" + jam + " jam)";
            return "(" + jam + " jam " + menit + " menit)";
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aaOn(g2);
            int w = getWidth(), h = getHeight();
            Color aksen = KARTU_AKSEN[idx];
            g2.setPaint(new GradientPaint(0, 0, KARTU_KIRI[idx], w, 0, KARTU_KANAN[idx]));
            g2.fillRoundRect(0, 0, w - 1, h - 1, 20, 20);
            if (isSelected()) {
                g2.setColor(aksen);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 20, 20);
            }
            // lingkaran radio
            int d = 18, cx = 14, cy = (h - d) / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(cx, cy, d, d);
            g2.setStroke(new BasicStroke(1.6f));
            g2.setColor(isSelected() ? aksen : new Color(150, 160, 180));
            g2.drawOval(cx, cy, d, d);
            if (isSelected()) g2.fillOval(cx + 4, cy + 4, d - 8, d - 8);
            // ikon bulat berwarna
            int ds = 44, ix = 46, iy = (h - ds) / 2;
            g2.setPaint(new GradientPaint(ix, iy, cerah(aksen), ix, iy + ds, aksen));
            g2.fillOval(ix, iy, ds, ds);
            g2.setColor(Color.WHITE);
            double mx = ix + ds / 2.0, my = iy + ds / 2.0;
            if (idx == 0) {
                Path2D t = new Path2D.Double();
                t.moveTo(mx - 6, my - 9); t.lineTo(mx + 10, my); t.lineTo(mx - 6, my + 9); t.closePath();
                g2.fill(t);
            } else if (idx == 1) {
                g2.fill(bintang(mx, my + 1, 12, 5.2));
            } else {
                Path2D c = new Path2D.Double();
                c.moveTo(mx - 11, my + 8); c.lineTo(mx - 13, my - 7); c.lineTo(mx - 5, my - 1);
                c.lineTo(mx, my - 10); c.lineTo(mx + 5, my - 1); c.lineTo(mx + 13, my - 7);
                c.lineTo(mx + 11, my + 8); c.closePath();
                g2.fill(c);
            }
            // teks
            g2.setFont(fontUI("SemiBold", 15f));
            g2.setColor(WARNA_NAVY);
            FontMetrics fm = g2.getFontMetrics();
            int yNama = h / 2 - 4;
            g2.drawString(paket.getNamaPaket() + " " + formatDurasi(paket.getDurasiMenit()), 106, yNama);
            g2.setFont(fontUI("Regular", 14f));
            g2.drawString("Rp" + String.format("%,.0f", paket.getHarga()), 106, yNama + fm.getHeight() + 1);
            g2.dispose();
        }
    }

    /** Tombol dengan gradasi warna dan sudut membulat. */
    private static class TombolGradasi extends JButton {
        private final Color c1, c2;
        TombolGradasi(String teks, Color c1, Color c2, Color fg) {
            super(teks);
            this.c1 = c1; this.c2 = c2;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(fg);
            setFont(fontUI("SemiBold", 14f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            putClientProperty("FlatLaf.style", "arc: 18; borderWidth: 0; focusWidth: 0; innerFocusWidth: 0; margin: 13,16,13,16");
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            aaOn(g2);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            if (getModel().isPressed()) {
                g2.setColor(new Color(0, 0, 0, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Dekorasi banner: awan, dua tiket, dan kilau (semua dilukis, bukan gambar). */
    private static void gambarDekorBanner(Graphics2D g2, int w, int h) {
        Shape clipLama = g2.getClip();
        g2.setClip(new RoundRectangle2D.Double(0, 0, w, h, 28, 28));
        // awan
        g2.setColor(new Color(170, 226, 214, 190));
        g2.fill(new Ellipse2D.Double(w - 190, h - 34, 120, 70));
        g2.fill(new Ellipse2D.Double(w - 110, h - 46, 130, 90));
        g2.setColor(new Color(150, 205, 245, 200));
        g2.fill(new Ellipse2D.Double(w - 260, h - 24, 110, 60));
        // tiket koral (belakang)
        AffineTransform lama = g2.getTransform();
        g2.rotate(Math.toRadians(9), w - 72, h / 2.0);
        g2.setPaint(new GradientPaint(w - 100, 20, new Color(255, 150, 120), w - 40, 90, new Color(240, 110, 90)));
        g2.fill(new RoundRectangle2D.Double(w - 108, 18, 74, 68, 14, 14));
        g2.setTransform(lama);
        // tiket kuning (depan)
        g2.rotate(Math.toRadians(-12), w - 150, h / 2.0);
        g2.setPaint(new GradientPaint(w - 210, 14, new Color(255, 222, 120), w - 100, 86, new Color(250, 170, 50)));
        g2.fill(new RoundRectangle2D.Double(w - 210, 14, 112, 70, 16, 16));
        g2.setColor(Color.WHITE);
        g2.fill(bintang(w - 154, 49, 16, 7));
        g2.setTransform(lama);
        // kilau
        g2.setColor(new Color(255, 205, 70));
        g2.fill(bintang(w - 250, 36, 9, 3));
        g2.setColor(Color.WHITE);
        g2.fill(bintang(w - 20, 28, 7, 2.5));
        g2.fill(bintang(w - 284, 70, 5, 2));
        g2.setClip(clipLama);
    }

    public TransaksiPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 244, 254));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Judul + banner berwarna (subjudul dilukis, bukan komponen baru)
        JLabel lblTitle = new JLabel("Transaksi & Pembayaran Tiket") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                aaOn(g2);
                g2.setPaint(new GradientPaint(0, 0, new Color(176, 214, 248), getWidth(), 0, new Color(214, 236, 253)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                gambarDekorBanner(g2, getWidth(), getHeight());
                g2.setFont(fontUI("Regular", 14f));
                g2.setColor(new Color(40, 70, 130));
                g2.drawString("Pilih paket bermain dan selesaikan pembayaran.", 28, getHeight() - 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblTitle.setFont(fontUI("Bold", 25f));
        lblTitle.setForeground(WARNA_NAVY);
        lblTitle.setVerticalAlignment(SwingConstants.TOP);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 28, 48, 28));
        add(lblTitle, BorderLayout.NORTH);

        // --- PANEL KIRI ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);

        JPanel formPanel = new KartuPanel(new GridLayout(2, 1, 0, 6), 24, "Data Pengunjung", 0);
        formPanel.setBackground(Color.WHITE);
        JLabel lblNama = new JLabel("Nama Anak:");
        lblNama.setFont(fontUI("Medium", 13f));
        lblNama.setForeground(WARNA_NAVY);
        formPanel.add(lblNama);
        txtNamaAnak = new JTextField();
        txtNamaAnak.setFont(fontUI("Regular", 14f));
        txtNamaAnak.putClientProperty("JTextField.placeholderText", "Masukkan nama anak...");
        txtNamaAnak.putClientProperty("FlatLaf.style",
                "arc: 16; borderColor: #D5E3F5; focusedBorderColor: #2A96F0; focusWidth: 0; margin: 8,14,8,14; background: #F7FAFF");
        formPanel.add(txtNamaAnak);
        leftPanel.add(formPanel, BorderLayout.NORTH);

        // Pilihan Paket
        JPanel paketPanel = new KartuPanel(new BorderLayout(10, 10), 24, "Pilih Paket Bermain", 1);
        paketPanel.setBackground(Color.WHITE);
        
        radioPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        radioPanel.setOpaque(false);
        paketPanel.add(radioPanel, BorderLayout.CENTER);

        JButton btnTambahAnak = new TombolGradasi("+ Tambah ke Transaksi",
                new Color(196, 226, 252), new Color(176, 216, 250), WARNA_BIRU);
        paketPanel.add(btnTambahAnak, BorderLayout.SOUTH);
        
        leftPanel.add(paketPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.CENTER);

        // --- PANEL KANAN ---
        JPanel rightPanel = new KartuPanel(new BorderLayout(10, 10), 24, null, 0);
        rightPanel.setPreferredSize(new Dimension(380, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel infoHeader = new JPanel(new GridLayout(1, 2));
        infoHeader.setOpaque(false);
        lblAnakHeader = new JLabel("Tiket: 0 Anak");
        lblAnakHeader.setIcon(new IkonLabel(2));
        lblAnakHeader.setIconTextGap(10);
        lblAnakHeader.setFont(fontUI("SemiBold", 15f));
        lblAnakHeader.setForeground(WARNA_NAVY);
        lblTotalBiayaHeader = new JLabel("Total: Rp0");
        lblTotalBiayaHeader.setFont(fontUI("Bold", 15f));
        lblTotalBiayaHeader.setForeground(WARNA_NAVY);
        lblTotalBiayaHeader.setHorizontalAlignment(SwingConstants.CENTER);
        infoHeader.add(lblAnakHeader);
        infoHeader.add(lblTotalBiayaHeader);
        rightPanel.add(infoHeader, BorderLayout.NORTH);

        txtRingkasan = new JTextArea();
        txtRingkasan.setEditable(false);
        txtRingkasan.setFont(fontUI("Regular", 13f));
        txtRingkasan.setForeground(WARNA_NAVY);
        txtRingkasan.setMargin(new Insets(12, 12, 12, 12));
        JScrollPane scrollRingkasan = new JScrollPane(txtRingkasan);
        scrollRingkasan.setBorder(new LineBorder(WARNA_GARIS, 1, true));
        rightPanel.add(scrollRingkasan, BorderLayout.CENTER);

        JPanel paymentAction = new JPanel(new GridLayout(3, 1, 5, 8));
        paymentAction.setOpaque(false);
        JLabel lblMetode = new JLabel("Metode Bayar:");
        lblMetode.setIcon(new IkonLabel(3));
        lblMetode.setIconTextGap(10);
        lblMetode.setFont(fontUI("SemiBold", 14f));
        lblMetode.setForeground(WARNA_NAVY);
        paymentAction.add(lblMetode);
        JComboBox<String> cmbBayar = new JComboBox<>(new String[]{"Tunai (Cash)", "QRIS"});
        cmbBayar.setFont(fontUI("Regular", 14f));
        cmbBayar.putClientProperty("FlatLaf.style", "arc: 16; borderColor: #D5E3F5; focusWidth: 0; padding: 6,12,6,12; background: #F7FAFF");
        paymentAction.add(cmbBayar);

        JButton btnBayar = new TombolGradasi("Bayar & Terbitkan Tiket",
                new Color(36, 100, 186), new Color(24, 64, 140), Color.WHITE);
        paymentAction.add(btnBayar);

        rightPanel.add(paymentAction, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // --- LOGIKA EVENT LISTENER ---
        btnTambahAnak.addActionListener(e -> {
            // PERUBAHAN: Hanya memberikan notifikasi info, bukan melarang (Peringatan)
            if (dataStore.getKapasitasTersedia() - paketTerpilihList.size() <= 0) {
                JOptionPane.showMessageDialog(this, "Kapasitas penuh! Tiket berikutnya akan berstatus 'Menunggu'.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            
            String nama = txtNamaAnak.getText().trim();
            if (nama.isEmpty() || bgPaket == null || bgPaket.getSelection() == null) {
                JOptionPane.showMessageDialog(this, "Isi nama anak dan pilih paket!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String namaPaketDipilih = bgPaket.getSelection().getActionCommand();
            PaketBermain paketDipilih = dataStore.getDaftarPaket().stream()
                .filter(p -> p.getNamaPaket().equals(namaPaketDipilih))
                .findFirst().orElse(null);

            if (paketDipilih != null) {
                namaAnakList.add(nama);
                paketTerpilihList.add(paketDipilih);
                totalBiaya += paketDipilih.getHarga();
                
                txtNamaAnak.setText("");
                bgPaket.clearSelection();
                perbaruiRingkasan();
            }
        });

        btnBayar.addActionListener(e -> {
            if (paketTerpilihList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada tiket yang ditambahkan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            for (int i = 0; i < paketTerpilihList.size(); i++) {
                String idTiketBaru = "TKT-" + System.currentTimeMillis() + "-" + i;
                Tiket tiketBaru = new Tiket(idTiketBaru, namaAnakList.get(i), paketTerpilihList.get(i));
                
                // Secara otomatis, tiketBaru akan memiliki status "Menunggu" dari konstruktor Tiket.java
                dataStore.tambahTiket(tiketBaru); 
            }
            JOptionPane.showMessageDialog(this, "Pembayaran Berhasil! " + paketTerpilihList.size() + " tiket diterbitkan dan masuk ke antrean (Menunggu).");
            
            namaAnakList.clear();
            paketTerpilihList.clear();
            totalBiaya = 0;
            perbaruiRingkasan();
        });

        // Panggil untuk menampilkan UI paket saat pertama kali dibuat
        refreshPaketUI(); 
        perbaruiRingkasan();
    }

    // Latar halaman: gradasi biru muda lembut
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, new Color(240, 247, 255), getWidth(), getHeight(), new Color(224, 237, 252)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    // FUNGSI BARU: Untuk me-refresh ulang daftar RadioButton paket
    public void refreshPaketUI() {
        radioPanel.removeAll(); // Hapus pilihan lama
        bgPaket = new ButtonGroup();
        
        int warnaKe = 0;
        for (PaketBermain paket : dataStore.getDaftarPaket()) {
            JRadioButton rb = new PaketRadio(paket, warnaKe);
            rb.setActionCommand(paket.getNamaPaket());
            bgPaket.add(rb);
            radioPanel.add(rb);
            warnaKe++;
        }
        
        // Memaksa UI untuk menggambar ulang komponen terbaru
        radioPanel.revalidate();
        radioPanel.repaint();
    }

    private void perbaruiRingkasan() {
        lblAnakHeader.setText("Tiket: " + namaAnakList.size() + " Anak");
        lblTotalBiayaHeader.setText("Total: Rp" + String.format("%,.0f", totalBiaya));
        
        StringBuilder sb = new StringBuilder();
        sb.append("--- RINGKASAN TRANSAKSI ---\n\n");
        if (namaAnakList.isEmpty()) {
            sb.append("Belum ada tiket.\n");
        } else {
            for (int i = 0; i < namaAnakList.size(); i++) {
                sb.append((i+1)).append(". ").append(namaAnakList.get(i)).append("\n");
                sb.append("   ").append(paketTerpilihList.get(i).getNamaPaket())
                  .append(" - Rp").append(String.format("%,.0f", paketTerpilihList.get(i).getHarga())).append("\n");
            }
        }
        sb.append("\n---------------------------\n");
        sb.append("TOTAL BIAYA: Rp").append(String.format("%,.0f", totalBiaya));
        txtRingkasan.setText(sb.toString());
    }
}