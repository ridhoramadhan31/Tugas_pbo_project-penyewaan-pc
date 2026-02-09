/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crud;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.PC;
import model.Sewa;

/**
 *
 * @author User
 */
public class crud {
    
    public Connection conn;
    // Formatter Rupiah Indonesia
    NumberFormat kursIDR = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public crud() {
        koneksi myKoneksi = new koneksi();
        conn = myKoneksi.getKoneksi();
    }

    // Helper: Ubah double ke String format Rupiah (Rp10.000,00)
    public String formatRupiah(double val) {
        return kursIDR.format(val);
    }

    // ==========================================
    // BAGIAN MANAJEMEN DATA PC
    // ==========================================

    public void tampilPC(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        // Query dengan pencarian nama atau spek
        String sql = "SELECT * FROM pc WHERE nama_pc LIKE ? OR spek LIKE ? ORDER BY id ASC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama_pc"),
                    rs.getString("spek"),
                    formatRupiah(rs.getDouble("harga_per_jam")), // Tampil sbg Rupiah
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Tampil PC: " + e.getMessage());
        }
    }

    public boolean simpanPC(PC p) {
        String sql = "INSERT INTO pc (nama_pc, spek, harga_per_jam, status) VALUES (?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getNamaPC());
            ps.setString(2, p.getSpek());
            ps.setDouble(3, p.getHargaPerJam());
            ps.setString(4, p.getStatus());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Simpan PC: " + e.getMessage());
            return false;
        }
    }

    public boolean ubahPC(PC p) {
        String sql = "UPDATE pc SET nama_pc=?, spek=?, harga_per_jam=?, status=? WHERE id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getNamaPC());
            ps.setString(2, p.getSpek());
            ps.setDouble(3, p.getHargaPerJam());
            ps.setString(4, p.getStatus());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hapusPC(int id) {
        try {
            String sql = "DELETE FROM pc WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            
            // Opsional: Reset Auto Increment ID PC
            conn.createStatement().execute("SET @count = 0");
            conn.createStatement().execute("UPDATE pc SET id = @count:= @count + 1");
            conn.createStatement().execute("ALTER TABLE pc AUTO_INCREMENT = 1");
            
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Hapus (PC sedang disewa?)");
            return false;
        }
    }

    // ==========================================
    // BAGIAN MANAJEMEN TRANSAKSI SEWA
    // ==========================================

    public void tampilSewa(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        // JOIN Table Sewa & PC untuk mengambil Nama PC
        String sql = "SELECT s.id_sewa, p.nama_pc, s.nama_penyewa, s.tgl_sewa, s.durasi, s.total_bayar " +
                     "FROM sewa s JOIN pc p ON s.id_pc = p.id " +
                     "WHERE s.nama_penyewa LIKE ? ORDER BY s.tgl_sewa DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_sewa"),
                    rs.getString("nama_pc"),
                    rs.getString("nama_penyewa"),
                    rs.getString("tgl_sewa"),
                    rs.getInt("durasi") + " Jam",
                    formatRupiah(rs.getDouble("total_bayar")) // Tampil sbg Rupiah
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean simpanSewa(Sewa s) {
        String sql = "INSERT INTO sewa (id_pc, nama_penyewa, tgl_sewa, durasi, total_bayar) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, s.getIdPC());
            ps.setString(2, s.getNamaPenyewa());
            ps.setString(3, s.getTglSewa());
            ps.setInt(4, s.getDurasi());
            ps.setDouble(5, s.getTotalBayar());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean ubahSewa(Sewa s) {
        String sql = "UPDATE sewa SET id_pc=?, nama_penyewa=?, tgl_sewa=?, durasi=?, total_bayar=? WHERE id_sewa=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, s.getIdPC());
            ps.setString(2, s.getNamaPenyewa());
            ps.setString(3, s.getTglSewa());
            ps.setInt(4, s.getDurasi());
            ps.setDouble(5, s.getTotalBayar());
            ps.setInt(6, s.getIdSewa());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hapusSewa(int id) {
        try {
            String sql = "DELETE FROM sewa WHERE id_sewa=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            
            // Opsional: Reset Auto Increment ID Sewa
            conn.createStatement().execute("SET @count = 0");
            conn.createStatement().execute("UPDATE sewa SET id_sewa = @count:= @count + 1");
            conn.createStatement().execute("ALTER TABLE sewa AUTO_INCREMENT = 1");
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    // Mengisi ComboBox PC dengan format "ID - Nama (Harga)"
    public void populateComboPC(javax.swing.JComboBox cmb) {
        cmb.removeAllItems();
        try {
            String sql = "SELECT id, nama_pc, harga_per_jam FROM pc ORDER BY nama_pc ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String item = rs.getInt("id") + " - " + rs.getString("nama_pc") + 
                              " (" + formatRupiah(rs.getDouble("harga_per_jam")) + "/jam)";
                cmb.addItem(item);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Export PDF Generic (Bisa untuk Tabel PC maupun Sewa)
    public void exportLaporanPdf(DefaultTableModel model, File file, String titleStr) {
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            // Judul Laporan
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph(titleStr, fontTitle);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph(" ")); // Spasi

            // Buat Tabel PDF sesuai jumlah kolom JTable
            PdfPTable table = new PdfPTable(model.getColumnCount());
            table.setWidthPercentage(100);

            // Header Tabel
            for(int i=0; i<model.getColumnCount(); i++) {
                table.addCell(model.getColumnName(i));
            }

            // Isi Data
            for (int i = 0; i < model.getRowCount(); i++) {
                for(int j=0; j<model.getColumnCount(); j++) {
                    table.addCell(model.getValueAt(i, j).toString());
                }
            }

            doc.add(table);
            doc.close();
            JOptionPane.showMessageDialog(null, "Export PDF Sukses!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Export: " + e.getMessage());
        }
    }
}