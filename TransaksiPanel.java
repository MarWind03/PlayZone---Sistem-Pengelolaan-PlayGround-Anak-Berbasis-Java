package praktikum.pbo.projekakhir;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    public TransaksiPanel(DataStore dataStore) {
        this.dataStore = dataStore;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Transaksi & Pembayaran Tiket");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        // --- PANEL KIRI ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Data Pengunjung"));
        formPanel.setBackground(Color.WHITE);
        formPanel.add(new JLabel("Nama Anak:"));
        txtNamaAnak = new JTextField();
        formPanel.add(txtNamaAnak);
        leftPanel.add(formPanel, BorderLayout.NORTH);

        // Pilihan Paket
        JPanel paketPanel = new JPanel(new BorderLayout(10, 10));
        paketPanel.setBorder(BorderFactory.createTitledBorder("Pilih Paket Bermain"));
        paketPanel.setBackground(Color.WHITE);
        
        radioPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        radioPanel.setOpaque(false);
        paketPanel.add(radioPanel, BorderLayout.CENTER);

        JButton btnTambahAnak = new JButton("+ Tambah ke Transaksi");
        paketPanel.add(btnTambahAnak, BorderLayout.SOUTH);
        
        leftPanel.add(paketPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.CENTER);

        // --- PANEL KANAN ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel infoHeader = new JPanel(new GridLayout(1, 2));
        infoHeader.setOpaque(false);
        lblAnakHeader = new JLabel("Tiket: 0 Anak");
        lblTotalBiayaHeader = new JLabel("Total: Rp0");
        lblTotalBiayaHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoHeader.add(lblAnakHeader);
        infoHeader.add(lblTotalBiayaHeader);
        rightPanel.add(infoHeader, BorderLayout.NORTH);

        txtRingkasan = new JTextArea();
        txtRingkasan.setEditable(false);
        rightPanel.add(new JScrollPane(txtRingkasan), BorderLayout.CENTER);

        JPanel paymentAction = new JPanel(new GridLayout(3, 1, 5, 5));
        paymentAction.setOpaque(false);
        paymentAction.add(new JLabel("Metode Bayar:"));
        JComboBox<String> cmbBayar = new JComboBox<>(new String[]{"Tunai (Cash)", "QRIS"});
        paymentAction.add(cmbBayar);

        JButton btnBayar = new JButton("Bayar & Terbitkan Tiket");
        btnBayar.setBackground(new Color(24, 43, 73));
        btnBayar.setForeground(Color.WHITE);
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

    // FUNGSI BARU: Untuk me-refresh ulang daftar RadioButton paket
    public void refreshPaketUI() {
        radioPanel.removeAll(); // Hapus pilihan lama
        bgPaket = new ButtonGroup();
        
        for (PaketBermain paket : dataStore.getDaftarPaket()) {
            JRadioButton rb = new JRadioButton(paket.toString());
            rb.setActionCommand(paket.getNamaPaket());
            bgPaket.add(rb);
            radioPanel.add(rb);
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