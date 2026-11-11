package praktikum.pbo.projekakhir;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private DataStore dataStore;

    private DashboardPanel dashboardPanel;
    private TiketSesiPanel tiketSesiPanel; // Tambahan Panel Baru
    private TransaksiPanel transaksiPanel;

    public MainFrame() {
        dataStore = new DataStore();

        setTitle("PlayZone - Sistem Pengelolaan Playground");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 650));
        sidebar.setBackground(new Color(24, 43, 73));
        sidebar.setLayout(new GridLayout(10, 1, 0, 8));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel lblBrand = new JLabel("PlayZone", JLabel.CENTER);
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);
        sidebar.add(lblBrand);

        JButton btnNavDashboard = new JButton("Dashboard");
        JButton btnNavTransaksi = new JButton("Transaksi & Kasir");
        JButton btnNavKelolaPaket = new JButton("Kelola Paket"); // Tombol Baru
        JButton btnNavTiketSesi = new JButton("Tiket & Sesi");
        JButton btnNavKapasitas = new JButton("Kapasitas & Laporan");

        sidebar.add(btnNavDashboard);
        sidebar.add(btnNavTransaksi);
        sidebar.add(btnNavKelolaPaket); // Masukkan ke sidebar
        sidebar.add(btnNavTiketSesi);
        sidebar.add(btnNavKapasitas); // Taruh di bawah btnNavTiketSesi
        add(sidebar, BorderLayout.WEST);

        // Content Container
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        dashboardPanel = new DashboardPanel(dataStore);
        tiketSesiPanel = new TiketSesiPanel(dataStore);
        transaksiPanel = new TransaksiPanel(dataStore); // Inisialisasi ke variabel
        KelolaPaketPanel kelolaPaketPanel = new KelolaPaketPanel(dataStore); // Panel Baru
        LaporanPanel laporanPanel = new LaporanPanel(dataStore);

        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(transaksiPanel, "TRANSAKSI");
        mainContentPanel.add(kelolaPaketPanel, "KELOLA_PAKET"); // Masukkan ke wadah
        mainContentPanel.add(tiketSesiPanel, "TIKET_SESI");
        mainContentPanel.add(laporanPanel, "LAPORAN");

        add(mainContentPanel, BorderLayout.CENTER);

        // Navigasi
        btnNavDashboard.addActionListener(e -> {
            dashboardPanel.refreshData();
            cardLayout.show(mainContentPanel, "DASHBOARD");
        });
        btnNavTransaksi.addActionListener(e -> {
            transaksiPanel.refreshPaketUI(); // Panggil fungsi refresh di sini
            cardLayout.show(mainContentPanel, "TRANSAKSI");
        });
        btnNavTiketSesi.addActionListener(e -> {
            tiketSesiPanel.refreshData();
            cardLayout.show(mainContentPanel, "TIKET_SESI");
        });

        btnNavKelolaPaket.addActionListener(e -> {
            kelolaPaketPanel.refreshData();
            cardLayout.show(mainContentPanel, "KELOLA_PAKET");
        });

        btnNavKapasitas.addActionListener(e -> {
            laporanPanel.refreshData();
            cardLayout.show(mainContentPanel, "LAPORAN");
        });

        // ==========================================
        // SIMULASI WAKTU BERJALAN (TIMER)
        // 1000 ms (1 detik) = 1 Menit waktu bermain
        // ==========================================
        Timer gameTimer = new Timer(1000, e -> {
            dataStore.kurangiWaktuSemuaTiket();
            // Update UI secara realtime jika panel sedang dibuka
            dashboardPanel.refreshData();
            tiketSesiPanel.refreshData();
        });
        gameTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}