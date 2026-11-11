package praktikum.pbo.projekakhir;

import javax.swing.*;
import java.awt.*;

public class LaporanPanel extends JPanel {
    private DataStore dataStore;
    private JLabel lblTotalTiket;
    private JLabel lblTotalPendapatan;
    private JLabel lblStatusKapasitas;

    public LaporanPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblTitle = new JLabel("Kapasitas & Laporan Operasional");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        centerPanel.setOpaque(false);

        // Bagian Laporan Keuangan/Tiket
        JPanel laporanCard = new JPanel(new GridLayout(2, 1, 10, 10));
        laporanCard.setBackground(Color.WHITE);
        laporanCard.setBorder(BorderFactory.createTitledBorder("Rekapitulasi Hari Ini"));
        
        lblTotalTiket = new JLabel("Total Tiket Terjual: 0 Tiket");
        lblTotalTiket.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        lblTotalPendapatan = new JLabel("Total Pendapatan: Rp0");
        lblTotalPendapatan.setFont(new Font("SansSerif", Font.BOLD, 18));

        laporanCard.add(lblTotalTiket);
        laporanCard.add(lblTotalPendapatan);
        centerPanel.add(laporanCard);

        // Bagian Status Kapasitas
        JPanel kapasitasCard = new JPanel(new BorderLayout());
        kapasitasCard.setBackground(Color.WHITE);
        kapasitasCard.setBorder(BorderFactory.createTitledBorder("Status Kapasitas Saat Ini"));

        lblStatusKapasitas = new JLabel("Kapasitas Aman (Tersedia: 30 / 30)", JLabel.CENTER);
        lblStatusKapasitas.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblStatusKapasitas.setForeground(new Color(40, 167, 69)); // Hijau
        
        kapasitasCard.add(lblStatusKapasitas, BorderLayout.CENTER);
        centerPanel.add(kapasitasCard);

        add(centerPanel, BorderLayout.CENTER);
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