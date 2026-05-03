/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import datastructures.MyStack;

/**
 *
 * @author idalozyurt
 */
public class Feribot {
    private String seferNo;
    private int feribotNo;
    private double feribotRihtimGirisSaati;
    private double feribotRihtimKalkisSaati;
    private boolean feribotKalkisaHazirMi;
    
    private MyStack<Arac> altKat;  
    private MyStack<Arac> ustKat;  
    
    private static final int KAPASITE = 5;

    public Feribot() {
        this.seferNo = "";
        this.feribotNo = 0;
        this.feribotRihtimGirisSaati = 0.0;
        this.feribotRihtimKalkisSaati = 0.0;
        this.feribotKalkisaHazirMi = false;
        this.altKat = new MyStack<>(KAPASITE);
        this.ustKat = new MyStack<>(KAPASITE);
    }

    public Feribot(String seferNo, int feribotNo, double girisSaati, double kalkisSaati, boolean hazirMi) {
        this.seferNo = seferNo;
        this.feribotNo = feribotNo;
        this.feribotRihtimGirisSaati = girisSaati;
        this.feribotRihtimKalkisSaati = kalkisSaati;
        this.feribotKalkisaHazirMi = hazirMi;
        this.altKat = new MyStack<>(KAPASITE);
        this.ustKat = new MyStack<>(KAPASITE);
    }
    
    
    
    public String getSeferNo(){ 
        return seferNo; 
    }
    public int getFeribotNo(){
        return feribotNo;
    }
    public double getRihtimGirisSaati(){
        return feribotRihtimGirisSaati;
    }
    public double getRihtimKalkisSaati(){
        return feribotRihtimKalkisSaati;
    }
    public boolean isKalkisaHazirMi(){ 
        return feribotKalkisaHazirMi; 
    }
    public MyStack<Arac> getAltKat(){ 
        return altKat;
    }
    public MyStack<Arac> getUstKat(){
        return ustKat;
    }
    public int getAltKatDoluluk(){ 
        return altKat.size(); 
    }
    public int getUstKatDoluluk(){
        return ustKat.size();
    }
    public boolean isFull(){
          return altKat.isFull() && ustKat.isFull();
    }
    
   
    public void setSeferNo(String seferNo){
        this.seferNo = seferNo;
    }
    public void setFeribotNo(int feribotNo){
        this.feribotNo = feribotNo;
    }
    public void setKalkisaHazirMi(boolean hazir){
        this.feribotKalkisaHazirMi = hazir; 
    }

    public boolean aracYukle(Arac arac) {
        if (arac.getAracTipi() == 1) { 
            if (altKat.size() < KAPASITE) {
                altKat.push(arac);
                arac.setSeferNo(this.seferNo);
                return true;
            }
        } else if (arac.getAracTipi() == 2) { 
            if (ustKat.size() < KAPASITE) {
                ustKat.push(arac);
                arac.setSeferNo(this.seferNo);
                return true;
            }
        }
        return false;
    }

   
    public boolean kalkisSartlariSaglandiMi(double simdikiZaman) {
        int altDoluluk = (altKat.size() * 100) / KAPASITE;
        int ustDoluluk = (ustKat.size() * 100) / KAPASITE;
        
 
        if (altKat.size() == KAPASITE && ustKat.size() == KAPASITE) {
            return true;
        }
        if (altKat.size() == KAPASITE || ustKat.size() == KAPASITE) {
            return true;
        }
        if (simdikiZaman >= feribotRihtimKalkisSaati && altDoluluk >= 50 && ustDoluluk >= 50) {
            return true;
        }
        return false;
    }

    
    public void feribotBilgileriniYazdir() {
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("FERİBOT %d - Sefer: %s%n", feribotNo, seferNo);
        System.out.printf("Rıhtım Giriş: %.2f | Planlanan Kalkış: %.2f%n", 
                feribotRihtimGirisSaati, feribotRihtimKalkisSaati);
        
        System.out.println("-".repeat(60));
        System.out.println("Alt Kat (Ağır Vasıtalar) - " + altKat.size() + "/5 araç:");
        printKatAraclari(altKat);
        System.out.println("\nÜst KAt (Otomobiller) - " + ustKat.size() + "/5 araç:");
        printKatAraclari(ustKat);
        System.out.println("=".repeat(60));
    }
    
    private void printKatAraclari(MyStack<Arac> kat) {
        MyStack<Arac> temp = new MyStack<>(KAPASITE);
        Arac[] aracDizisi = new Arac[kat.size()];
        int i = 0;
        
        while (!kat.isEmpty()) {
            Arac a = kat.pop();
            aracDizisi[i++] = a;
            temp.push(a);
        }
        
        while (!temp.isEmpty()) kat.push(temp.pop());
        
        for (int j = aracDizisi.length - 1; j >= 0; j--) 
            System.out.println("  " + aracDizisi[j]);
        
        if (aracDizisi.length == 0) System.out.println("  (Boş)");
    }
    
}
