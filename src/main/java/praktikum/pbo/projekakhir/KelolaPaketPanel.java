package praktikum.pbo.projekakhir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class KelolaPaketPanel extends JPanel {
    private DataStore dataStore;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JTextField txtNamaPaket;
    private JTextField txtDurasi;
    private JTextField txtHarga;
    private int selectedPackageIndex = -1;

    // --- Palet warna (styling saja) ---
    private static final Color NAVY      = new Color(24, 43, 73);
    private static final Color BG_PAGE   = new Color(243, 247, 253);
    private static final Color BLUE_1    = new Color(33, 150, 243);
    private static final Color BLUE_2    = new Color(66, 190, 255);
    private static final Color HEADER_BG = new Color(232, 241, 253);
    private static final Color LINE      = new Color(235, 240, 247);
    private static final Font  FONT_BASE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    public KelolaPaketPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblTitle = new JLabel("Kelola Paket Bermain");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(NAVY);
        GradientHeader headerPanel = new GradientHeader();
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- BAGIAN ATAS: Tabel Paket ---
        JPanel tableContainer = new RoundedPanel(new BorderLayout(5, 5));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(createCardBorder("Daftar Paket Tersedia"));

        String[] columns = {"ID Paket", "Nama Paket", "Durasi (Menit)", "Harga (Rp)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        add(tableContainer, BorderLayout.CENTER);

        // --- BAGIAN BAWAH: Form Edit Paket ---
        JPanel formContainer = new RoundedPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(createCardBorder("Edit Paket Dipilih"));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        formPanel.add(createFormLabel("Nama Paket:"));
        txtNamaPaket = new JTextField();
        styleTextField(txtNamaPaket);
        formPanel.add(txtNamaPaket);

        formPanel.add(createFormLabel("Durasi (Menit):"));
        txtDurasi = new JTextField();
        styleTextField(txtDurasi);
        formPanel.add(txtDurasi);

        formPanel.add(createFormLabel("Harga (Rp):"));
        txtHarga = new JTextField();
        styleTextField(txtHarga);
        formPanel.add(txtHarga);

        formContainer.add(formPanel, BorderLayout.CENTER);

        JButton btnSimpan = new GradientButton("Simpan Perubahan");
        formContainer.add(btnSimpan, BorderLayout.SOUTH);

        add(formContainer, BorderLayout.SOUTH);

        // --- EVENT LISTENER ---
        
        // Memasukkan data dari tabel ke form saat baris diklik
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                selectedPackageIndex = table.getSelectedRow();
                PaketBermain p = dataStore.getDaftarPaket().get(selectedPackageIndex);
                txtNamaPaket.setText(p.getNamaPaket());
                txtDurasi.setText(String.valueOf(p.getDurasiMenit()));
                txtHarga.setText(String.valueOf(p.getHarga()));
            }
        });

        // Menyimpan perubahan ke dalam DataStore (Menerapkan Encapsulation)
        btnSimpan.addActionListener(e -> {
            if (selectedPackageIndex == -1) {
                JOptionPane.showMessageDialog(this, "Pilih paket dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String namaBaru = txtNamaPaket.getText();
                int durasiBaru = Integer.parseInt(txtDurasi.getText());
                double hargaBaru = Double.parseDouble(txtHarga.getText());

                PaketBermain paketDipilih = dataStore.getDaftarPaket().get(selectedPackageIndex);
                
                // Memanggil method mutator (Encapsulation)
                paketDipilih.ubahNama(namaBaru);
                paketDipilih.ubahDurasi(durasiBaru);
                paketDipilih.ubahHarga(hargaBaru);

                refreshData();
                JOptionPane.showMessageDialog(this, "Paket berhasil diperbarui!");
                
                // Reset form
                selectedPackageIndex = -1;
                txtNamaPaket.setText("");
                txtDurasi.setText("");
                txtHarga.setText("");
                table.clearSelection();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Durasi dan Harga harus berupa angka valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshData(); // Muat data awal
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        for (PaketBermain p : dataStore.getDaftarPaket()) {
            tableModel.addRow(new Object[]{
                "PKT-" + (dataStore.getDaftarPaket().indexOf(p) + 1), // ID Dummy visual
                p.getNamaPaket(), 
                p.getDurasiMenit(), 
                String.format("%,.0f", p.getHarga())
            });
        }
    }

    // =====================================================================
    //  HELPER STYLING (tidak mengandung logika aplikasi)
    // =====================================================================

    private javax.swing.border.Border createCardBorder(String title) {
        javax.swing.border.TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), title);
        tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 15));
        tb.setTitleColor(NAVY);
        return BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(14, 18, 14, 18), tb);
    }

    private JLabel createFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BOLD);
        l.setForeground(NAVY);
        return l;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(FONT_BASE);
        tf.setForeground(NAVY);
        tf.setBackground(new Color(250, 252, 255));
        // Properti FlatLaf (diabaikan jika bukan FlatLaf)
        tf.putClientProperty("FlatLaf.style",
                "arc: 16; borderColor: #D5E0F0; focusedBorderColor: #2196F3; margin: 6,14,6,14");
    }

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
        h.setPreferredSize(new Dimension(0, 40));
        h.putClientProperty("FlatLaf.style",
                "background: #E8F1FD; separatorColor: #E8F1FD; bottomSeparatorColor: #E8F1FD");

        // Padding kiri sel (visual saja)
        javax.swing.table.DefaultTableCellRenderer r = new javax.swing.table.DefaultTableCellRenderer();
        r.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 6));
        t.setDefaultRenderer(Object.class, r);
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

    /** Tombol bergradasi biru dengan sudut membulat. */
    private static class GradientButton extends JButton {
        GradientButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 44));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c1 = getModel().isPressed() ? BLUE_1.darker() : BLUE_1;
            Color c2 = getModel().isRollover() ? BLUE_2.brighter() : BLUE_2;
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}