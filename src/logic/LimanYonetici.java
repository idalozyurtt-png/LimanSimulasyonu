package logic;

import datastructures.*;
import models.*;
import java.util.*;

public class LimanYonetici {

    private MyQueue<Arac> yuklemeYolu1;
    private MyQueue<Arac> yuklemeYolu2;
    private List<Feribot> feribotlar;
    private List<Arac> tumAraclar;
    private MyHashTable<String, Arac> hashTable;
    private int yonlendirmeSayaci;

    public LimanYonetici() {
        this.yuklemeYolu1 = new MyQueue<>();
        this.yuklemeYolu2 = new MyQueue<>();
        this.feribotlar = new ArrayList<>();
        this.tumAraclar = new ArrayList<>();
        this.hashTable = new MyHashTable<>();
        this.yonlendirmeSayaci = 0;
    }

    public void yukle() {
        feribotlar = XmlManager.feribotlariOku();
        tumAraclar = XmlManager.araclariOku();
        sistemiYenidenDuzenle();
    }

    private void sistemiYenidenDuzenle() {
        yuklemeYolu1.clear();
        yuklemeYolu2.clear();
        hashTable = new MyHashTable<>();

        // Araçları gişe giriş saatine göre sırala (Sayfa 2, Madde 35)
        tumAraclar.sort(Comparator.comparingDouble(Arac::getGiseGirisSaati));

        int aracNo = 1;
        for (Arac arac : tumAraclar) {
            arac.setAracNo(aracNo++);
            hashTable.put(arac.getAracPlaka(), arac);
        }

        // PDF Sayfa 4 Madde 4: Sırasıyla yollara dağıt (1. araç -> Yol 1, 2. araç -> Yol 2...)
        yonlendirmeSayaci = 0;
        for (Arac arac : tumAraclar) {
            yonlendirmeSayaci++;
            if (yonlendirmeSayaci % 2 == 1) {
                yuklemeYolu1.enqueue(arac);
            } else {
                yuklemeYolu2.enqueue(arac);
            }
        }
    }

    public boolean aracEkle(String plaka, double girisSaati, int aracTipi) {
        if (hashTable.containsKey(plaka)) {
            System.out.println("HATA: " + plaka + " plakalı araç sistemde zaten kayıtlı!");
            return false;
        }

        Arac yeniArac = new Arac("", 0, plaka, girisSaati, aracTipi);
        tumAraclar.add(yeniArac);
        sistemiYenidenDuzenle();
        XmlManager.tumAraclariKaydet(tumAraclar);

        System.out.println("✓ Yeni araç eklendi: " + yeniArac);
        return true;
    }

