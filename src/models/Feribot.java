package models;

import datastructures.MyStack;

public class Feribot {
   
    private char[] seferNo = new char[10];          
    private int feribotNo;                          
    private double feribotRihtimGirisSaati;         
    private double feribotRihtimKalkisSaati;        
    private boolean feribotKalkisaHazirMi;          
    
    private double gercekKalkisSaati; 
    private double gecikmeSuresi;

    private MyStack<Arac> altKat;  
    private MyStack<Arac> ustKat;  
    
    private static final int KAPASITE = 5; 

    public Feribot() {
        this.seferNo = "          ".toCharArray();
        this.feribotNo = 0;
        this.feribotRihtimGirisSaati = 0.0;
        this.feribotRihtimKalkisSaati = 0.0;
        this.feribotKalkisaHazirMi = false;
        this.gercekKalkisSaati = 0.0;
        this.gecikmeSuresi = 0.0;
        this.altKat = new MyStack<>(KAPASITE);
        this.ustKat = new MyStack<>(KAPASITE);
    }

    public Feribot(String seferNo, int feribotNo, double girisSaati, double kalkisSaati, boolean hazirMi) {
        setSeferNo(seferNo);
        this.feribotNo = feribotNo;
        this.feribotRihtimGirisSaati = girisSaati;
        this.feribotRihtimKalkisSaati = kalkisSaati;
        this.feribotKalkisaHazirMi = hazirMi;
        this.gercekKalkisSaati = kalkisSaati; 
        this.gecikmeSuresi = 0.0;
        this.altKat = new MyStack<>(KAPASITE);
        this.ustKat = new MyStack<>(KAPASITE);
    }
    
    public String getSeferNo() { 
        return new String(this.seferNo).trim(); 
    }
    
    public int getFeribotNo(){ return feribotNo; }
    public double getRihtimGirisSaati(){ return feribotRihtimGirisSaati; }
    public double getRihtimKalkisSaati(){ return feribotRihtimKalkisSaati; }
    public boolean isKalkisaHazirMi(){ return feribotKalkisaHazirMi; }
    public MyStack<Arac> getAltKat(){ return altKat; }
    public MyStack<Arac> getUstKat(){ return ustKat; }
    public boolean isFull(){ return altKat.isFull() && ustKat.isFull(); }
    public double getGercekKalkisSaati() { return gercekKalkisSaati; }
    public int getAltKatDoluluk() {  return altKat.size();  }
    public int getUstKatDoluluk() { return ustKat.size(); }
     
    public void setGercekKalkisSaati(double gercekKalkisSaati) { 
        this.gercekKalkisSaati = gercekKalkisSaati; 
        double fark = gercekKalkisSaati - feribotRihtimKalkisSaati;
        this.gecikmeSuresi = fark > 0.001 ? fark : 0.0;
    }
    
    public void setSeferNo(String seferNo) { 
        if (seferNo == null || seferNo.isEmpty() || seferNo.equals("*")) {
            this.seferNo = "          ".toCharArray();
        } else {
            this.seferNo = String.format("%-10s", seferNo).substring(0, 10).toCharArray();
        }
    }

    public boolean aracYukle(Arac arac) {
        if (arac.getAracTipi() == 1) { 
            if (altKat.size() < KAPASITE) {
                altKat.push(arac);
                arac.setSeferNo(this.getSeferNo()); 
                return true;
            }
        } else if (arac.getAracTipi() == 2) { 
            if (ustKat.size() < KAPASITE) {
                ustKat.push(arac);
                arac.setSeferNo(this.getSeferNo()); 
                return true;
            }
        }
        return false;
    }

    public boolean kalkisSartlariSaglandiMi(double simdikiZaman) {
        int altDoluluk = altKat.size();
        int ustDoluluk = ustKat.size();

        if (altDoluluk == KAPASITE && ustDoluluk == KAPASITE) {
            this.feribotKalkisaHazirMi = true; 
            return true;
        }

        if (simdikiZaman < feribotRihtimKalkisSaati) {
            return false;
        }

        boolean ikiKatYariDolu = altDoluluk >= 2 && ustDoluluk >= 2; 
        boolean tekKatTamDolu = altDoluluk == KAPASITE || ustDoluluk == KAPASITE;

        if (ikiKatYariDolu || tekKatTamDolu) {
            this.feribotKalkisaHazirMi = true; 
            return true;
        }

        return false;
    }

    // PDF Raporu Sayfa 5'teki çıktı formatına tam uyum sağlandı (Karakter Karakter Eşleşti)
    public void feribotBilgileriniYazdir() {
        System.out.println();
        System.out.printf("Sefer No=%s%n", getSeferNo()); 
        System.out.printf("Feribot No=%d%n", feribotNo); 
        System.out.printf("Kalkış Saati=%.2f%n", feribotRihtimKalkisSaati); 
        System.out.printf("Gerçekleşen Kalkış Saati=%.2f%n", gercekKalkisSaati); 
        
        if (gecikmeSuresi == 0.0) {
            System.out.println("Gecikme Süresi=0");
        } else {
            System.out.printf("Gecikme Süresi=%.2f%n", gecikmeSuresi);
        }
        
        System.out.println("Giriş Kat Araç Bilgileri"); 
        printKatAraclariSafPlaka(altKat);
        
        System.out.println("Üst Kat Araç Bilgileri"); 
        printKatAraclariSafPlaka(ustKat);
    }

    // Yığındaki (Stack) araçları, feribota ilk binen araç 1 Nolu Alan olacak şekilde kronolojik yazdırır
    private void printKatAraclariSafPlaka(MyStack<Arac> kat) {
        MyStack<Arac> tempStack = new MyStack<>(KAPASITE);
        Arac[] aracDizisi = new Arac[kat.size()];
        int index = 0;
        
        // Orijinal stack yapısını bozmamak için geçici stack'e aktarırken diziye kaydediyoruz
        while (!kat.isEmpty()) {
            Arac a = kat.pop();
            aracDizisi[index++] = a;
            tempStack.push(a);
        }
        
        // Verileri orijinal yığına geri yüklüyoruz
        while (!tempStack.isEmpty()) {
            kat.push(tempStack.pop());
        }
        
        // Rapordaki şablonda feribota ilk giren araç en üstte listeleniyor (1 Nolu Alan olarak)
        // Yığından en son çıkan eleman ilk giren olduğu için diziyi sondan başa doğru yazdırıyoruz
        int alanNo = 1;
        for (int j = aracDizisi.length - 1; j >= 0; j--) {
            System.out.printf("%d Nolu Alan = %s%n", alanNo++, aracDizisi[j].getAracPlaka());
        }
        
        if (aracDizisi.length == 0) {
            System.out.println("  (Boş)");
        }
    } 
}
