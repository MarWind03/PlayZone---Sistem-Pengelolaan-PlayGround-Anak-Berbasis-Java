package praktikum.pbo.projekakhir;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;


public class DashboardPanel extends JPanel {
    private DataStore dataStore;
    private JLabel lblAnakDiDalam, lblKapasitas, lblSesiAktif;
    private JTable table;
    private DefaultTableModel tableModel;

    // ===== Tambahan UI (tidak terhubung ke logika) =====
    private JLabel lblTransaksiHariIni;
    private JLabel lblTotalTransaksi, lblJumlahTransaksi;
    private BarChart barChart;
    private JButton btnTransaksiBaru, btnCekTiket, btnLihatKapasitas;

    // ===== Palet warna sesuai referensi =====
    private static final Color BG     = new Color(241, 245, 251);
    private static final Color NAVY   = new Color(12, 30, 66);
    private static final Color CYAN   = new Color(34, 211, 238);
    private static final Color SKY    = new Color(125, 211, 252);
    private static final Color YELLOW = new Color(255, 214, 79);
    private static final Color CORAL  = new Color(255, 155, 122);
    private static final Color PURPLE = new Color(196, 181, 253);
    private static final Color MINT   = new Color(125, 223, 176);
    private static final Color MUTED  = new Color(100, 116, 139);

    // ===== Font Nunito (fallback ke SansSerif jika belum terpasang) =====
    private static Font nunito(int style, float size) {
        for (String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (f.equalsIgnoreCase("Nunito")) {
                return new Font("Nunito", style, 12).deriveFont(size);
            }
        }
        try {
            String file = (style & Font.BOLD) != 0 ? "/fonts/Nunito-Bold.ttf" : "/fonts/Nunito-Regular.ttf";
            java.io.InputStream in = DashboardPanel.class.getResourceAsStream(file);
            if (in != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(base);
                return base.deriveFont(size);
            }
        } catch (Exception ignored) { }
        return new Font("SansSerif", style, 12).deriveFont(size);
    }

