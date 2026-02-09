/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
public class PC {
    private int id;
    private String namaPC;
    private String spek;
    private double hargaPerJam;
    private String status;

    public PC() {}

    public PC(int id, String namaPC, String spek, double hargaPerJam, String status) {
        this.id = id;
        this.namaPC = namaPC;
        this.spek = spek;
        this.hargaPerJam = hargaPerJam;
        this.status = status;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNamaPC() { return namaPC; }
    public void setNamaPC(String namaPC) { this.namaPC = namaPC; }
    public String getSpek() { return spek; }
    public void setSpek(String spek) { this.spek = spek; }
    public double getHargaPerJam() { return hargaPerJam; }
    public void setHargaPerJam(double hargaPerJam) { this.hargaPerJam = hargaPerJam; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}