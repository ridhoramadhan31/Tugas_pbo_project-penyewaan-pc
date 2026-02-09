/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;

import com.toedter.calendar.JDateChooser;
import crud.crud;
import model.PC;
import model.Sewa;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author User
 */
public class FrameApp extends javax.swing.JFrame {

    // --- Global Variables ---
    crud myCrud;
    DefaultTableModel modelPC;
    DefaultTableModel modelSewa;
    CardLayout cardLayout;
    
    // UI Colors (Cyberpunk Theme)
    Color darkBG = new Color(30, 30, 46);
    Color panelBG = new Color(40, 40, 60);
    Color neonBlue = new Color(0, 204, 255);
    Color textWhite = new Color(240, 240, 240);
    Color menuActive = new Color(255, 204, 0); // Kuning
    Color menuInactive = new Color(21, 22, 37); // Gelap Sidebar
    
    // Format Rupiah Indonesia
    NumberFormat kursIDR = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public FrameApp() {
        initComponents();
        // Set background frame utama agar seragam
        getContentPane().setBackground(darkBG);
        initCustomLogic();
        initIcons(); // Method untuk inisialisasi Emoji
    }

    // Method untuk Mengisi Teks Emoji/Ikon secara Manual
    // Method untuk Mengisi Teks Emoji/Ikon secara Manual
    private void initIcons() {
        // --- 1. SETUP TEXT EMOJI (Pakai Kode Unicode agar Aman) ---
        // \uD83D\uDDA5 = 🖥 (Komputer)
        // \uD83C\uDFAE = 🎮 (Game)
        // \uD83D\uDCCA = 📊 (Chart)
        // \uD83D\uDCBE = 💾 (Floppy)
        // \uD83D\uDDD1 = 🗑 (Sampah)
        // \uD83D\uDD04 = 🔄 (Refresh)
        // \uD83D\uDDA8 = 🖨 (Printer)
        // \uD83D\uDD0D = 🔍 (Kaca Pembesar)

        // Logo & Judul
        lblIcon.setText("\uD83D\uDDA5"); 
        lblTitle.setText("RENTAL PC");
        lblSubtitle.setText("Tempat Penyewaan PC & Game");

        // Menu Navigasi
        btnNavPC.setText("\uD83D\uDDA5 Data PC");
        btnNavSewa.setText("\uD83C\uDFAE Penyewaan");
        btnNavLaporan.setText("\uD83D\uDCCA Laporan");

        // Tombol CRUD PC
        if(txtIdPC.getText().isEmpty()) btnSimpanPC.setText("\uD83D\uDCBE SIMPAN");
        btnHapusPC.setText("\uD83D\uDDD1 HAPUS");
        btnResetPC.setText("\uD83D\uDD04 RESET");

        // Tombol CRUD Sewa
        if(txtPenyewa.getText().isEmpty()) btnSimpanSewa.setText("\uD83D\uDCBE SIMPAN");
        btnHapusSewa.setText("\uD83D\uDDD1 HAPUS");
        btnResetSewa.setText("\uD83D\uDD04 RESET");

        // Tombol Laporan
        btnCetakLaporanPC.setText("\uD83D\uDDA8 Cetak Laporan PC (PDF)");
        btnCetakLaporanSewa.setText("\uD83D\uDDA8 Cetak Laporan Sewa (PDF)");

        // --- 2. PERBAIKAN BORDER PENCARIAN (Agar Ikon Muncul) ---
        
        // Kita gunakan font "Dialog" atau "Segoe UI Symbol" yang support Unicode
        Font fontEmoji = new Font("Dialog", Font.PLAIN, 12);
        
        // Buat Border untuk Pencarian PC
        javax.swing.border.TitledBorder borderPC = javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(neonBlue), 
            "\uD83D\uDD0D Cari PC..." // Pakai Unicode \uD83D\uDD0D
        );
        borderPC.setTitleFont(fontEmoji); // Set Font
        borderPC.setTitleColor(neonBlue); // Set Warna
        txtCariPC.setBorder(borderPC);
        
