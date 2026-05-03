
package models;

/**
 *
 * @author idalozyurt
 */


public class Arac {
    private String seferNo;
    private int aracNo;
    private String plaka;
    private double giseGirisSaati;
    private int aracTipi; 

    public Arac() {
        this.seferNo = "";
        this.aracNo = 0;
        this.plaka = "";
        this.giseGirisSaati = 0.0;
        this.aracTipi = 2;
    }

    public Arac(String seferNo, int aracNo, String plaka, double giseGirisSaati, int aracTipi) {
        this.seferNo = seferNo;
        this.aracNo = aracNo;
        this.plaka = plaka;
        this.giseGirisSaati = giseGirisSaati;
        this.aracTipi = aracTipi;
    }

    
    public String getSeferNo(){
        return seferNo; 
    }
    public int getAracNo(){
        return aracNo;
    }
    public String getPlaka(){
        return plaka;
    }
    public double getGiseGirisSaati(){
        return giseGirisSaati; 
    }
    public int getAracTipi(){
        return aracTipi; 
    }

 
    public void setSeferNo(String seferNo){
        this.seferNo = seferNo; 
    }
    public void setAracNo(int aracNo){
        this.aracNo = aracNo;
    }
    public void setPlaka(String plaka){
        this.plaka = plaka;
    }
    public void setGiseGirisSaati(double giseGirisSaati){
        this.giseGirisSaati = giseGirisSaati;
    }
    public void setAracTipi(int aracTipi){ 
        this.aracTipi = aracTipi; 
    }

    @Override
    public String toString() {
        String tip = (aracTipi == 1) ? "Ağır Vasıta" : "Otomobil";
        return String.format("Araç No: %d | Plaka: %s | Tip: %s | Giriş: %.2f | Sefer: %s",
                aracNo, plaka, tip, giseGirisSaati, seferNo.isEmpty() ? "Beklemede" : seferNo);
    }
}

