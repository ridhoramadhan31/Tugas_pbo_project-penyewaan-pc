/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crud;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class koneksi {
    String url = "jdbc:mysql://localhost:3306/db_rental_pc"; 
    String user = "root"; 
    String pass = "";     
    Connection Koneksidb;

    public koneksi() {
        try {
            Driver dbdriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(dbdriver);
            
            Koneksidb = DriverManager.getConnection(url, user, pass);
            System.out.println("Koneksi Berhasil");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Koneksi Gagal: " + e.toString());
        }
    }

    public Connection getKoneksi() { return Koneksidb; }
}