    public DashboardPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(0, 16));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Header (banner)
        add(new BannerPanel(), BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        // Stat Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 112));

        lblAnakDiDalam = new JLabel("0");
        lblKapasitas = new JLabel("25");
        lblSesiAktif = new JLabel("0");
        lblTransaksiHariIni = new JLabel("0");

        statsPanel.add(createCard("Anak di Dalam", lblAnakDiDalam, SKY));
        statsPanel.add(createCard("Kapasitas Tersedia", lblKapasitas, YELLOW));
        statsPanel.add(createCard("Sesi Aktif", lblSesiAktif, CORAL));
        statsPanel.add(createCard("Transaksi Hari Ini", lblTransaksiHariIni, PURPLE));
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Tabel Sesi
        RoundedPanel tableContainer = new RoundedPanel(Color.WHITE, 28, true);
        tableContainer.setLayout(new BorderLayout(0, 8));
        tableContainer.setBorder(BorderFactory.createEmptyBorder(20, 22, 16, 22));

        JLabel lblTableTitle = new JLabel("Sesi Bermain Aktif");
        lblTableTitle.setFont(nunito(Font.BOLD, 18f));
        lblTableTitle.setForeground(NAVY);
        tableContainer.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"ID Tiket", "Nama Anak", "Paket Bermain", "Sisa Waktu", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setOpaque(false);
        tableContainer.add(scroll, BorderLayout.CENTER);

        // Ringkasan Transaksi
        RoundedPanel summary = new RoundedPanel(Color.WHITE, 28, true);
        summary.setLayout(new BorderLayout(0, 8));
        summary.setBorder(BorderFactory.createEmptyBorder(20, 22, 16, 22));

        JLabel lblSummaryTitle = new JLabel("Ringkasan Transaksi");
        lblSummaryTitle.setFont(nunito(Font.BOLD, 17f));
        lblSummaryTitle.setForeground(NAVY);

        lblTotalTransaksi = new JLabel("Rp0");
        lblTotalTransaksi.setFont(nunito(Font.BOLD, 30f));
        lblTotalTransaksi.setForeground(NAVY);
        lblJumlahTransaksi = new JLabel("0 transaksi");
        lblJumlahTransaksi.setFont(nunito(Font.BOLD, 13f));
        lblJumlahTransaksi.setForeground(MUTED);

        JPanel totalRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalRow.setOpaque(false);
        totalRow.add(lblTotalTransaksi);
        totalRow.add(lblJumlahTransaksi);

        JPanel summaryTop = new JPanel(new GridLayout(2, 1, 0, 6));
        summaryTop.setOpaque(false);
        summaryTop.add(lblSummaryTitle);
        summaryTop.add(totalRow);

        barChart = new BarChart();
        summary.add(summaryTop, BorderLayout.NORTH);
        summary.add(barChart, BorderLayout.CENTER);

        // Baris tabel + ringkasan (proporsional saat resize)
        // Tabel sesi dilebarkan penuh; panel ringkasan tidak ditampilkan
        centerPanel.add(tableContainer, BorderLayout.CENTER);

        // Tombol aksi cepat
        JPanel actions = new JPanel(new GridLayout(1, 3, 16, 0));
        actions.setOpaque(false);
        actions.setPreferredSize(new Dimension(0, 64));
//        btnTransaksiBaru = createActionButton("+", "Transaksi Baru", CORAL);
//        btnCekTiket = createActionButton("\u25A3", "Cek Tiket", CYAN);
//        btnLihatKapasitas = createActionButton("\u263A", "Lihat Kapasitas", PURPLE);
//        actions.add(btnTransaksiBaru);
//        actions.add(btnCekTiket);
//        actions.add(btnLihatKapasitas);
        centerPanel.add(actions, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        refreshData(); // Muat data awal
    }

    private JPanel createCard(String title, JLabel lblVal, Color bgColor) {
        RoundedPanel card = new RoundedPanel(bgColor, 28, true) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(6, 6, getWidth() - 14, getHeight() - 14, 28, 28));
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillOval(getWidth() - 90, -30, 100, 100);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillRoundRect(getWidth() - 60, getHeight() - 45, 70, 60, 24, 24);
                g2.dispose();
            }
        };
        card.setLayout(new GridLayout(2, 1, 0, 2));
        card.setBorder(BorderFactory.createEmptyBorder(18, 24, 14, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(nunito(Font.BOLD, 14f));
        lblTitle.setForeground(NAVY);
        lblVal.setFont(nunito(Font.BOLD, 38f));
        lblVal.setForeground(NAVY);

        card.add(lblTitle);
        card.add(lblVal);
        return card;
    }

    // Fungsi untuk memperbarui tampilan saat ada transaksi baru
    public void refreshData() {
        lblAnakDiDalam.setText(String.valueOf(dataStore.getAnakDiDalam()));
        lblKapasitas.setText(String.valueOf(dataStore.getKapasitasTersedia()));
        lblSesiAktif.setText(String.valueOf(dataStore.getTiketAktif().size()));
        lblTransaksiHariIni.setText(String.valueOf(dataStore.getTotalTiketTerjual())); // FIX: kartu Transaksi Hari Ini ikut diisi

        tableModel.setRowCount(0); // Hapus isi tabel lama
        for (Tiket t : dataStore.getTiketAktif()) {
            tableModel.addRow(new Object[]{
                t.getIdTiket(), t.getNamaAnak(), 
                t.getPaket().getNamaPaket(), 
                t.getFormatSisaWaktu(), // <--- UBAH BAGIAN INI
                t.getStatus()
            });
        }
    }

    // ===== Setter & getter tambahan khusus UI (opsional dipakai) =====
    public void setRingkasanTransaksi(String totalText, int jumlah, double[] values, String[] labels) {
        lblTotalTransaksi.setText(totalText);
        lblJumlahTransaksi.setText(jumlah + " transaksi");
        lblTransaksiHariIni.setText(String.valueOf(jumlah));
        barChart.setData(values, labels);
    }
    public JButton getBtnTransaksiBaru()   { return btnTransaksiBaru; }
    public JButton getBtnCekTiket()        { return btnCekTiket; }
    public JButton getBtnLihatKapasitas()  { return btnLihatKapasitas; }

    // ===== Helper UI =====
    private void styleTable(JTable t) {
        t.setFont(nunito(Font.PLAIN, 14f));
        t.setForeground(NAVY);
        t.setRowHeight(42);
        t.setShowGrid(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(236, 240, 246));
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(new Color(224, 247, 252));
        t.setSelectionForeground(NAVY);
        t.setFillsViewportHeight(true);
        t.setBackground(Color.WHITE);

        JTableHeader h = t.getTableHeader();
        h.setFont(nunito(Font.BOLD, 12f));
        h.setPreferredSize(new Dimension(0, 34));
        h.setReorderingAllowed(false);
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, false, false, r, c);
                l.setBackground(Color.WHITE);
                l.setForeground(MUTED);
                l.setFont(nunito(Font.BOLD, 12f));
                l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                return l;
            }
        });

        DefaultTableCellRenderer padded = new DefaultTableCellRenderer();
        padded.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        t.setDefaultRenderer(Object.class, padded);

        // Kolom Status ditampilkan sebagai "pill"
        t.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                final String text = v == null ? "" : v.toString();
                final boolean warn = text.toLowerCase().contains("hampir") || text.toLowerCase().contains("habis");
                JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7)) {
                    @Override public Dimension getPreferredSize() { return new Dimension(120, 42); }
                };
                wrap.setBackground(s ? tb.getSelectionBackground() : Color.WHITE);
                JLabel pill = new JLabel(text, SwingConstants.CENTER) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                pill.setOpaque(false);
                pill.setBackground(warn ? new Color(255, 224, 212) : new Color(204, 245, 224));
                pill.setForeground(warn ? new Color(180, 60, 20) : new Color(22, 120, 70));
                pill.setFont(nunito(Font.BOLD, 12f));
                pill.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));
                wrap.add(pill);
                return wrap;
            }
        });
    }

    private JButton createActionButton(String icon, String text, Color color) {
        final JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? color.darker()
                        : getModel().isRollover() ? color.brighter() : color;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(new Color(255, 255, 255, 120));
                g2.fillRoundRect(14, (getHeight() - 38) / 2, 38, 38, 14, 14);
                g2.setColor(NAVY);
                g2.setFont(nunito(Font.BOLD, 18f));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icon, 14 + (38 - fm.stringWidth(icon)) / 2, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(nunito(Font.BOLD, 16f));
        b.setForeground(NAVY);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(0, 70, 0, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setRolloverEnabled(true);
        return b;
    }

    // ===== Komponen custom UI =====
    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final int radius;
        private final boolean shadow;

        RoundedPanel(Color fill, int radius, boolean shadow) {
            this.fill = fill; this.radius = radius; this.shadow = shadow;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 8, h = getHeight() - 8;
            if (shadow) {
                for (int i = 4; i >= 1; i--) {
                    g2.setColor(new Color(12, 30, 66, 6));
                    g2.fillRoundRect(i, i + 2, w, h, radius + i, radius + i);
                }
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Insets getInsets() {
            Insets i = super.getInsets();
            return new Insets(i.top, i.left, i.bottom + 8, i.right + 8);
        }
    }

    private static class BannerPanel extends JPanel {
        BannerPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 170));
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            Shape card = new RoundRectangle2D.Float(0, 0, w, h, 32, 32);
            g2.setPaint(new GradientPaint(0, 0, new Color(34, 211, 238), w, 0, new Color(125, 211, 252)));
            g2.fill(card);
            g2.setClip(card);

            // dekorasi lembut
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillOval(-40, -60, 180, 180);
            g2.fillRoundRect((int) (w * 0.48), h - 70, 130, 110, 40, 40);

            // awan
            g2.setColor(new Color(255, 255, 255, 230));
            int cx = (int) (w * 0.66);
            g2.fillRoundRect(cx, 26, 110, 30, 30, 30);
            g2.fillRoundRect(w - 130, 20, 80, 22, 22, 22);

            // bintang
            g2.setColor(YELLOW);
            drawStar(g2, cx - 50, 40, 12);
            drawStar(g2, w - 30, 70, 8);

            // rumput
            g2.setColor(MINT);
            g2.fillRoundRect((int) (w * 0.62), h - 36, w, 60, 20, 20);

            // perosotan
            int sx = (int) (w * 0.80);
            g2.setColor(YELLOW);
            Path2D roof = new Path2D.Float();
            roof.moveTo(sx - 6, h - 100); roof.lineTo(sx + 36, h - 126); roof.lineTo(sx + 78, h - 100); roof.closePath();
            g2.fill(roof);
            g2.setColor(new Color(124, 92, 255));
            g2.fillRoundRect(sx, h - 102, 72, 68, 12, 12);
            g2.setColor(new Color(196, 181, 253));
            g2.fillRoundRect(sx + 12, h - 92, 40, 26, 10, 10);
            g2.setColor(CORAL);
            Path2D slide = new Path2D.Float();
            slide.moveTo(sx + 72, h - 84); slide.lineTo(sx + 130, h - 36); slide.lineTo(sx + 110, h - 36); slide.lineTo(sx + 72, h - 66); slide.closePath();
            g2.fill(slide);

            // balok A & B
            int bx = (int) (w * 0.64);
            g2.setColor(YELLOW);
            g2.fillRoundRect(bx, h - 68, 40, 34, 6, 6);
            g2.setColor(CORAL);
            g2.fillRoundRect(bx + 40, h - 68, 40, 34, 6, 6);
            g2.setColor(new Color(124, 92, 255));
            g2.fillRoundRect(bx + 20, h - 100, 36, 32, 6, 6);
            g2.setColor(Color.WHITE);
            g2.setFont(nunito(Font.BOLD, 18f));
            g2.drawString("A", bx + 31, h - 77);
            g2.setColor(NAVY);
            g2.drawString("B", bx + 14, h - 43);

            // bola
            g2.setColor(CORAL);  g2.fillOval(w - 110, h - 54, 26, 26);
            g2.setColor(YELLOW); g2.fillOval(w - 90, h - 50, 26, 26);
            g2.setColor(PURPLE); g2.fillOval(w - 68, h - 54, 26, 26);

            g2.setClip(null);

            // teks
            g2.setColor(NAVY);
            g2.setFont(nunito(Font.BOLD, 34f));
            g2.drawString("Hi, Admin!", 36, 62);
            g2.setFont(nunito(Font.BOLD, 19f));
            g2.drawString("Siap membuat hari bermain jadi lebih menyenangkan?", 36, 98);
            g2.setFont(nunito(Font.PLAIN, 14f));
            g2.setColor(new Color(12, 30, 66, 200));
            g2.drawString("Pantau aktivitas playground hari ini dalam satu tempat.", 36, 126);
            g2.dispose();
        }

        private void drawStar(Graphics2D g2, int cx, int cy, int r) {
            Path2D p = new Path2D.Float();
            p.moveTo(cx, cy - r);
            p.quadTo(cx, cy, cx + r, cy);
            p.quadTo(cx, cy, cx, cy + r);
            p.quadTo(cx, cy, cx - r, cy);
            p.quadTo(cx, cy, cx, cy - r);
            g2.fill(p);
        }
    }

    private static class BarChart extends JPanel {
        private double[] values = {0, 0, 0, 0, 0, 0};
        private String[] labels = {"09.00", "11.00", "13.00", "15.00", "17.00", "19.00"};
        private final Color[] colors = {SKY, CYAN, YELLOW, CORAL, MINT, PURPLE};

        BarChart() { setOpaque(false); setPreferredSize(new Dimension(300, 200)); }

        void setData(double[] v, String[] l) {
            if (v != null && l != null && v.length == l.length) { values = v; labels = l; }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int n = values.length;
            if (n == 0) { g2.dispose(); return; }
            double max = 0;
            for (double d : values) max = Math.max(max, d);
            int w = getWidth() - 8, h = getHeight();
            int labelH = 22, gap = 14;
            int barW = Math.max(10, (w - gap * (n - 1)) / n);
            int chartH = h - labelH - 4;
            g2.setFont(nunito(Font.PLAIN, 11f));
            FontMetrics fm = g2.getFontMetrics();
            for (int i = 0; i < n; i++) {
                int x = i * (barW + gap);
                int bh = max <= 0 ? 8 : Math.max(8, (int) (values[i] / max * (chartH - 4)));
                g2.setColor(colors[i % colors.length]);
                g2.fillRoundRect(x, chartH - bh, barW, bh + 6, 10, 10);
                g2.fillRect(x, chartH - 2, barW, 4); // dasar bar rata
                g2.setColor(MUTED);
                g2.drawString(labels[i], x + (barW - fm.stringWidth(labels[i])) / 2, h - 6);
            }
            g2.dispose();
        }
    }
}