        // Buat Border untuk Pencarian Sewa
        javax.swing.border.TitledBorder borderSewa = javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(neonBlue), 
            "\uD83D\uDD0D Cari Penyewa..." // Pakai Unicode \uD83D\uDD0D
        );
        borderSewa.setTitleFont(fontEmoji); // Set Font
        borderSewa.setTitleColor(neonBlue); // Set Warna
        txtCariSewa.setBorder(borderSewa);
    }

    private void initCustomLogic() {
        myCrud = new crud();
        
        // 1. Cek Koneksi
        if (myCrud.conn == null) {
            JOptionPane.showMessageDialog(this, "Koneksi Database Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // 2. Setup Layout & Tables
        cardLayout = (CardLayout) pnlContent.getLayout();
        setupTablePC();
        setupTableSewa();
        
        // 3. Terapkan Tema Gelap pada Tabel
        setupDarkTable(tblPC);
        setupDarkTable(tblSewa);
        
        // 4. Load Data
        refreshData();
        if(dtSewa.getDate() == null) dtSewa.setDate(new Date());
        
        // 5. Validasi Input
        setupInputValidation();
        
        // 6. Tampilan Awal
        cardLayout.show(pnlContent, "cardPC");
        updateMenuStyles(btnNavPC);
    }

    // Method untuk membuat tabel menjadi gelap dan keren
    private void setupDarkTable(JTable table) {
        // Warna Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(panelBG);
        header.setForeground(neonBlue);
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        header.setOpaque(false);

        // Warna Sel Tabel
        table.setBackground(new Color(50, 50, 70));
        table.setForeground(textWhite);
        table.setSelectionBackground(neonBlue);
        table.setSelectionForeground(darkBG);
        table.setGridColor(new Color(70, 70, 90));

        // Renderer untuk memastikan background sel gelap
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(new Color(50, 50, 70));
        renderer.setForeground(textWhite);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        table.setRowHeight(35);
    }

    // --- FITUR VALIDASI & FORMATTING ---
    private void setupInputValidation() {
        onlyNumber(txtDurasi); 
        setupCurrencyField(txtHarga); 
    }

    private void onlyNumber(JTextField txt) {
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
        });
    }

    private void setupCurrencyField(JTextField txt) {
        onlyNumber(txt);
        // Saat diklik: Hapus Rp dan koma
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String clean = txt.getText().replaceAll("[^0-9]", "");
                txt.setText(clean);
            }
            // Saat selesai: Format ke Rupiah
            @Override
            public void focusLost(FocusEvent e) {
                if (!txt.getText().isEmpty()) {
                    try {
                        double val = Double.parseDouble(txt.getText().replaceAll("[^0-9]", ""));
                        txt.setText(kursIDR.format(val));
                    } catch (Exception ex) { txt.setText(""); }
                }
            }
        });
    }

    private double parseRupiah(String text) {
        if (text == null || text.isEmpty()) return 0;
        try {
            String clean = text.replaceAll("[^0-9]", "");
            return Double.parseDouble(clean);
        } catch (Exception e) { return 0; }
    }

    // --- HELPER METHODS LAINNYA ---

    private void setupTablePC() {
        String[] header = {"ID", "Nama PC", "Spek", "Harga/Jam", "Status"};
        modelPC = new DefaultTableModel(header, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPC.setModel(modelPC);
    }

    private void setupTableSewa() {
        String[] header = {"ID Sewa", "PC", "Penyewa", "Tgl", "Durasi", "Total"};
        modelSewa = new DefaultTableModel(header, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblSewa.setModel(modelSewa);
    }

    private void refreshData() {
        myCrud.tampilPC(modelPC, "");
        myCrud.tampilSewa(modelSewa, "");
        myCrud.populateComboPC(cmbPC);
    }

    private void resetPC() {
        txtIdPC.setText("");
        txtNamaPC.setText("");
        txtSpek.setText("");
        txtHarga.setText("");
        cmbStatus.setSelectedIndex(0);
        
        btnSimpanPC.setText("💾 SIMPAN"); 
        
        tblPC.clearSelection();
        refreshData();
    }

    private void resetSewa() {
        cmbPC.setSelectedIndex(-1);
        if (cmbPC.getItemCount() > 0) cmbPC.setSelectedIndex(0);
        txtPenyewa.setText("");
        dtSewa.setDate(new Date());
        txtDurasi.setText("");
        
        btnSimpanSewa.setText("💾 SIMPAN");

        tblSewa.clearSelection();
        refreshData();
    }

    // Logika Warna Tombol Menu
    private void updateMenuStyles(javax.swing.JButton activeBtn) {
        javax.swing.JButton[] menus = {btnNavPC, btnNavSewa, btnNavLaporan};
        for (javax.swing.JButton btn : menus) {
            btn.setBackground(menuInactive);
            btn.setForeground(new Color(200, 200, 200)); // Abu-abu
        }
        activeBtn.setBackground(menuActive);
        activeBtn.setForeground(Color.WHITE);
    }

    private void exportPdf(DefaultTableModel model, String filename, String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(filename));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getParentFile(), file.getName() + ".pdf");
            }
            myCrud.exportLaporanPdf(model, file, title);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlSidebar = new javax.swing.JPanel();
        pnlLogo = new javax.swing.JPanel();
        lblIcon = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlMenu = new javax.swing.JPanel();
        btnNavPC = new javax.swing.JButton();
        btnNavSewa = new javax.swing.JButton();
        btnNavLaporan = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        cardPC = new javax.swing.JPanel();
        pnlFormPC = new javax.swing.JPanel();
        lblIdPC = new javax.swing.JLabel();
        txtIdPC = new javax.swing.JTextField();
        lblNamaPC = new javax.swing.JLabel();
        txtNamaPC = new javax.swing.JTextField();
        lblSpek = new javax.swing.JLabel();
        txtSpek = new javax.swing.JTextField();
        lblHarga = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        pnlBtnPC = new javax.swing.JPanel();
        btnSimpanPC = new javax.swing.JButton();
        btnHapusPC = new javax.swing.JButton();
        btnResetPC = new javax.swing.JButton();
        pnlTabelPC = new javax.swing.JPanel();
        txtCariPC = new javax.swing.JTextField();
        scrollPC = new javax.swing.JScrollPane();
        tblPC = new javax.swing.JTable();
        cardSewa = new javax.swing.JPanel();
        pnlFormSewa = new javax.swing.JPanel();
        lblPilihPC = new javax.swing.JLabel();
        cmbPC = new javax.swing.JComboBox<>();
        lblPenyewa = new javax.swing.JLabel();
        txtPenyewa = new javax.swing.JTextField();
        lblTglSewa = new javax.swing.JLabel();
        dtSewa = new com.toedter.calendar.JDateChooser();
        lblDurasi = new javax.swing.JLabel();
        txtDurasi = new javax.swing.JTextField();
        pnlBtnSewa = new javax.swing.JPanel();
        btnSimpanSewa = new javax.swing.JButton();
        btnHapusSewa = new javax.swing.JButton();
        btnResetSewa = new javax.swing.JButton();
        pnlTabelSewa = new javax.swing.JPanel();
        txtCariSewa = new javax.swing.JTextField();
        scrollSewa = new javax.swing.JScrollPane();
        tblSewa = new javax.swing.JTable();
        cardLaporan = new javax.swing.JPanel();
        btnCetakLaporanPC = new javax.swing.JButton();
        btnCetakLaporanSewa = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CYBERNET - Rental System");
        setBackground(new java.awt.Color(30, 30, 46));

        pnlSidebar.setBackground(new java.awt.Color(21, 22, 37));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(250, 700));
        pnlSidebar.setLayout(new java.awt.BorderLayout());

        pnlLogo.setBackground(new java.awt.Color(21, 22, 37));
        pnlLogo.setPreferredSize(new java.awt.Dimension(250, 140));
        pnlLogo.setLayout(new java.awt.GridBagLayout());

        lblIcon.setFont(new java.awt.Font("Segoe UI Emoji", 1, 40)); // NOI18N
        lblIcon.setForeground(new java.awt.Color(0, 204, 255));
        lblIcon.setText("PC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        pnlLogo.add(lblIcon, gridBagConstraints);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("RENTAL PC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlLogo.add(lblTitle, gridBagConstraints);

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblSubtitle.setText("Tempat Penyewaan PC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        pnlLogo.add(lblSubtitle, gridBagConstraints);

        pnlSidebar.add(pnlLogo, java.awt.BorderLayout.NORTH);

        pnlMenu.setBackground(new java.awt.Color(21, 22, 37));
        pnlMenu.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnlMenu.setLayout(new java.awt.GridLayout(5, 1, 0, 10));

        btnNavPC.setBackground(new java.awt.Color(255, 204, 0));
        btnNavPC.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        btnNavPC.setForeground(new java.awt.Color(255, 255, 255));
        btnNavPC.setText("Data PC");
        btnNavPC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNavPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavPCActionPerformed(evt);
            }
        });
        pnlMenu.add(btnNavPC);

        btnNavSewa.setBackground(new java.awt.Color(21, 22, 37));
        btnNavSewa.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        btnNavSewa.setText("Penyewaan");
        btnNavSewa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNavSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavSewaActionPerformed(evt);
            }
        });
        pnlMenu.add(btnNavSewa);

        btnNavLaporan.setBackground(new java.awt.Color(21, 22, 37));
        btnNavLaporan.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        btnNavLaporan.setText("Laporan");
        btnNavLaporan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNavLaporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavLaporanActionPerformed(evt);
            }
        });
        pnlMenu.add(btnNavLaporan);

        pnlSidebar.add(pnlMenu, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlSidebar, java.awt.BorderLayout.WEST);

        pnlContent.setBackground(new java.awt.Color(30, 30, 46));
        pnlContent.setLayout(new java.awt.CardLayout());

        cardPC.setBackground(new java.awt.Color(30, 30, 46));
        cardPC.setLayout(new java.awt.BorderLayout());

        pnlFormPC.setBackground(new java.awt.Color(40, 40, 60));
        pnlFormPC.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "INPUT DATA PC", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 255, 255))); // NOI18N
        pnlFormPC.setPreferredSize(new java.awt.Dimension(350, 430));
        pnlFormPC.setLayout(new java.awt.GridLayout(0, 1, 0, 8));

        lblIdPC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblIdPC.setForeground(new java.awt.Color(225, 225, 225));
        lblIdPC.setText("ID PC (Auto)");
        pnlFormPC.add(lblIdPC);

        txtIdPC.setEditable(false);
        txtIdPC.setBackground(new java.awt.Color(34, 34, 51));
        txtIdPC.setForeground(new java.awt.Color(153, 153, 153));
        txtIdPC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(102, 102, 102)));
        pnlFormPC.add(txtIdPC);

        lblNamaPC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNamaPC.setForeground(new java.awt.Color(225, 225, 225));
        lblNamaPC.setText("Nama PC");
        pnlFormPC.add(lblNamaPC);

        txtNamaPC.setBackground(new java.awt.Color(50, 50, 69));
        txtNamaPC.setForeground(new java.awt.Color(255, 255, 255));
        txtNamaPC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 204, 255)));
        txtNamaPC.setCaretColor(new java.awt.Color(0, 204, 255));
        pnlFormPC.add(txtNamaPC);

        lblSpek.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSpek.setForeground(new java.awt.Color(225, 225, 225));
        lblSpek.setText("Spesifikasi");
        pnlFormPC.add(lblSpek);

        txtSpek.setBackground(new java.awt.Color(50, 50, 69));
        txtSpek.setForeground(new java.awt.Color(255, 255, 255));
        txtSpek.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 204, 255)));
        txtSpek.setCaretColor(new java.awt.Color(0, 204, 255));
        pnlFormPC.add(txtSpek);

        lblHarga.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblHarga.setForeground(new java.awt.Color(225, 225, 225));
        lblHarga.setText("Harga Per Jam (Rp)");
        pnlFormPC.add(lblHarga);

        txtHarga.setBackground(new java.awt.Color(50, 50, 69));
        txtHarga.setForeground(new java.awt.Color(255, 255, 255));
        pnlFormPC.add(txtHarga);

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(225, 225, 225));
        lblStatus.setText("Status");
        pnlFormPC.add(lblStatus);

        cmbStatus.setBackground(new java.awt.Color(105, 80, 80));
        cmbStatus.setForeground(new java.awt.Color(255, 255, 255));
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tersedia", "Disewa" }));
        cmbStatus.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 204, 255)));
        pnlFormPC.add(cmbStatus);

        pnlBtnPC.setOpaque(false);
        pnlBtnPC.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        btnSimpanPC.setBackground(new java.awt.Color(255, 204, 0));
        btnSimpanPC.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        btnSimpanPC.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpanPC.setText("SIMPAN");
        btnSimpanPC.setBorderPainted(false);
        btnSimpanPC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSimpanPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanPCActionPerformed(evt);
            }
        });
        pnlBtnPC.add(btnSimpanPC);

        btnHapusPC.setBackground(new java.awt.Color(200, 50, 50));
        btnHapusPC.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        btnHapusPC.setForeground(new java.awt.Color(255, 255, 255));
        btnHapusPC.setText("HAPUS");
        btnHapusPC.setBorderPainted(false);
        btnHapusPC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapusPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusPCActionPerformed(evt);
            }
        });
        pnlBtnPC.add(btnHapusPC);

        btnResetPC.setBackground(new java.awt.Color(100, 100, 100));
        btnResetPC.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        btnResetPC.setForeground(new java.awt.Color(255, 255, 255));
        btnResetPC.setText("RESET");
        btnResetPC.setBorderPainted(false);
        btnResetPC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetPCActionPerformed(evt);
            }
        });
        pnlBtnPC.add(btnResetPC);

        pnlFormPC.add(pnlBtnPC);

        cardPC.add(pnlFormPC, java.awt.BorderLayout.WEST);

        pnlTabelPC.setBackground(new java.awt.Color(30, 30, 46));
        pnlTabelPC.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlTabelPC.setLayout(new java.awt.BorderLayout());

        txtCariPC.setBackground(new java.awt.Color(50, 50, 69));
        txtCariPC.setFont(new java.awt.Font("Segoe UI Emoji", 0, 12)); // NOI18N
        txtCariPC.setForeground(new java.awt.Color(255, 255, 255));
        txtCariPC.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cari PC...", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 204, 255))); // NOI18N
        txtCariPC.setCaretColor(new java.awt.Color(0, 204, 255));
        txtCariPC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariPCKeyReleased(evt);
            }
        });
        pnlTabelPC.add(txtCariPC, java.awt.BorderLayout.NORTH);

        scrollPC.setBackground(new java.awt.Color(30, 30, 46));
        scrollPC.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));

        tblPC.setBackground(new java.awt.Color(50, 50, 69));
        tblPC.setForeground(new java.awt.Color(255, 255, 255));
        tblPC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblPC.setGridColor(new java.awt.Color(68, 68, 102));
        tblPC.setSelectionBackground(new java.awt.Color(0, 204, 255));
        tblPC.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tblPC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPCMouseClicked(evt);
            }
        });
        scrollPC.setViewportView(tblPC);

        pnlTabelPC.add(scrollPC, java.awt.BorderLayout.CENTER);

        cardPC.add(pnlTabelPC, java.awt.BorderLayout.CENTER);

        pnlContent.add(cardPC, "cardPC");

        cardSewa.setBackground(new java.awt.Color(30, 30, 46));
        cardSewa.setLayout(new java.awt.BorderLayout());

        pnlFormSewa.setBackground(new java.awt.Color(40, 40, 60));
        pnlFormSewa.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TRANSAKSI SEWA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 204, 255))); // NOI18N
        pnlFormSewa.setPreferredSize(new java.awt.Dimension(350, 430));
        pnlFormSewa.setLayout(new java.awt.GridLayout(0, 1, 0, 8));

        lblPilihPC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPilihPC.setForeground(new java.awt.Color(225, 225, 225));
        lblPilihPC.setText("Pilih PC");
        pnlFormSewa.add(lblPilihPC);

        cmbPC.setBackground(new java.awt.Color(50, 50, 69));
        cmbPC.setForeground(new java.awt.Color(255, 255, 255));
        cmbPC.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 204, 255)));
        pnlFormSewa.add(cmbPC);

        lblPenyewa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPenyewa.setForeground(new java.awt.Color(225, 225, 225));
        lblPenyewa.setText("Nama Penyewa");
        pnlFormSewa.add(lblPenyewa);

        txtPenyewa.setBackground(new java.awt.Color(50, 50, 69));
        txtPenyewa.setForeground(new java.awt.Color(255, 255, 255));
        txtPenyewa.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 204, 255)));
        txtPenyewa.setCaretColor(new java.awt.Color(0, 204, 255));
        pnlFormSewa.add(txtPenyewa);

        lblTglSewa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTglSewa.setForeground(new java.awt.Color(225, 225, 225));
        lblTglSewa.setText("Tanggal Sewa");
        pnlFormSewa.add(lblTglSewa);

        dtSewa.setBackground(new java.awt.Color(50, 50, 69));
        dtSewa.setForeground(new java.awt.Color(255, 255, 255));
        dtSewa.setDateFormatString("yyyy-MM-dd");
        pnlFormSewa.add(dtSewa);

        lblDurasi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDurasi.setForeground(new java.awt.Color(225, 225, 225));
        lblDurasi.setText("Durasi (Jam)");
        pnlFormSewa.add(lblDurasi);

        txtDurasi.setBackground(new java.awt.Color(50, 50, 69));
        txtDurasi.setForeground(new java.awt.Color(255, 255, 255));
        txtDurasi.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(0, 204, 255)));
        txtDurasi.setCaretColor(new java.awt.Color(0, 204, 255));
        pnlFormSewa.add(txtDurasi);

        pnlBtnSewa.setOpaque(false);
        pnlBtnSewa.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        btnSimpanSewa.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        btnSimpanSewa.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpanSewa.setText("SIMPAN");
        btnSimpanSewa.setBorderPainted(false);
        btnSimpanSewa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSimpanSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanSewaActionPerformed(evt);
            }
        });
        pnlBtnSewa.add(btnSimpanSewa);

        btnHapusSewa.setBackground(new java.awt.Color(200, 50, 50));
        btnHapusSewa.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        btnHapusSewa.setForeground(new java.awt.Color(255, 255, 255));
        btnHapusSewa.setText("HAPUS");
        btnHapusSewa.setBorderPainted(false);
        btnHapusSewa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapusSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusSewaActionPerformed(evt);
            }
        });
        pnlBtnSewa.add(btnHapusSewa);

        btnResetSewa.setBackground(new java.awt.Color(100, 100, 100));
        btnResetSewa.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        btnResetSewa.setForeground(new java.awt.Color(255, 255, 255));
        btnResetSewa.setText("RESET");
        btnResetSewa.setBorderPainted(false);
        btnResetSewa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetSewaActionPerformed(evt);
            }
        });
        pnlBtnSewa.add(btnResetSewa);

        pnlFormSewa.add(pnlBtnSewa);

        cardSewa.add(pnlFormSewa, java.awt.BorderLayout.WEST);

        pnlTabelSewa.setBackground(new java.awt.Color(30, 30, 46));
        pnlTabelSewa.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlTabelSewa.setLayout(new java.awt.BorderLayout());

        txtCariSewa.setBackground(new java.awt.Color(50, 50, 69));
        txtCariSewa.setForeground(new java.awt.Color(255, 255, 255));
        txtCariSewa.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cari Penyewa...", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 204, 255))); // NOI18N
        txtCariSewa.setCaretColor(new java.awt.Color(0, 204, 255));
        txtCariSewa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariSewaKeyReleased(evt);
            }
        });
        pnlTabelSewa.add(txtCariSewa, java.awt.BorderLayout.NORTH);

        scrollSewa.setBackground(new java.awt.Color(30, 30, 46));
        scrollSewa.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));

        tblSewa.setBackground(new java.awt.Color(50, 50, 69));
        tblSewa.setForeground(new java.awt.Color(255, 255, 255));
        tblSewa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblSewa.setGridColor(new java.awt.Color(68, 68, 102));
        tblSewa.setSelectionBackground(new java.awt.Color(0, 204, 255));
        tblSewa.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tblSewa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSewaMouseClicked(evt);
            }
        });
        scrollSewa.setViewportView(tblSewa);

        pnlTabelSewa.add(scrollSewa, java.awt.BorderLayout.CENTER);

        cardSewa.add(pnlTabelSewa, java.awt.BorderLayout.CENTER);

        pnlContent.add(cardSewa, "cardSewa");

        cardLaporan.setBackground(new java.awt.Color(30, 30, 46));
        cardLaporan.setLayout(new java.awt.GridBagLayout());

        btnCetakLaporanPC.setBackground(new java.awt.Color(0, 25, 105));
        btnCetakLaporanPC.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        btnCetakLaporanPC.setForeground(new java.awt.Color(255, 255, 255));
        btnCetakLaporanPC.setText("Cetak Laporan PC (PDF)");
        btnCetakLaporanPC.setBorderPainted(false);
        btnCetakLaporanPC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCetakLaporanPC.setPreferredSize(new java.awt.Dimension(250, 50));
        btnCetakLaporanPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakLaporanPCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        cardLaporan.add(btnCetakLaporanPC, gridBagConstraints);

        btnCetakLaporanSewa.setBackground(new java.awt.Color(0, 51, 51));
        btnCetakLaporanSewa.setFont(new java.awt.Font("Segoe UI Emoji", 1, 14)); // NOI18N
        btnCetakLaporanSewa.setForeground(new java.awt.Color(255, 255, 255));
        btnCetakLaporanSewa.setText("Cetak Laporan Penyewaan (PDF)");
        btnCetakLaporanSewa.setBorderPainted(false);
        btnCetakLaporanSewa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCetakLaporanSewa.setPreferredSize(new java.awt.Dimension(250, 50));
        btnCetakLaporanSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakLaporanSewaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        cardLaporan.add(btnCetakLaporanSewa, gridBagConstraints);

        pnlContent.add(cardLaporan, "cardLaporan");

        getContentPane().add(pnlContent, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNavPCActionPerformed(java.awt.event.ActionEvent evt) {                                             
        cardLayout.show(pnlContent, "cardPC");
        updateMenuStyles(btnNavPC);
    }                                        

    private void btnNavSewaActionPerformed(java.awt.event.ActionEvent evt) {                                               
        cardLayout.show(pnlContent, "cardSewa");
        updateMenuStyles(btnNavSewa);
    }                                          

    private void btnNavLaporanActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        cardLayout.show(pnlContent, "cardLaporan");
        updateMenuStyles(btnNavLaporan);
    }                                             

    private void btnSimpanPCActionPerformed(java.awt.event.ActionEvent evt) {                                                
        String nama = txtNamaPC.getText();
        String spek = txtSpek.getText();
        double harga = parseRupiah(txtHarga.getText());
        String status = cmbStatus.getSelectedItem().toString();

        if (nama.isEmpty() || harga == 0) {
            JOptionPane.showMessageDialog(this, "Lengkapi Data PC!");
            return;
        }

        PC p = new PC(0, nama, spek, harga, status);

        if (btnSimpanPC.getText().contains("SIMPAN")) {
            if (myCrud.simpanPC(p)) {
                JOptionPane.showMessageDialog(this, "PC Disimpan");
                resetPC();
            }
        } else {
            p.setId(Integer.parseInt(txtIdPC.getText()));
            if (myCrud.ubahPC(p)) {
                JOptionPane.showMessageDialog(this, "PC Diubah");
                resetPC();
            }
        }
    }                                           

    private void tblPCMouseClicked(java.awt.event.MouseEvent evt) {                                       
        int row = tblPC.getSelectedRow();
        if (row != -1) {
            txtIdPC.setText(tblPC.getValueAt(row, 0).toString());
            txtNamaPC.setText(tblPC.getValueAt(row, 1).toString());
            txtSpek.setText(tblPC.getValueAt(row, 2).toString());
            txtHarga.setText(tblPC.getValueAt(row, 3).toString()); // Format rupiah, akan diparse saat simpan
            cmbStatus.setSelectedItem(tblPC.getValueAt(row, 4).toString());
            
            btnSimpanPC.setText("✏️ UBAH");
        }
    }                                  

    private void btnHapusPCActionPerformed(java.awt.event.ActionEvent evt) {                                               
        if (!txtIdPC.getText().isEmpty()) {
            if (JOptionPane.showConfirmDialog(this, "Hapus PC ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                myCrud.hapusPC(Integer.parseInt(txtIdPC.getText()));
                resetPC();
            }
        }
    }                                          

    private void btnResetPCActionPerformed(java.awt.event.ActionEvent evt) {                                               
        resetPC();
    }                                          

    private void btnSimpanSewaActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if (cmbPC.getSelectedItem() == null || txtPenyewa.getText().isEmpty() || txtDurasi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi Data Sewa!");
            return;
        }

        try {
            // Logic ambil ID PC dan Harga dari Combobox String
            String selectedPC = cmbPC.getSelectedItem().toString();
            int idPC = Integer.parseInt(selectedPC.split(" - ")[0]);
            
            // Ambil harga dari text combobox: "1 - PC Gaming (Rp 5.000/jam)"
            String hargaStr = selectedPC.substring(selectedPC.lastIndexOf("Rp") + 3, selectedPC.lastIndexOf("/jam"));
            double hargaPerJam = parseRupiah(hargaStr.trim());
            
            int durasi = Integer.parseInt(txtDurasi.getText());
            double total = hargaPerJam * durasi;
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tgl = sdf.format(dtSewa.getDate());

            Sewa s = new Sewa();
            s.setIdPC(idPC);
            s.setNamaPenyewa(txtPenyewa.getText());
            s.setTglSewa(tgl);
            s.setDurasi(durasi);
            s.setTotalBayar(total);

            if (btnSimpanSewa.getText().contains("SIMPAN")) {
                if (myCrud.simpanSewa(s)) {
                    JOptionPane.showMessageDialog(this, "Sewa Berhasil! Total: " + kursIDR.format(total));
                    resetSewa();
                }
            } else {
                int row = tblSewa.getSelectedRow();
                int idSewa = Integer.parseInt(tblSewa.getValueAt(row, 0).toString());
                s.setIdSewa(idSewa);
                if (myCrud.ubahSewa(s)) {
                    JOptionPane.showMessageDialog(this, "Data Sewa Diubah! Total Baru: " + kursIDR.format(total));
                    resetSewa();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error input: " + e.getMessage());
        }
    }                                             

    private void tblSewaMouseClicked(java.awt.event.MouseEvent evt) {                                         
        int row = tblSewa.getSelectedRow();
        if (row != -1) {
            // Set Combobox PC (Looping string matching)
            String pcName = tblSewa.getValueAt(row, 1).toString();
            for (int i = 0; i < cmbPC.getItemCount(); i++) {
                if (cmbPC.getItemAt(i).contains(pcName)) {
                    cmbPC.setSelectedIndex(i);
                    break;
                }
            }
            txtPenyewa.setText(tblSewa.getValueAt(row, 2).toString());
            try {
                Date tgl = new SimpleDateFormat("yyyy-MM-dd").parse(tblSewa.getValueAt(row, 3).toString());
                dtSewa.setDate(tgl);
            } catch (Exception e) {}
            // Ambil durasi (hapus kata " Jam")
            String durStr = tblSewa.getValueAt(row, 4).toString().replace(" Jam", "");
            txtDurasi.setText(durStr);
            
            btnSimpanSewa.setText("✏️ UBAH");
        }
    }                                    

    private void btnHapusSewaActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        int row = tblSewa.getSelectedRow();
        if (row != -1) {
            int id = Integer.parseInt(tblSewa.getValueAt(row, 0).toString());
            if (JOptionPane.showConfirmDialog(this, "Hapus Transaksi?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                myCrud.hapusSewa(id);
                resetSewa();
            }
        }
    }                                            

    private void btnResetSewaActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        resetSewa();
    }                                            

    private void btnCetakLaporanPCActionPerformed(java.awt.event.ActionEvent evt) {                                                      
        exportPdf(modelPC, "Laporan_PC.pdf", "Laporan Data PC CyberNet");
    }                                                 

    private void btnCetakLaporanSewaActionPerformed(java.awt.event.ActionEvent evt) {                                                        
        exportPdf(modelSewa, "Laporan_Sewa.pdf", "Laporan Transaksi Sewa CyberNet");
    }                                                   

    private void txtCariPCKeyReleased(java.awt.event.KeyEvent evt) {                                          
        myCrud.tampilPC(modelPC, txtCariPC.getText());
    }                                     

    private void txtCariSewaKeyReleased(java.awt.event.KeyEvent evt) {                                            
        myCrud.tampilSewa(modelSewa, txtCariSewa.getText());
    }                                       

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {}
        java.awt.EventQueue.invokeLater(() -> new FrameApp().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCetakLaporanPC;
    private javax.swing.JButton btnCetakLaporanSewa;
    private javax.swing.JButton btnHapusPC;
    private javax.swing.JButton btnHapusSewa;
    private javax.swing.JButton btnNavLaporan;
    private javax.swing.JButton btnNavPC;
    private javax.swing.JButton btnNavSewa;
    private javax.swing.JButton btnResetPC;
    private javax.swing.JButton btnResetSewa;
    private javax.swing.JButton btnSimpanPC;
    private javax.swing.JButton btnSimpanSewa;
    private javax.swing.JPanel cardLaporan;
    private javax.swing.JPanel cardPC;
    private javax.swing.JPanel cardSewa;
    private javax.swing.JComboBox<String> cmbPC;
    private javax.swing.JComboBox<String> cmbStatus;
    private com.toedter.calendar.JDateChooser dtSewa;
    private javax.swing.JLabel lblDurasi;
    private javax.swing.JLabel lblHarga;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblIdPC;
    private javax.swing.JLabel lblNamaPC;
    private javax.swing.JLabel lblPenyewa;
    private javax.swing.JLabel lblPilihPC;
    private javax.swing.JLabel lblSpek;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTglSewa;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlBtnPC;
    private javax.swing.JPanel pnlBtnSewa;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlFormPC;
    private javax.swing.JPanel pnlFormSewa;
    private javax.swing.JPanel pnlLogo;
    private javax.swing.JPanel pnlMenu;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JPanel pnlTabelPC;
    private javax.swing.JPanel pnlTabelSewa;
    private javax.swing.JScrollPane scrollPC;
    private javax.swing.JScrollPane scrollSewa;
    private javax.swing.JTable tblPC;
    private javax.swing.JTable tblSewa;
    private javax.swing.JTextField txtCariPC;
    private javax.swing.JTextField txtCariSewa;
    private javax.swing.JTextField txtDurasi;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIdPC;
    private javax.swing.JTextField txtNamaPC;
    private javax.swing.JTextField txtPenyewa;
    private javax.swing.JTextField txtSpek;
    // End of variables declaration//GEN-END:variables
}