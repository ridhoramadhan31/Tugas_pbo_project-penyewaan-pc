/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
public class Sewa {
    private int idSewa;
    private int idPC;
    private String namaPC; // Helper untuk tabel
    private String namaPenyewa;
    private String tglSewa;
    private int durasi;
    private double totalBayar;

    public Sewa() {}

    // Getters & Setters Standard
    public int getIdSewa() { return idSewa; }
    public void setIdSewa(int idSewa) { this.idSewa = idSewa; }
    public int getIdPC() { return idPC; }
    public void setIdPC(int idPC) { this.idPC = idPC; }
    public String getNamaPC() { return namaPC; }
    public void setNamaPC(String namaPC) { this.namaPC = namaPC; }
    public String getNamaPenyewa() { return namaPenyewa; }
    public void setNamaPenyewa(String namaPenyewa) { this.namaPenyewa = namaPenyewa; }
    public String getTglSewa() { return tglSewa; }
    public void setTglSewa(String tglSewa) { this.tglSewa = tglSewa; }
    public int getDurasi() { return durasi; }
    public void setDurasi(int durasi) { this.durasi = durasi; }
    public double getTotalBayar() { return totalBayar; }
    public void setTotalBayar(double totalBayar) { this.totalBayar = totalBayar; }
}