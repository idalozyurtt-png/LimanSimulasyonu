package models;

import datastructures.MyStack;

public class Feribot {
    private String seferNo; // [cite: 49]
    private int feribotNo; // [cite: 51]
    private double feribotRihtimGirisSaati; // [cite: 52]
    private double feribotRihtimKalkisSaati; // [cite: 53]
    private boolean feribotKalkisaHazirMi; // [cite: 54]
    
    // PDF isterlerine göre eklenen gerçek zaman takip değişkenleri [cite: 25, 99]
    private double gercekKalkisSaati; 
    private double gecikmeSuresi;

    private MyStack<Arac> altKat;  
    private MyStack<Arac> ustKat;  
    
    private static final int KAPASITE = 5; // [cite: 11]

    public Feribot() {
        this.seferNo = " ";
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
        this.seferNo = seferNo;
        this.feribotNo = feribotNo;
        this.feribotRihtimGirisSaati = girisSaati;
        this.feribotRihtimKalkisSaati = kalkisSaati;
        this.feribotKalkisaHazirMi = hazirMi;
        this.gercekKalkisSaati = kalkisSaati; // Varsayılan olarak planlanan saat [cite: 53]
        this.gecikmeSuresi = 0.0;
        this.altKat = new MyStack<>(KAPASITE);
        this.ustKat = new MyStack<>(KAPASITE);
    }
    
    public String getSeferNo(){ return seferNo; }
    public int getFeribotNo(){ return feribotNo; }
    public double getRihtimGirisSaati(){ return feribotRihtimGirisSaati; }
    public double getRihtimKalkisSaati(){ return feribotRihtimKalkisSaati; }
    public boolean isKalkisaHazirMi(){ return feribotKalkisaHazirMi; }
    public MyStack<Arac> getAltKat(){ return altKat; }
    public MyStack<Arac> getUstKat(){ return ustKat; }
    public int getAltKatDoluluk(){ return altKat.size(); }
    public int getUstKatDoluluk(){ return ustKat.size(); }
    public boolean isFull(){ return altKat.isFull() && ustKat.isFull(); }
    
    public double getGercekKalkisSaati() { return gercekKalkisSaati; }
    public void setGercekKalkisSaati(double gercekKalkisSaati) { 
        this.gercekKalkisSaati = gercekKalkisSaati; 
        // Gecikme süresini hesapla (Negatif çıkarsa erken kalkmıştır, 0 kabul edilir veya doğrudan basılır) [cite: 25, 99]
        this.gecikmeSuresi = Math.max(0.0, gercekKalkisSaati - feribotRihtimKalkisSaati);
    }
    
    public void setSeferNo(String seferNo){ this.seferNo = seferNo; }
    public void setFeribotNo(int feribotNo){ this.feribotNo = feribotNo; }
    public void setKalkisaHazirMi(boolean hazir){ this.feribotKalkisaHazirMi = hazir; }

