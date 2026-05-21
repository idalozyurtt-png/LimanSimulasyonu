
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

    // =====================================================
    // VERİLERİ YÜKLE
    // =====================================================

    public void yukle() {

        feribotlar = XmlManager.feribotlariOku();
        tumAraclar = XmlManager.araclariOku();

        sistemiYenidenDuzenle();
    }

    // =====================================================
    // SİSTEMİ YENİDEN OLUŞTUR
    // =====================================================

    private void sistemiYenidenDuzenle() {

        // QUEUE'LARI TEMİZLE
        yuklemeYolu1.clear();
        yuklemeYolu2.clear();

        // HASH TABLOSUNU SIFIRLA
        hashTable = new MyHashTable<>();

        // ARAÇLARI GİŞE GİRİŞ SAATİNE GÖRE SIRALA
        tumAraclar.sort(
                Comparator.comparingDouble(Arac::getGiseGirisSaati)
        );

        // YENİDEN NUMARA VER
        int aracNo = 1;

        for (Arac arac : tumAraclar) {

            arac.setAracNo(aracNo++);

            // HASH TABLOSU GÜNCELLE
            hashTable.put(arac.getPlaka(), arac);
        }

        // YOLLARA DAĞIT
        yonlendirmeSayaci = 0;

        for (Arac arac : tumAraclar) {

            yonlendirmeSayaci++;

            // TEK / ÇİFT MANTIĞI
            if (yonlendirmeSayaci % 2 == 1) {
                yuklemeYolu1.enqueue(arac);
            } else {
                yuklemeYolu2.enqueue(arac);
            }
        }
    }

    // =====================================================
    // YENİ ARAÇ EKLE
    // =====================================================

    public boolean aracEkle(String plaka,
                            double girisSaati,
                            int aracTipi) {

        // HASH KONTROL
        if (hashTable.containsKey(plaka)) {

            System.out.println(
                    "HATA: " + plaka +
                    " plakalı araç sistemde zaten kayıtlı!"
            );

            return false;
        }

        // GEÇİCİ NO (yeniden sıralamada değişecek)
        Arac yeniArac = new Arac(
                "",
                0,
                plaka,
                girisSaati,
                aracTipi
        );

        // MASTER LİSTEYE EKLE
        tumAraclar.add(yeniArac);

        // TÜM SİSTEMİ YENİDEN KUR
        sistemiYenidenDuzenle();

        // XML'E YAZ
        XmlManager.tumAraclariKaydet(tumAraclar);

        System.out.println("✓ Yeni araç eklendi: " + yeniArac);

        return true;
    }

    // =====================================================
    // SİMÜLASYON
    // =====================================================

    public void simulasyonuBaslat() {

        System.out.println(
                "\n========== LİMAN SİMÜLASYONU BAŞLIYOR ==========\n"
        );

        for (Feribot feribot : feribotlar) {

            System.out.printf(
                    "\n>>> Feribot %d (Sefer: %s) rıhtıma yanaşıyor... (Giriş: %.2f)%n",
                    feribot.getFeribotNo(),
                    feribot.getSeferNo(),
                    feribot.getRihtimGirisSaati()
            );

            double simdikiZaman = feribot.getRihtimGirisSaati();
            double sonYuklemeZamani = simdikiZaman;

            boolean yuklemeDevam = true;

            while (yuklemeDevam && !feribot.isFull()) {

                boolean yuklendi = false;

                // =================================================
                // 1. YOL
                // =================================================

                if (!yuklemeYolu1.isEmpty()) {

                    Arac arac = yuklemeYolu1.dequeue();

                    if (feribot.aracYukle(arac)) {

                        arac.setSeferNo(feribot.getSeferNo());

                        System.out.printf(
                                "  ✓ 1. Yol'dan %s yüklendi%n",
                                arac.getPlaka()
                        );

                        yuklendi = true;

                        sonYuklemeZamani =
                                arac.getGiseGirisSaati();

                    } else {

                        yuklemeYolu1.enqueue(arac);
                    }
                }

                // =================================================
                // 2. YOL
                // =================================================

                if (!yuklemeYolu2.isEmpty()) {

                    Arac arac = yuklemeYolu2.dequeue();

                    if (feribot.aracYukle(arac)) {

                        arac.setSeferNo(feribot.getSeferNo());

                        System.out.printf(
                                "  ✓ 2. Yol'dan %s yüklendi%n",
                                arac.getPlaka()
                        );

                        yuklendi = true;

                        sonYuklemeZamani =
                                arac.getGiseGirisSaati();

                    } else {

                        yuklemeYolu2.enqueue(arac);
                    }
                }

                if (!yuklendi) {
                    yuklemeDevam = false;
                }

                // =================================================
                // KALKIŞ KONTROLÜ
                // =================================================

                if (feribot.kalkisSartlariSaglandiMi(simdikiZaman)) {
                    break;
                }

                simdikiZaman += 0.05;
            }

            double gercekKalkisSaati =
                    feribot.getRihtimKalkisSaati();

            if (feribot.kalkisSartlariSaglandiMi(sonYuklemeZamani)) {
                gercekKalkisSaati = sonYuklemeZamani;
            }

            feribot.feribotBilgileriniYazdir();

            System.out.printf(
                    "\n>>> Feribot %d KALKIŞ YAPIYOR! (%.2f)\n",
                    feribot.getFeribotNo(),
                    gercekKalkisSaati
            );
        }
    }

    // =====================================================
    // TÜM ARAÇLARI LİSTELE
    // =====================================================

    public void tumAraclariListele() {

        System.out.println(
                "\n========== TÜM ARAÇLAR =========="
        );

        List<Arac> sirali = new ArrayList<>(tumAraclar);

        sirali.sort(Comparator.comparingInt(Arac::getAracNo));

        for (Arac arac : sirali) {
            System.out.println(arac);
        }
    }

    // =====================================================
    // GETTER
    // =====================================================

    public MyQueue<Arac> getYuklemeYolu1() {
        return yuklemeYolu1;
    }

    public MyQueue<Arac> getYuklemeYolu2() {
        return yuklemeYolu2;
    }

    public List<Feribot> getFeribotlar() {
        return feribotlar;
    }

    public List<Arac> getTumAraclar() {
        return tumAraclar;
    }

    public MyHashTable<String, Arac> getHashTable() {
        return hashTable;
    }
}
