package praktikum.pbo.projekakhir;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class MainFrame extends JFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private DataStore dataStore;

    private DashboardPanel dashboardPanel;
    private TiketSesiPanel tiketSesiPanel;
    private TransaksiPanel transaksiPanel;
    private LaporanPanel laporanPanel; // FIX: dijadikan field agar bisa di-refresh

    private LoginFrame loginPanel;
    private JPanel rootPanel;
    private CardLayout rootLayout;

    private JPanel sidebar;
    private JPanel menuPanel;

    private static final Color NAVY = new Color(24, 43, 73);
    private static final Color BLUE = new Color(35, 145, 245);
    private static final Color WHITE = new Color(250, 252, 255);

    // Constructor utama
    public MainFrame() {

        dataStore = new DataStore();

        setTitle("PlayZone - Sistem Pengelolaan Playground");
        setSize(1000, 650);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Panel utama untuk Login dan Aplikasi
        rootLayout = new CardLayout();
        rootPanel = new JPanel(rootLayout);

        // Login Panel
        loginPanel = new LoginFrame();
        loginPanel.setLoginListener(role -> tampilkanMenu(role));

        rootPanel.add(loginPanel, "LOGIN");

        // Membuat tampilan aplikasi
        buatHalamanAplikasi();

        rootPanel.add(buatContainerAplikasi(), "APP");

        setContentPane(rootPanel);

        rootLayout.show(rootPanel, "LOGIN");

        // Simulasi waktu berjalan
        Timer gameTimer = new Timer(1000, e -> {
            dataStore.kurangiWaktuSemuaTiket();
            dashboardPanel.refreshData();
            tiketSesiPanel.refreshData();
            laporanPanel.refreshData(); // FIX: laporan ikut diperbarui
        });

        gameTimer.start();
    }

    // Membuat halaman aplikasi
    private void buatHalamanAplikasi() {

        // Sidebar
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setPaint(new GradientPaint(
                        0, 0,
                        new Color(30, 54, 88),
                        0, getHeight(),
                        new Color(12, 32, 62)
                ));

                g2.fillRect(0, 0, getWidth(), getHeight());

                Path2D wave = new Path2D.Float();

                wave.moveTo(0, getHeight() - 45);
                wave.curveTo(
                        45, getHeight() - 125,
                        150, getHeight() - 145,
                        getWidth(), getHeight() - 165
                );

                wave.lineTo(getWidth(), getHeight());
                wave.lineTo(0, getHeight());
                wave.closePath();

                g2.setColor(new Color(75, 125, 185, 45));
                g2.fill(wave);

                g2.dispose();
            }
        };

        sidebar.setPreferredSize(new Dimension(220, 650));
        sidebar.setLayout(new BorderLayout(0, 8));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        );

        JLabel lblBrand = new JLabel("PlayZone", JLabel.CENTER);
        lblBrand.setFont(new Font("Poppins", Font.BOLD, 23));
        lblBrand.setForeground(Color.WHITE);

        sidebar.add(lblBrand, BorderLayout.NORTH);

        // Tempat tombol navigasi
        menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(
                new BoxLayout(menuPanel, BoxLayout.Y_AXIS)
        );

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Tombol Keluar
        JButton btnKeluar = new JButton("Keluar");
        stylingTombol(btnKeluar);

        sidebar.add(btnKeluar, BorderLayout.SOUTH);

        btnKeluar.addActionListener(e -> {
            loginPanel.reset();
            rootLayout.show(rootPanel, "LOGIN");
        });

        // Content Container
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        dashboardPanel = new DashboardPanel(dataStore);
        tiketSesiPanel = new TiketSesiPanel(dataStore);
        transaksiPanel = new TransaksiPanel(dataStore);

        KelolaPaketPanel kelolaPaketPanel =
                new KelolaPaketPanel(dataStore);

        laporanPanel = new LaporanPanel(dataStore); // FIX: tidak lagi variabel lokal

        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(transaksiPanel, "TRANSAKSI");
        mainContentPanel.add(kelolaPaketPanel, "KELOLA_PAKET");
        mainContentPanel.add(tiketSesiPanel, "TIKET_SESI");
        mainContentPanel.add(laporanPanel, "LAPORAN");

        // Navigasi Dashboard
        // Navigasi lainnya diatur saat role dipilih.
    }

    // Container halaman aplikasi
    private JPanel buatContainerAplikasi() {

        JPanel appPanel = new JPanel(new BorderLayout());

        appPanel.add(sidebar, BorderLayout.WEST);
        appPanel.add(mainContentPanel, BorderLayout.CENTER);

        return appPanel;
    }

    // Menampilkan menu sesuai role
    private void tampilkanMenu(String role) {

        menuPanel.removeAll();

        if (role.equals("admin")) {

            JButton btnNavDashboard = new JButton("Dashboard");
            JButton btnNavKelolaPaket = new JButton("Kelola Paket");
            JButton btnNavKapasitas = new JButton("Kapasitas & Laporan");
            JButton btnNavResetHari = new JButton("Reset Hari");

            tambahNavigasi(btnNavDashboard, () -> {
                dashboardPanel.refreshData();
                cardLayout.show(mainContentPanel, "DASHBOARD");
            });

            tambahNavigasi(btnNavKelolaPaket, () -> {
                cardLayout.show(mainContentPanel, "KELOLA_PAKET");
            });

            tambahNavigasi(btnNavKapasitas, () -> {
                laporanPanel.refreshData(); // FIX: ambil data terbaru saat tab dibuka
                cardLayout.show(mainContentPanel, "LAPORAN");
            });

            tambahNavigasi(btnNavResetHari, () -> {
                resetHari();
            });
            stylingTombolBahaya(btnNavResetHari); // BARU: tombol Reset Hari berwarna merah lembut

            dashboardPanel.refreshData();
            cardLayout.show(mainContentPanel, "DASHBOARD");

        } else if (role.equals("karyawan")) {

            JButton btnNavTransaksi = new JButton("Transaksi & Kasir");
            JButton btnNavTiketSesi = new JButton("Tiket & Sesi");

            tambahNavigasi(btnNavTransaksi, () -> {
                transaksiPanel.refreshPaketUI();
                cardLayout.show(mainContentPanel, "TRANSAKSI");
            });

            tambahNavigasi(btnNavTiketSesi, () -> {
                tiketSesiPanel.refreshData();
                cardLayout.show(mainContentPanel, "TIKET_SESI");
            });

            transaksiPanel.refreshPaketUI();
            cardLayout.show(mainContentPanel, "TRANSAKSI");
        }

        menuPanel.revalidate();
        menuPanel.repaint();

        rootLayout.show(rootPanel, "APP");
    }

    // Konfirmasi, arsipkan data hari ini, lalu mulai hari baru dari 0
    private void resetHari() {
        int anakDiDalam = dataStore.getAnakDiDalam();

        String pesan = "Data hari ini akan disimpan ke riwayat, lalu hari baru dimulai dari 0.\n"
                + "Total tiket, pendapatan, dan daftar sesi akan dikosongkan.";
        if (anakDiDalam > 0) {
            pesan += "\n\nPerhatian: masih ada " + anakDiDalam
                    + " anak di dalam. Sesi mereka juga akan ditutup.";
        }
        pesan += "\n\nYakin ingin melanjutkan?";

        // BARU: tombol berbahasa Indonesia, "Batal" jadi pilihan default
        Object[] pilihanTombol = {"Ya, Reset", "Batal"};
        int pilihan = JOptionPane.showOptionDialog(
                this, pesan, "Reset Hari",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, pilihanTombol, pilihanTombol[1]);

        if (pilihan != 0) {
            return;
        }

        if (dataStore.resetHari()) {
            dashboardPanel.refreshData();
            tiketSesiPanel.refreshData();
            laporanPanel.refreshData();
            JOptionPane.showMessageDialog(this,
                    "Data hari ini sudah diarsipkan. Hari baru dimulai dari 0.",
                    "Reset Hari", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan arsip. Data hari ini tidak diubah.",
                    "Reset Hari", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Menambahkan tombol navigasi
    private void tambahNavigasi(JButton button, Runnable action) {

        stylingTombol(button);

        button.addActionListener(e -> action.run());

        menuPanel.add(button);
        menuPanel.add(Box.createVerticalStrut(8));
    }

    // Styling tombol navigasi
    private void stylingTombol(JButton button) {

        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setForeground(NAVY);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 234, 246), 1
                ),
                BorderFactory.createEmptyBorder(10, 14, 10, 10)
        ));

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("FlatLaf.style", "arc: 18");

        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(190, 45));
    }

    // BARU: styling khusus tombol berisiko (merah lembut, bukan merah menyala)
    private void stylingTombolBahaya(JButton button) {

        Color merahTeks = new Color(185, 40, 40);

        button.setForeground(merahTeks);
        button.setBackground(new Color(255, 236, 236));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(245, 190, 190), 1
                ),
                BorderFactory.createEmptyBorder(10, 14, 10, 10)
        ));

        // Warna hover & saat ditekan ikut merah (bawaan FlatLaf-nya biru)
        button.putClientProperty("FlatLaf.style",
                "arc: 18; hoverBackground: #FFD9D9; pressedBackground: #FFC4C4");
    }

    public static void main(String[] args) {

        com.formdev.flatlaf.FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}