    public boolean aracYukle(Arac arac) {
        if (arac.getAracTipi() == 1) { // Ağır vasıta giriş katına [cite: 36, 67, 77]
            if (altKat.size() < KAPASITE) {
                altKat.push(arac);
                arac.setSeferNo(this.seferNo); // [cite: 65]
                return true;
            }
        } else if (arac.getAracTipi() == 2) { // Otomobil üst kata [cite: 36, 67, 77]
            if (ustKat.size() < KAPASITE) {
                ustKat.push(arac);
                arac.setSeferNo(this.seferNo); // [cite: 65]
                return true;
            }
        }
        return false;
    }
  // PDF Kurallarını ve senin mantığını dört dörtlük sağlayan kesin kural motoru
    public boolean kalkisSartlariSaglandiMi(double simdikiZaman) {
        int altDoluluk = altKat.size();
        int ustDoluluk = ustKat.size();

        // ════════════════════════════════════════════════════════════════════
        // 3. ŞART (MUTLAK ERKEN KALKIŞ): Her iki katın da %100 dolu olması durumu
        // Sadece bu durumda KALKIŞ SAATİNİ BEKLEMEDEN direkt kalkıp gider!
        // ════════════════════════════════════════════════════════════════════
        if (altDoluluk == KAPASITE && ustDoluluk == KAPASITE) {
            return true;
        }

        // ════════════════════════════════════════════════════════════════════
        // DİĞER DURUMLARDA KALKIŞ SAATİNİ BEKLESİN!
        // Eğer simülasyon saati henüz planlanan kalkış saatine gelmediyse, 
        // aşağıdaki 1. ve 2. şartlar asla tetiklenmez, feribot limandan ayrılamaz!
        // ════════════════════════════════════════════════════════════════════
        if (simdikiZaman < feribotRihtimKalkisSaati) {
            return false;
        }

        // --- BURADAN İTİBAREN PLANLANAN KALKIŞ SAATİ GELMİŞ VEYA GEÇMİŞTİR ---

        // 1. ŞART: Kalkış saatinin gelmesi VE her iki katın da en az %50 (>=3) dolu olması durumu
        boolean ikiKatYariDolu = altDoluluk >= 3 && ustDoluluk >= 3;

        // 2. ŞART: Kalkış saatinin gelmesi VE yalnızca bir katının %100 (=5) dolu olması durumu
        boolean tekKatTamDolu = altDoluluk == KAPASITE || ustDoluluk == KAPASITE;

        return ikiKatYariDolu || tekKatTamDolu;
    }
    // PDF Sayfa 4/5'teki İstenen Tam Rapor Formatı [cite: 96, 98, 99, 100, 104]
    public void feribotBilgileriniYazdir() {
        System.out.println();
        System.out.printf("Sefer No = %s%n", seferNo); // [cite: 98]
        System.out.printf("Feribot No = %d%n", feribotNo); // [cite: 98]
        System.out.printf("Kalkış Saati = %.2f%n", feribotRihtimKalkisSaati); // 
        System.out.printf("Gerçekleşen Kalkış Saati = %.2f%n", gercekKalkisSaati); // 
        System.out.printf("Gecikme Süresi = %.2f%n", gecikmeSuresi); // 
        
        System.out.println("Giriş Kat Araç Bilgileri"); // [cite: 100]
        printKatAraclariSafPlaka(altKat);
        
        System.out.println("Üst Kat Araç Bilgileri"); // [cite: 104]
        printKatAraclariSafPlaka(ustKat);
    }
    private void printKatAraclariSafPlaka(MyStack<Arac> kat) {
    MyStack<Arac> temp = new MyStack<>(KAPASITE);
    Arac[] aracDizisi = new Arac[kat.size()];
    int i = 0;
    
    // Yığından elemanları boşaltıp diziye aktarıyoruz
    // Bu işlem bittiğinde:
    // aracDizisi[0] -> En son binen araç (Yığının en üstü)
    // aracDizisi[length-1] -> İlk binen araç (Yığının en altı)
    while (!kat.isEmpty()) {
        Arac a = kat.pop();
        aracDizisi[i++] = a;
        temp.push(a);
    }
    
    // Yığını eski haline geri getiriyoruz
    while (!temp.isEmpty()) {
        kat.push(temp.pop());
    }
    
    // HOCANIN FORMATI İÇİN DÜZELTME:
    // 1 Nolu Alan feribotun en içi (ilk binen araç) olmalıdır.
    // Bu yüzden diziyi sondan başa doğru (ilk binenden son binene) yazdırıyoruz.
    int alanNo = 1;
    for (int j = aracDizisi.length - 1; j >= 0; j--) {
        System.out.printf("%d Nolu Alan = %s%n", alanNo++, aracDizisi[j].getPlaka());
    }
    
    if (aracDizisi.length == 0) {
        System.out.println("  (Boş)");
    }
}
}
