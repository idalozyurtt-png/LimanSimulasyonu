/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;
import datastructures.*;
import models.*;
import java.util.*;

/**
 *
 * @author idalozyurt
 */

public class LimanYonetici {
    private MyQueue<Arac> yuklemeYolu1;
    private MyQueue<Arac> yuklemeYolu2;
    private List<Feribot> feribotlar;
    private List<Arac> tumAraclar;
    private MyHashTable<String, Arac> hashTable;
    private int yonlendirmeSayaci;
    private int sonAracNo;

    public LimanYonetici() {
        this.yuklemeYolu1 = new MyQueue<>();
        this.yuklemeYolu2 = new MyQueue<>();
        this.feribotlar = new ArrayList<>();
        this.tumAraclar = new ArrayList<>();
        this.hashTable = new MyHashTable<>();
        this.yonlendirmeSayaci = 0;
        this.sonAracNo = 0;
    }
    
    public void yukle() {
        feribotlar = XmlManager.feribotlariOku();
        tumAraclar = XmlManager.araclariOku();
        
        
        for (Arac arac : tumAraclar) {
            hashTable.put(arac.getPlaka(), arac);
            if (arac.getAracNo() > sonAracNo) {
                sonAracNo = arac.getAracNo();
            }
        }
        
        for (Arac arac : tumAraclar) {
            if (arac.getAracNo() == 0) {
                arac.setAracNo(++sonAracNo);
            }
        }
        
        
        for (Arac arac : tumAraclar) {
            yonlendirmeSayaci++;
            if (yonlendirmeSayaci % 2 == 1) {
                yuklemeYolu1.enqueue(arac);
            } else {
                yuklemeYolu2.enqueue(arac);
            }
        }
    }
    
    public void simulasyonuBaslat() {
        System.out.println("\n========== LİMAN SİMÜLASYONU BAŞLIYOR ==========\n");
        
        for (Feribot feribot : feribotlar) {
            System.out.printf("\n>>> Feribot %d (Sefer: %s) rıhtıma yanaşıyor... (Giriş: %.2f)%n",
                    feribot.getFeribotNo(), feribot.getSeferNo(), feribot.getRihtimGirisSaati());
            
            double simdikiZaman = feribot.getRihtimGirisSaati();
            double sonYuklemeZamani = simdikiZaman;
            boolean yuklemeDevam = true;
            
            while (yuklemeDevam && !feribot.isFull()) {
                boolean yuklendi = false;
                
                
                if (!yuklemeYolu1.isEmpty()) {
                    Arac arac = yuklemeYolu1.dequeue();
                    if (feribot.aracYukle(arac)) {
                        System.out.printf("  ✓ 1. Yol'dan %s yüklendi (%.2f)%n", arac.getPlaka(), simdikiZaman);
                        yuklendi = true;
                        sonYuklemeZamani = arac.getGiseGirisSaati();
                    } else {
                       
                        yuklemeYolu1.enqueue(arac);
                    }
                }
                
                if (!yuklemeYolu2.isEmpty()) {
                    Arac arac = yuklemeYolu2.dequeue();
                    if (feribot.aracYukle(arac)) {
                        System.out.printf("  ✓ 2. Yol'dan %s yüklendi (%.2f)%n", arac.getPlaka(), simdikiZaman);
                        yuklendi = true;
                        sonYuklemeZamani = arac.getGiseGirisSaati();
                    } else {
                        yuklemeYolu2.enqueue(arac);
                    }
                }
                
                if (!yuklendi) {
                    yuklemeDevam = false;
                }
                
                simdikiZaman += 0.05;
                if (feribot.kalkisSartlariSaglandiMi(simdikiZaman)) {
                    break;
                }
            }
            
           
            double kalkisSaati = feribot.getRihtimKalkisSaati();
            if (sonYuklemeZamani > feribot.getRihtimGirisSaati() && feribot.kalkisSartlariSaglandiMi(sonYuklemeZamani)) {
                kalkisSaati = sonYuklemeZamani;
            }
            
            feribot.feribotBilgileriniYazdir();
            System.out.printf(">>> Feribot %d KALKIŞ YAPIYOR! (Gerçek Kalkış: %.2f)%n%n",
                    feribot.getFeribotNo(), kalkisSaati);
        }
        
        
        System.out.println("\n========== YÜKLEME YOLLARINDA KALAN ARAÇLAR ==========");
        System.out.print("1. Yol: ");
        printKuyruk(yuklemeYolu1);
        System.out.print("2. Yol: ");
        printKuyruk(yuklemeYolu2);
    }
    
    private void printKuyruk(MyQueue<Arac> kuyruk) {
        MyQueue<Arac> temp = new MyQueue<>();
        int count = 0;
        while (!kuyruk.isEmpty()) {
            Arac a = kuyruk.dequeue();
            System.out.print(a.getPlaka() + " ");
            temp.enqueue(a);
            count++;
        }
        while (!temp.isEmpty()) {
            kuyruk.enqueue(temp.dequeue());
        }
        System.out.println("(" + count + " araç)");
    }
    
    public boolean aracEkle(String plaka, double girisSaati, int aracTipi) {
     
        if (hashTable.containsKey(plaka)) {
            System.out.println("HATA: " + plaka + " plakalı araç ZATEN KAYITLI!");
            return false;
        }
        
       
        int yeniAracNo = ++sonAracNo;
        Arac yeniArac = new Arac("", yeniAracNo, plaka, girisSaati, aracTipi);
        
       
        hashTable.put(plaka, yeniArac);
        
       
        tumAraclar.add(yeniArac);
        
       
        yonlendirmeSayaci++;
        if (yonlendirmeSayaci % 2 == 1){
            yuklemeYolu1.enqueue(yeniArac);
        } else {
            yuklemeYolu2.enqueue(yeniArac);
        }
        
        XmlManager.aracEkle(yeniArac, tumAraclar);
        
        System.out.println("✓ Yeni araç eklendi: " + yeniArac);
        return true;
    }
    
    public void tumAraclariListele() {
        System.out.println("\n========== TÜM ARAÇLAR (Araç No'ya Göre Sıralı) ==========");
        List<Arac> sirali = new ArrayList<>(tumAraclar);
        sirali.sort(Comparator.comparingInt(Arac::getAracNo));
        for (Arac arac : sirali) {
            System.out.println(arac);
        }
    }
    
   
    public MyQueue<Arac> getYuklemeYolu1(){
        return yuklemeYolu1;
    }
    public MyQueue<Arac> getYuklemeYolu2(){ 
        return yuklemeYolu2;
    }
    public List<Feribot> getFeribotlar(){
        return feribotlar;
    }
    public List<Arac> getTumAraclar(){ 
        return tumAraclar;
    }
    public MyHashTable<String, Arac> getHashTable() {
        return hashTable; 
    }
    public int getSonAracNo(){
        return sonAracNo; 
    }
}
