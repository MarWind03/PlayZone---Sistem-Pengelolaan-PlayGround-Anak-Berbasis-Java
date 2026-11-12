package praktikum.pbo.projekakhir;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginFrame extends JPanel {

    // Akun bawaan (email, password) untuk tiap role
    private static final String ADMIN_EMAIL = "admin@playzone.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String PEGAWAI_EMAIL = "pegawai@playzone.com";
    private static final String PEGAWAI_PASSWORD = "pegawai123";

    private static final Color NAVY = new Color(20, 43, 80);
    private static final Color ABU = new Color(110, 120, 135);
    private static final Color MERAH = new Color(200, 45, 45);

    private static final String HALAMAN_PILIH = "PILIH";
    private static final String HALAMAN_FORM = "FORM";

    private final CardLayout cardLayout = new CardLayout();

    private JLabel lblJudulForm;
    private JLabel lblIkonForm;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JLabel lblError;
    private JButton btnMasuk;

    private String roleDipilih = "admin";
    private Consumer<String> loginListener;

    public LoginFrame() {
        setLayout(cardLayout);
        setBackground(new Color(240, 248, 255));

        add(buatHalamanPilih(), HALAMAN_PILIH);
        add(buatHalamanForm(), HALAMAN_FORM);

        cardLayout.show(this, HALAMAN_PILIH);
    }

    // Listener dipanggil dengan "admin" atau "karyawan" setelah login berhasil
    public void setLoginListener(Consumer<String> listener) {
        this.loginListener = listener;
    }

    // Kembali ke pilihan akun dan kosongkan form (dipakai saat Keluar)
    public void reset() {
        bersihkanForm();
        cardLayout.show(this, HALAMAN_PILIH);
    }

    // ==========================================
    // HALAMAN 1: PILIH AKUN
    // ==========================================
    private JPanel buatHalamanPilih() {
        JPanel halaman = new JPanel(new GridBagLayout());
        halaman.setBackground(new Color(240, 248, 255));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("PlayZone");
        logo.setFont(new Font("SansSerif", Font.BOLD, 34));
        logo.setForeground(NAVY);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Selamat Datang di PlayZone!");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(NAVY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Silakan pilih akun untuk melanjutkan");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 17));
        subtitle.setForeground(ABU);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        JPanel cards = new JPanel(new GridLayout(1, 2, 25, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(600, 240));
        cards.setMaximumSize(new Dimension(600, 240));

        cards.add(buatCard("Admin", "admin", "🛡",
                new Color(205, 237, 255), new Color(43, 161, 245)));
        cards.add(buatCard("Pegawai", "karyawan", "👥",
                new Color(255, 239, 190), new Color(255, 183, 35)));

        content.add(logo);
        content.add(Box.createVerticalStrut(20));
        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(35));
        content.add(cards);

        halaman.add(content);
        return halaman;
    }

    private JPanel buatCard(String namaTampil, String role, String iconText,
            Color bg, Color buttonColor) {

        JPanel card = new JPanel();
        card.setBackground(bg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                BorderFactory.createEmptyBorder(25, 20, 20, 20)
        ));

        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 55));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel label = new JLabel(namaTampil);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(NAVY);
        label.setAlignmentX(CENTER_ALIGNMENT);

        JButton button = new JButton("Login");
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(230, 45));
        button.setPreferredSize(new Dimension(230, 45));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> bukaForm(role, namaTampil, iconText, buttonColor));

        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(label);
        card.add(Box.createVerticalStrut(20));
        card.add(button);

        return card;
    }

    // ==========================================
    // HALAMAN 2: FORM LOGIN (EMAIL & PASSWORD)
    // ==========================================
    private JPanel buatHalamanForm() {
        JPanel halaman = new JPanel(new GridBagLayout());
        halaman.setBackground(new Color(240, 248, 255));

        JPanel kartu = new JPanel();
        kartu.setBackground(Color.WHITE);
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                BorderFactory.createEmptyBorder(30, 60, 40, 60)
        ));
        kartu.setPreferredSize(new Dimension(580, 580));
        kartu.setMaximumSize(new Dimension(580, 580));

        JButton btnKembali = new JButton("← Kembali");
        btnKembali.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnKembali.setForeground(ABU);
        btnKembali.setContentAreaFilled(false);
        btnKembali.setBorderPainted(false);
        btnKembali.setFocusPainted(false);
        btnKembali.setMargin(new Insets(0, 0, 0, 0));
        btnKembali.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnKembali.addActionListener(e -> reset());

        lblIkonForm = new JLabel("🛡");
        lblIkonForm.setFont(new Font("SansSerif", Font.PLAIN, 56));
        lblIkonForm.setAlignmentX(CENTER_ALIGNMENT);

        lblJudulForm = new JLabel("Login Admin");
        lblJudulForm.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblJudulForm.setForeground(NAVY);
        lblJudulForm.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Masukkan email dan password Anda");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 15));
        sub.setForeground(ABU);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        txtEmail = new JTextField();
        txtEmail.putClientProperty("JTextField.placeholderText", "nama@playzone.com");
        stylingField(txtEmail);

        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password");
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        stylingField(txtPassword);

        lblError = new JLabel(" ");
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblError.setForeground(MERAH);

        btnMasuk = new JButton("Masuk");
        btnMasuk.setForeground(Color.WHITE);
        btnMasuk.setFont(new Font("SansSerif", Font.BOLD, 17));
        btnMasuk.setFocusPainted(false);
        btnMasuk.setBorderPainted(false);
        btnMasuk.setAlignmentX(CENTER_ALIGNMENT);
        btnMasuk.setPreferredSize(new Dimension(100, 52));
        btnMasuk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btnMasuk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMasuk.putClientProperty("FlatLaf.style", "arc: 14");
        btnMasuk.addActionListener(e -> prosesLogin());

        // Enter di kolom email pindah ke password, Enter di password = Masuk
        txtEmail.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> prosesLogin());

        // Semua komponen memakai perataan tengah; label kolom dibungkus baris
        // rata kiri supaya sejajar dengan tepi kolom input.
        kartu.add(barisKiri(btnKembali));
        kartu.add(Box.createVerticalStrut(10));
        kartu.add(lblIkonForm);
        kartu.add(Box.createVerticalStrut(12));
        kartu.add(lblJudulForm);
        kartu.add(Box.createVerticalStrut(8));
        kartu.add(sub);
        kartu.add(Box.createVerticalStrut(35));
        kartu.add(barisKiri(buatLabelField("Email")));
        kartu.add(Box.createVerticalStrut(8));
        kartu.add(txtEmail);
        kartu.add(Box.createVerticalStrut(22));
        kartu.add(barisKiri(buatLabelField("Password")));
        kartu.add(Box.createVerticalStrut(8));
        kartu.add(txtPassword);
        kartu.add(Box.createVerticalStrut(12));
        kartu.add(barisKiri(lblError));
        kartu.add(Box.createVerticalStrut(18));
        kartu.add(btnMasuk);
        kartu.add(Box.createVerticalGlue());

        halaman.add(kartu);
        return halaman;
    }

    // Membungkus komponen dalam satu baris selebar kartu, isi rata kiri
    private JPanel barisKiri(JComponent komponen) {
        JPanel baris = new JPanel(new BorderLayout());
        baris.setOpaque(false);
        baris.setAlignmentX(CENTER_ALIGNMENT);
        baris.add(komponen, BorderLayout.WEST);
        baris.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                baris.getPreferredSize().height));
        return baris;
    }

    private JLabel buatLabelField(String teks) {
        JLabel label = new JLabel(teks);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(NAVY);
        return label;
    }

    private void stylingField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setAlignmentX(CENTER_ALIGNMENT);
        field.setPreferredSize(new Dimension(100, 50));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.putClientProperty("FlatLaf.style", "arc: 14; margin: 4,12,4,12");
    }

    private void bukaForm(String role, String namaTampil, String ikon, Color warna) {
        roleDipilih = role;
        lblJudulForm.setText("Login " + namaTampil);
        lblIkonForm.setText(ikon);
        btnMasuk.setBackground(warna);
        bersihkanForm();
        cardLayout.show(this, HALAMAN_FORM);
        SwingUtilities.invokeLater(() -> txtEmail.requestFocusInWindow());
    }

    private void bersihkanForm() {
        txtEmail.setText("");
        txtPassword.setText("");
        lblError.setText(" ");
    }

    // ==========================================
    // VALIDASI LOGIN
    // ==========================================
    private void prosesLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            tampilkanError("Email dan password wajib diisi.");
            return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            tampilkanError("Format email tidak valid.");
            return;
        }

        boolean cocok;
        if (roleDipilih.equals("admin")) {
            cocok = email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD);
        } else {
            cocok = email.equalsIgnoreCase(PEGAWAI_EMAIL) && password.equals(PEGAWAI_PASSWORD);
        }

        if (!cocok) {
            tampilkanError("Email atau password salah.");
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
            return;
        }

        bersihkanForm();
        if (loginListener != null) {
            loginListener.accept(roleDipilih);
        }
    }

    private void tampilkanError(String pesan) {
        lblError.setText(pesan);
    }

    // Untuk menjalankan tampilan LoginFrame secara mandiri
    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("PlayZone");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            LoginFrame loginPanel = new LoginFrame();
            loginPanel.setLoginListener(role ->
                    JOptionPane.showMessageDialog(frame, "Login berhasil sebagai " + role));
            frame.setContentPane(loginPanel);
            frame.setVisible(true);
        });
    }
}