    public void simulasyonuBaslat() {
        System.out.println("\n========== LİMAN SİMÜLASYONU BAŞLIYOR ==========\n");

        for (Feribot feribot : feribotlar) {
            System.out.printf("\n>>> Feribot %d (Sefer: %s) rıhtıma yanaşıyor... (Planlanan Kalkış: %.2f)%n",
                    feribot.getFeribotNo(), feribot.getSeferNo(), feribot.getRihtimKalkisSaati());

            // Feribot rıhtıma yanaştığı an simülasyon zamanı başlar
            double feribotZamani = feribot.getRihtimGirisSaati();
            double sonBinenAracSaati = feribotZamani;
            boolean feribotKalkti = false;

            while (!feribotKalkti) {
                // MUTLAK KONTROL: Eğer feribot her iki kattan da %100 dolduysa saat beklenmeksizin anında kalkar! (Sayfa 2, Madde 26)
                if (feribot.getAltKatDoluluk() == 5 && feribot.getUstKatDoluluk() == 5) {
                    feribot.setGercekKalkisSaati(sonBinenAracSaati);
                    feribotKalkti = true;
                    break;
                }

                boolean eylemGerceklestiMi = false;

                // 1. YOL KONTROLÜ (Sırasıyla birer birer çekim mantığı - Sayfa 4, Madde 78)
                if (!yuklemeYolu1.isEmpty()) {
                    Arac arac = yuklemeYolu1.peek();
                    // Araç gişeden geçmiş mi veya feribot saati gelmiş mi kontrolü
                    if (arac.getGiseGirisSaati() <= feribotZamani || feribotZamani >= feribot.getRihtimKalkisSaati()) {
                        if (feribot.aracYukle(arac)) {
                            yuklemeYolu1.dequeue(); // Yükleme başarılı ise kuyruktan çıkar
                            System.out.printf("  ✓ 1. Yol'dan %s yüklendi (Gişe Giriş: %.2f)%n", arac.getAracPlaka(), arac.getGiseGirisSaati());
                            eylemGerceklestiMi = true;
                            sonBinenAracSaati = arac.getGiseGirisSaati();
                            feribotZamani = Math.max(feribotZamani, sonBinenAracSaati);
                        }
                    }
                }

                // 2. YOL KONTROLÜ
                if (!yuklemeYolu2.isEmpty()) {
                    Arac arac = yuklemeYolu2.peek();
                    if (arac.getGiseGirisSaati() <= feribotZamani || feribotZamani >= feribot.getRihtimKalkisSaati()) {
                        if (feribot.aracYukle(arac)) {
                            yuklemeYolu2.dequeue(); // Yükleme başarılı ise kuyruktan çıkar
                            System.out.printf("  ✓ 2. Yol'dan %s yüklendi (Gişe Giriş: %.2f)%n", arac.getAracPlaka(), arac.getGiseGirisSaati());
                            eylemGerceklestiMi = true;
                            sonBinenAracSaati = arac.getGiseGirisSaati();
                            feribotZamani = Math.max(feribotZamani, sonBinenAracSaati);
                        }
                    }
                }

                // KALKIŞ ŞARTLARININ KONTROLÜ (Zaman ilerledikçe kontrol et)
                if (feribot.kalkisSartlariSaglandiMi(feribotZamani)) {
                    feribot.setGercekKalkisSaati(sonBinenAracSaati);
                    feribotKalkti = true;
                    break;
                }

                // Eğer iki yolda da o anlık feribot zamanına uygun araç yoksa mecburen kalkış saatine ilerle
                if (!eylemGerceklestiMi) {
                    if (feribotZamani < feribot.getRihtimKalkisSaati()) {
                        feribotZamani = Math.round((feribotZamani + 0.05) * 100.0) / 100.0; // Double taşma koruması
                    } else {
                        // Planlanan kalkış saati geçtiyse ve şartlar hala sağlanmadıysa mecburen kaldır
                        feribot.setGercekKalkisSaati(Math.max(feribotZamani, sonBinenAracSaati));
                        feribotKalkti = true;
                        break;
                    }
                }
            }

            // PDF Sayfa 4-5 formatında yazdırma tetiklenir
            feribot.feribotBilgileriniYazdir();
            System.out.printf("%n>>> Feribot %d Rıhtımdan Ayrıldı! (Gerçekleşen Kalkış Saati: %.2f) <<<%n",
                    feribot.getFeribotNo(), feribot.getGercekKalkisSaati());
        }
    }

    public void tumAraclariListele() {
        System.out.println("\nSefer No    Araç No    Plaka      Araç Tipi");
        System.out.println("-------------------------------------------");
        
        List<Arac> sirali = new ArrayList<>(tumAraclar);
        sirali.sort(Comparator.comparingInt(Arac::getAracNo)); 

        for (Arac arac : sirali) {
            String sNo = arac.getSeferNo().isEmpty() ? "Beklemede" : arac.getSeferNo();
            System.out.printf("%-11s %-10d %-10s %-9d%n", 
                    sNo, arac.getAracNo(), arac.getAracPlaka(), arac.getAracTipi());
        }
    }

    public MyQueue<Arac> getYuklemeYolu1() { return yuklemeYolu1; }
    public MyQueue<Arac> getYuklemeYolu2() { return yuklemeYolu2; }
    public List<Feribot> getFeribotlar() { return feribotlar; }
    public List<Arac> getTumAraclar() { return tumAraclar; }
    public MyHashTable<String, Arac> getHashTable() { return hashTable; }
}
