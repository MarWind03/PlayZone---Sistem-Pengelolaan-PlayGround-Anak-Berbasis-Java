package praktikum.pbo.projekakhir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private DataStore dataStore;
    private JLabel lblAnakDiDalam, lblKapasitas, lblSesiAktif;
    private JTable table;
    private DefaultTableModel tableModel;

    public DashboardPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        JLabel lblGreeting = new JLabel("Hi, Admin PlayZone!");
        lblGreeting.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel lblSub = new JLabel("Pantau aktivitas playground hari ini dalam satu tempat.");
        lblSub.setForeground(Color.GRAY);
        headerPanel.add(lblGreeting);
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Stat Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 90));

        lblAnakDiDalam = new JLabel("0");
        lblKapasitas = new JLabel("25");
        lblSesiAktif = new JLabel("0");
        
        statsPanel.add(createCard("Anak di Dalam", lblAnakDiDalam, new Color(225, 240, 255)));
        statsPanel.add(createCard("Kapasitas Tersedia", lblKapasitas, new Color(255, 243, 205)));
        statsPanel.add(createCard("Sesi Aktif", lblSesiAktif, new Color(254, 226, 226)));
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Tabel Sesi
        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));
        tableContainer.setBorder(BorderFactory.createTitledBorder("Sesi Bermain Aktif"));
        tableContainer.setBackground(Color.WHITE);

        String[] columns = {"ID Tiket", "Nama Anak", "Paket Bermain", "Sisa Waktu", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        refreshData(); // Muat data awal
    }

    private JPanel createCard(String title, JLabel lblVal, Color bgColor) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 22));

        card.add(lblTitle);
        card.add(lblVal);
        return card;
    }

    // Fungsi untuk memperbarui tampilan saat ada transaksi baru
    public void refreshData() {
        lblAnakDiDalam.setText(String.valueOf(dataStore.getAnakDiDalam()));
        lblKapasitas.setText(String.valueOf(dataStore.getKapasitasTersedia()));
        lblSesiAktif.setText(String.valueOf(dataStore.getTiketAktif().size()));

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
}