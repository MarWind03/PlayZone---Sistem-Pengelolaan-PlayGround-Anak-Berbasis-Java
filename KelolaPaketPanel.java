package praktikum.pbo.projekakhir;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KelolaPaketPanel extends JPanel {
    private DataStore dataStore;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JTextField txtNamaPaket;
    private JTextField txtDurasi;
    private JTextField txtHarga;
    private int selectedPackageIndex = -1;

    public KelolaPaketPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblTitle = new JLabel("Kelola Paket Bermain");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        // --- BAGIAN ATAS: Tabel Paket ---
        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createTitledBorder("Daftar Paket Tersedia"));

        String[] columns = {"ID Paket", "Nama Paket", "Durasi (Menit)", "Harga (Rp)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);
        
        add(tableContainer, BorderLayout.CENTER);

        // --- BAGIAN BAWAH: Form Edit Paket ---
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createTitledBorder("Edit Paket Dipilih"));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nama Paket:"));
        txtNamaPaket = new JTextField();
        formPanel.add(txtNamaPaket);

        formPanel.add(new JLabel("Durasi (Menit):"));
        txtDurasi = new JTextField();
        formPanel.add(txtDurasi);

        formPanel.add(new JLabel("Harga (Rp):"));
        txtHarga = new JTextField();
        formPanel.add(txtHarga);

        formContainer.add(formPanel, BorderLayout.CENTER);

        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.setBackground(new Color(24, 43, 73));
        btnSimpan.setForeground(Color.WHITE);
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
}