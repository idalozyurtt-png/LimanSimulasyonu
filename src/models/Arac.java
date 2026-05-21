package models;

/**
 * Araç sınıfı - PDF "Arac.h" tanımına tam uyumlu
 *
 * PDF alanları:
 *   seferNo      → Char[10]
 *   aracNo       → Integer
 *   aracPlaka    → Char[7]
 *   giseGirisSaati → Double  (Örneğin 13.30)
 *   aracTipi     → Integer  (1=Ağır Vasıta, 2=Otomobil)
 */
public class Arac {

    private char[] seferNo   = new char[10]; // PDF: seferNo (Char[10])
    private int    aracNo;                   // PDF: aracNo (Integer)
    private char[] aracPlaka = new char[7];  // PDF: aracPlaka (Char[7])
    private double giseGirisSaati;           // PDF: giseGirisSaati (Double)
    private int    aracTipi;                 // PDF: aracTipi (Integer)

    // =========================================================
    // VARSAYILAN CONSTRUCTOR
    // =========================================================

    public Arac() {
        this.seferNo        = "          ".toCharArray(); // 10 boşluk
        this.aracNo         = 0;
        this.aracPlaka      = "       ".toCharArray();    // 7 boşluk
        this.giseGirisSaati = 0.0;
        this.aracTipi       = 2; // Varsayılan: otomobil
    }

    // =========================================================
    // PARAMETRELI CONSTRUCTOR
    // =========================================================

    public Arac(String seferNo, int aracNo, String aracPlaka,
                double giseGirisSaati, int aracTipi) {

        setSeferNo(seferNo);
        this.aracNo = aracNo;
        setAracPlaka(aracPlaka);
        this.giseGirisSaati = giseGirisSaati;
        this.aracTipi = aracTipi;
    }

    // =========================================================
    // GETTER — seferNo
    // =========================================================

    /**
     * PDF: seferNo (Char[10])
     * Boş/atanmamış ise boş String döner.
     */
    public String getSeferNo() {
        String s = new String(this.seferNo).trim();
        return s.isEmpty() ? "" : s;
    }

    // =========================================================
    // GETTER — aracNo
    // =========================================================

    public int getAracNo() {
        return aracNo;
    }

    // =========================================================
    // GETTER — aracPlaka
    // =========================================================

    /**
     * PDF tam alan adı: aracPlaka (Char[7])
     * XmlManager ve diğer sınıflar bu metodu kullanmalı.
     */
    public String getAracPlaka() {
        return new String(this.aracPlaka).trim();
    }

    /**
     * Geriye dönük uyumluluk için köprü metod.
     * XmlManager'daki getPlaka() çağrılarını kırmamak için korundu.
     * Yeni kod getAracPlaka() kullanmalı.
     */
    public String getPlaka() {
        return getAracPlaka();
    }
   public char[] getSeferNoAsArray() {
    return this.seferNo;
    }

    public char[] getAracPlakaAsArray() {
    return this.aracPlaka;
    }

    // =========================================================
    // GETTER — giseGirisSaati
    // =========================================================

    public double getGiseGirisSaati() {
        return giseGirisSaati;
    }

    // =========================================================
    // GETTER — aracTipi
    // =========================================================

    public int getAracTipi() {
        return aracTipi;
    }

    // =========================================================
    // SETTER — seferNo
    // =========================================================

    /**
     * seferNo; araçlar feribota yüklendikten sonra atanır.
     * Txt'de "*" ile gösterilir — bu durumda boş bırakılır.
     */
    public void setSeferNo(String seferNo) {
        if (seferNo == null || seferNo.isEmpty() || seferNo.equals("*")) {
            this.seferNo = "          ".toCharArray(); // 10 boşluk
        } else {
            // 10 karaktere pad'le, fazlasını kes
            this.seferNo = String.format("%-10s", seferNo)
                                 .substring(0, 10)
                                 .toCharArray();
        }
    }

    // =========================================================
    // SETTER — aracNo
    // =========================================================

    /**
     * aracNo; gişe işlemleri sırasında atanır.
     * Txt'de "*" ile gösterilir.
     */
    public void setAracNo(int aracNo) {
        this.aracNo = aracNo;
    }

    // =========================================================
    // SETTER — aracPlaka
    // =========================================================

    /**
     * PDF: aracPlaka (Char[7]) — maksimum 7 karakter.
     * 7 karakterden kısa girişler sola hizalanıp boşlukla doldurulur.
     * 7 karakterden uzun girişler 7 karaktere kırpılır ve uyarı verilir.
     */
    public void setAracPlaka(String plaka) {
        if (plaka == null || plaka.isEmpty()) {
            this.aracPlaka = "       ".toCharArray(); // 7 boşluk
            return;
        }
        if (plaka.length() > 7) {
            System.out.println("UYARI: Plaka 7 karakterden uzun, kırpıldı: " + plaka);
            plaka = plaka.substring(0, 7);
        }
        // 7 karaktere pad'le
        this.aracPlaka = String.format("%-7s", plaka).toCharArray();
    }

    /**
     * Geriye dönük uyumluluk için köprü metod.
     * Yeni kod setAracPlaka() kullanmalı.
     */
    public void setPlaka(String plaka) {
        setAracPlaka(plaka);
    }

    // =========================================================
    // SETTER — giseGirisSaati
    // =========================================================

    public void setGiseGirisSaati(double giseGirisSaati) {
        this.giseGirisSaati = giseGirisSaati;
    }

    // =========================================================
    // SETTER — aracTipi
    // =========================================================

    public void setAracTipi(int aracTipi) {
        this.aracTipi = aracTipi;
    }

    // =========================================================
    // toString — PDF madde 8 tablo formatına uygun
    //   Sefer No | Araç No | Plaka | Araç Tipi
    // =========================================================

    @Override
   public String toString() {
    String sNo = getSeferNo();
    return String.format("%-11s %-10d %-10s %d",
            sNo.isEmpty() ? "Beklemede" : sNo,
            aracNo,
            getAracPlaka(),
            aracTipi);
    }
}
