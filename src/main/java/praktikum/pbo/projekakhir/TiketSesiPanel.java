
package praktikum.pbo.projekakhir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TiketSesiPanel extends JPanel {
    private DataStore dataStore;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblKapasitas;

    // --- Palet warna & font (styling saja) ---
    private static final Color NAVY      = new Color(24, 43, 73);
    private static final Color BG_PAGE   = new Color(243, 247, 253);
    private static final Color HEADER_BG = new Color(232, 241, 253);
    private static final Color LINE      = new Color(235, 240, 247);
    private static final Font  FONT_BASE = AppFont.regular(13f);
    private static final Font  FONT_BOLD = AppFont.semiBold(13f);

    public TiketSesiPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Pantau Tiket & Sesi Bermain");
        lblTitle.setFont(AppFont.bold(26f));
        lblTitle.setForeground(NAVY);
        GradientHeader headerPanel = new GradientHeader();
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- WADAH KONTEN: KARTU KAPASITAS DAN TABEL ---
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);

        // --- KARTU KAPASITAS TERSEDIA ---
        JPanel kapasitasCard = new RoundedPanel(new BorderLayout(15, 0));
        kapasitasCard.setBackground(Color.WHITE);
        kapasitasCard.setBorder(
                BorderFactory.createEmptyBorder(15, 22, 15, 22)
        );
        kapasitasCard.setPreferredSize(new Dimension(0, 90));

        JPanel kapasitasInfo = new JPanel(new GridLayout(2, 1, 0, 4));
        kapasitasInfo.setOpaque(false);

        JLabel lblTitleKapasitas = new JLabel("Kapasitas Tersedia");
        lblTitleKapasitas.setFont(AppFont.semiBold(14f));
        lblTitleKapasitas.setForeground(NAVY);

        lblKapasitas = new JLabel(
                String.valueOf(dataStore.getKapasitasTersedia())
        );
        lblKapasitas.setFont(AppFont.bold(28f));
        lblKapasitas.setForeground(new Color(46, 140, 110));

        kapasitasInfo.add(lblTitleKapasitas);
        kapasitasInfo.add(lblKapasitas);

        JLabel lblKeterangan = new JLabel("Slot yang masih bisa digunakan");
        lblKeterangan.setFont(AppFont.regular(12f));
        lblKeterangan.setForeground(new Color(130, 144, 163));

        kapasitasCard.add(kapasitasInfo, BorderLayout.CENTER);
        kapasitasCard.add(lblKeterangan, BorderLayout.EAST);

        contentPanel.add(kapasitasCard, BorderLayout.NORTH);

        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));
        tableContainer.setOpaque(false);

        String[] columns = {"ID Tiket", "Nama Anak", "Paket Bermain", "Sisa Waktu", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Kartu putih membulat pembungkus tabel (styling saja)
        JPanel tableCard = new RoundedPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(tableCard, BorderLayout.CENTER);

        // --- WADAH TOMBOL AKSI DI BAWAH ---
        JPanel actionContainer = new JPanel(new BorderLayout(10, 0));
        actionContainer.setOpaque(false);
        actionContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Kita gunakan GridLayout (1 baris, 3 kolom) agar tombol sejajar dan rapi
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        buttonPanel.setOpaque(false);

        // TOMBOL 1: AKTIFKAN SESI (HIJAU)
        JButton btnAktifkanSesi = new RoundedButton("Aktifkan Sesi (Masuk/Lanjut)",
                new Color(46, 204, 113), new Color(39, 174, 96));
        btnAktifkanSesi.setBackground(new Color(40, 167, 69));
        btnAktifkanSesi.setForeground(Color.WHITE);

        btnAktifkanSesi.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) return;

            int sukses = 0;
            for (int row : selectedRows) {
                String idTiket = (String) tableModel.getValueAt(row, 0);
                Tiket t = dataStore.getTiketById(idTiket);

                // Bisa mengaktifkan yang sedang "Menunggu" ATAU melanjutkan yang sedang "Jeda"
                if (t != null && (t.getStatus().equals("Menunggu") || t.getStatus().equals("Jeda"))) {
                    // Hanya cek kapasitas jika anak benar-benar baru mau masuk dari "Menunggu"
                    if (t.getStatus().equals("Menunggu")) {
                        if (dataStore.getKapasitasTersedia() > 0) {
                            t.setStatus("Aktif");
                            sukses++;
                        } else {
                            JOptionPane.showMessageDialog(this, "Kapasitas penuh! Sisa anak gagal dimasukkan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            break;
                        }
                    } else if (t.getStatus().equals("Jeda")) {
                        // Jika dari "Jeda" ke "Aktif", tidak memakan kapasitas baru
                        t.setStatus("Aktif");
                        sukses++;
                    }
                }
            }
            if (sukses > 0) {
                table.clearSelection();
                refreshData();
            }
        });

        // TOMBOL 2: JEDA/PAUSE SESI (KUNING)
        JButton btnJedaSesi = new RoundedButton("Jeda (Pause) Waktu",
                new Color(255, 214, 64), new Color(255, 183, 0));
        btnJedaSesi.setBackground(new Color(255, 193, 7)); // Kuning
        btnJedaSesi.setForeground(Color.BLACK);

        btnJedaSesi.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) return;

            for (int row : selectedRows) {
                String idTiket = (String) tableModel.getValueAt(row, 0);
                Tiket t = dataStore.getTiketById(idTiket);

                if (t != null && t.getStatus().equals("Aktif")) {
                    t.setStatus("Jeda");
                }
            }
            table.clearSelection();
            refreshData();
        });

        // TOMBOL 3: KONFIRMASI KELUAR (MERAH)
        JButton btnKonfirmasiKeluar = new RoundedButton("Konfirmasi Anak Keluar",
                new Color(255, 99, 99), new Color(235, 64, 64));
        btnKonfirmasiKeluar.setBackground(new Color(220, 53, 69));
        btnKonfirmasiKeluar.setForeground(Color.WHITE);

        btnKonfirmasiKeluar.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) return;

            List<String> idsToRemove = new ArrayList<>();
            for (int row : selectedRows) {
                idsToRemove.add((String) tableModel.getValueAt(row, 0));
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Keluarkan " + selectedRows.length + " anak dari playground?",
                "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                for (String id : idsToRemove) {
                    dataStore.konfirmasiKeluar(id);
                }
                table.clearSelection();
                refreshData();
            }
        });

        buttonPanel.add(btnAktifkanSesi);
        buttonPanel.add(btnJedaSesi);
        buttonPanel.add(btnKonfirmasiKeluar);

        actionContainer.add(buttonPanel, BorderLayout.CENTER);
        tableContainer.add(actionContainer, BorderLayout.SOUTH);

        // Tabel dan tombol tetap berada di bawah kartu kapasitas
        contentPanel.add(tableContainer, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        int[] selectedRows = table.getSelectedRows();
        List<String> selectedIds = new ArrayList<>();
        for (int row : selectedRows) {
            selectedIds.add((String) tableModel.getValueAt(row, 0));
        }

        tableModel.setRowCount(0);
        for (Tiket t : dataStore.getTiketAktif()) {
            tableModel.addRow(new Object[]{
                t.getIdTiket(), t.getNamaAnak(),
                t.getPaket().getNamaPaket(),
                t.getFormatSisaWaktu(),
                t.getStatus()
            });
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String currentId = (String) tableModel.getValueAt(i, 0);
            if (selectedIds.contains(currentId)) {
                table.addRowSelectionInterval(i, i);
            }
        }

        // Memperbarui tampilan kapasitas dari DataStore
        lblKapasitas.setText(
                String.valueOf(dataStore.getKapasitasTersedia())
        );
    }

    // =====================================================================
    //  HELPER STYLING (tidak mengandung logika aplikasi)
    // =====================================================================

    private void styleTable(JTable t) {
        t.setFont(FONT_BASE);
        t.setForeground(NAVY);
        t.setBackground(Color.WHITE);
        t.setRowHeight(40);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(LINE);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(new Color(220, 235, 255));
        t.setSelectionForeground(NAVY);
        t.setFillsViewportHeight(true);

        javax.swing.table.JTableHeader h = t.getTableHeader();
        h.setFont(FONT_BOLD);
        h.setForeground(NAVY);
        h.setBackground(HEADER_BG);
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 40));
        h.putClientProperty("FlatLaf.style",
                "background: #E8F1FD; separatorColor: #E8F1FD; bottomSeparatorColor: #E8F1FD");

        // Header dan sel rata tengah, padding visual saja
        javax.swing.table.DefaultTableCellRenderer cell = new javax.swing.table.DefaultTableCellRenderer();
        cell.setHorizontalAlignment(SwingConstants.CENTER);
        t.setDefaultRenderer(Object.class, cell);

        javax.swing.table.TableCellRenderer baseHeader = h.getDefaultRenderer();
        h.setDefaultRenderer((tbl, value, sel, foc, row, col) -> {
            Component c = baseHeader.getTableCellRendererComponent(tbl, value, sel, foc, row, col);
            if (c instanceof JLabel) {
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                ((JLabel) c).setBackground(HEADER_BG);
                ((JLabel) c).setForeground(NAVY);
                ((JLabel) c).setFont(FONT_BOLD);
            }
            return c;
        });
    }

    /** Pemuat font Poppins (cadangan: Segoe UI). Styling saja. */
    private static final class AppFont {
        private static final Font REGULAR  = load("/fonts/Poppins-Regular.ttf",  Font.PLAIN);
        private static final Font SEMIBOLD = load("/fonts/Poppins-SemiBold.ttf", Font.BOLD);
        private static final Font BOLD     = load("/fonts/Poppins-Bold.ttf",     Font.BOLD);

        static Font regular(float size)  { return REGULAR.deriveFont(size); }
        static Font semiBold(float size) { return SEMIBOLD.deriveFont(size); }
        static Font bold(float size)     { return BOLD.deriveFont(size); }

        private static Font load(String path, int fallbackStyle) {
            try (InputStream in = AppFont.class.getResourceAsStream(path)) {
                if (in == null) throw new IllegalStateException("Font tidak ditemukan: " + path);
                Font f = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            } catch (Exception e) {
                return new Font("Segoe UI", fallbackStyle, 13);
            }
        }
    }

    /** Panel putih dengan sudut membulat + bayangan halus. */
    private static class RoundedPanel extends JPanel {
        RoundedPanel(LayoutManager lm) {
            super(lm);
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(24, 43, 73, 14));
            g2.fill(new RoundRectangle2D.Float(2, 3, w - 4, h - 4, 26, 26));
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, w - 3, h - 4, 26, 26));
            g2.setColor(new Color(225, 233, 245));
            g2.draw(new RoundRectangle2D.Float(0, 0, w - 4, h - 5, 26, 26));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Latar header bergradasi biru muda dengan sudut membulat. */
    private static class GradientHeader extends JPanel {
        GradientHeader() {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(26, 28, 26, 28));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(150, 210, 250),
                    getWidth(), 0, new Color(205, 232, 252)));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 28, 28));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Tombol bergradasi vertikal dengan sudut membulat. */
    private static class RoundedButton extends JButton {
        private final Color top, bottom;
        RoundedButton(String text, Color top, Color bottom) {
            super(text);
            this.top = top;
            this.bottom = bottom;
            setFont(AppFont.semiBold(14f));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 56));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c1 = getModel().isPressed() ? top.darker() : top;
            Color c2 = getModel().isPressed() ? bottom.darker() : bottom;
            if (getModel().isRollover()) { c1 = c1.brighter(); c2 = c2.brighter(); }
            g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
