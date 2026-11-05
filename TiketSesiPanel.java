package praktikum.pbo.projekakhir;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TiketSesiPanel extends JPanel {
    private DataStore dataStore;
    private JTable table;
    private DefaultTableModel tableModel;

    public TiketSesiPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Pantau Tiket & Sesi Bermain");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));
        tableContainer.setBackground(Color.WHITE);

        String[] columns = {"ID Tiket", "Nama Anak", "Paket Bermain", "Sisa Waktu", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- WADAH TOMBOL AKSI DI BAWAH ---
        JPanel actionContainer = new JPanel(new BorderLayout(10, 0));
        actionContainer.setOpaque(false);
        actionContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Kita gunakan GridLayout (1 baris, 3 kolom) agar tombol sejajar dan rapi
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setOpaque(false);

        // TOMBOL 1: AKTIFKAN SESI (HIJAU)
        JButton btnAktifkanSesi = new JButton("Aktifkan Sesi (Masuk/Lanjut)");
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
        JButton btnJedaSesi = new JButton("Jeda (Pause) Waktu");
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
        JButton btnKonfirmasiKeluar = new JButton("Konfirmasi Anak Keluar");
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

        add(tableContainer, BorderLayout.CENTER);
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
    }
}