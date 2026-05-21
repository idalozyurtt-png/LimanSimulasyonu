package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import logic.LimanYonetici;
import models.Arac;
import models.Feribot;
import java.util.List;

/**
 * Adım adım + otomatik simülasyon motoru.
 * Kusursuz Sürüm: Sadece her iki kat birden %100 dolduğunda saat beklenmez.
 * Diğer tüm doluluk koşullarında planlanan kalkış saati harfiyen beklenir.
 */
public class SimulasyonMotoru {

    private final MainFrame frame;
    private LimanYonetici yonetici;

    private int aktifIdx = 0;
    private int yuklenenSayi = 0;
    private boolean calisiyor = false;
    private double hizCarpan = 1.0;
    private Timeline autoTimeline;

    private boolean siradakiYol1 = true; // Araçlar yol1 ve yol2'den sırayla alınır

    public SimulasyonMotoru(MainFrame frame, LimanYonetici yonetici) {
        this.frame = frame;
        this.yonetici = yonetici;
    }

    public void reset(LimanYonetici yeni) {
        durdur();
        this.yonetici = yeni;
        aktifIdx = 0;
        yuklenenSayi = 0;
        calisiyor = false;
        siradakiYol1 = true;
    }

    public void oynat() {
        if (calisiyor) return;
        calisiyor = true;
    }

    public void durdur() {
        calisiyor = false;
        if (autoTimeline != null) autoTimeline.stop();
    }

    public boolean isCalisiyor() { return calisiyor; }

    public void setHiz(double carpan) {
        this.hizCarpan = carpan;
        if (calisiyor) { durdur(); oynat(); }
    }

    // ── Simülasyon Mantığı Örgüsü ──────────────────
    public void adimAt() {
        List<Feribot> feribotlar = yonetici.getFeribotlar();
        if (feribotlar == null || feribotlar.isEmpty()) {
            frame.olay("--:--", "Önce veri yükleyin", MainFrame.RED);
            durdur(); return;
        }
        if (aktifIdx >= feribotlar.size()) {
            frame.olay("--:--", "✅ Tüm seferler tamamlandı", MainFrame.EMERALD);
            yonetici.tumAraclariListele(); // PDF Madde 8 dökümü
            durdur(); return;
        }

        Feribot aktif = feribotlar.get(aktifIdx);
        double aktifSimulasyonSaati = frame.getSimulasyonSaati();

        // 1. KONTROL: Feribot henüz rıhtıma yanaşmadıysa zamanı ilerlet ve bekle
        if (aktifSimulasyonSaati < aktif.getRihtimGirisSaati()) {
            zamanliDakikaArtir();
            return;
        }

        // 2. KONTROL: Eğer kalkış koşulları (Zaman bağımlı veya bağımsız) sağlandıysa feribotu kaldır
        if (aktif.kalkisSartlariSaglandiMi(aktifSimulasyonSaati)) {
            feribotuKalkisYaptir(aktif, "Kalkış koşulları doğrulandı");
            return;
        }

        boolean yuklendi = false;
        
        // Sırayla birer birer (alternating) araç yükleme döngüsü
        for (int deneme = 0; deneme < 2 && !yuklendi; deneme++) {
            var kuyruk = siradakiYol1 ? yonetici.getYuklemeYolu1() : yonetici.getYuklemeYolu2();
            int yolNo = siradakiYol1 ? 1 : 2;

            if (!kuyruk.isEmpty()) {
                Arac a = kuyruk.peek(); // Elemanı silmeden önce gişe saati kontrolü
                
                // Araç gişe giriş saati henüz simülasyon saatine gelmediyse yüklenemez
                if (a.getGiseGirisSaati() > aktifSimulasyonSaati) {
                    siradakiYol1 = !siradakiYol1;
                    continue; 
                }

                kuyruk.dequeue();

                if (aktif.aracYukle(a)) {
                    yuklenenSayi++;
                    
                    // PDF Madde 25 gereği son binen araca göre kalkış saatini güncelle
                    aktif.setGercekKalkisSaati(a.getGiseGirisSaati());

                    boolean agir = a.getAracTipi() == 1;
                    frame.olay(frame.saatStr(),
                            a.getPlaka() + " → Feribot " + aktif.getFeribotNo()
                                    + " · " + (agir ? "Alt Kat" : "Üst Kat") + " [Yol " + yolNo + "]",
                            agir ? MainFrame.TRK_C : MainFrame.SKY);
                    
                    yuklendi = true;
                    siradakiYol1 = !siradakiYol1;

                    // 🔥 ANLIK KONTROL: Araç bindiği saniyede her iki kat birden %100 tam doldu mu?
                    // Sadece iki kat birden tam dolduysa saati beklemeden anında kalkar!
                    if (aktif.kalkisSartlariSaglandiMi(frame.getSimulasyonSaati())) {
                        feribotuKalkisYaptir(aktif, "Çift Kat %100 Doluluk (Erken Kalkış)");
                        return; 
                    }

                } else {
                    kuyruk.enqueue(a); // Sığmadı, sıraya geri bırak
                    siradakiYol1 = !siradakiYol1;
                }
            } else {
                siradakiYol1 = !siradakiYol1;
            }
        }

        // 3. KONTROL: Saat dolduysa, yükleme bittiyse ve koşullar okeyse uğurla
        if (!yuklendi && aktifSimulasyonSaati >= aktif.getRihtimKalkisSaati()) {
            if (aktif.kalkisSartlariSaglandiMi(aktifSimulasyonSaati)) {
                feribotuKalkisYaptir(aktif, "Planlanan kalkış saati tamamlandı");
                return;
            }
        }

        // Eğer feribot kalkmadıysa zamanı tam 5 dakika ileri alarak akışa devam et
        zamanliDakikaArtir();
    }

    private void feribotuKalkisYaptir(Feribot aktif, String neden) {
        aktif.feribotBilgileriniYazdir(); // Konsola rapor basar
        frame.olay(frame.saatStr(),
                "⚓ Feribot " + aktif.getFeribotNo() + " (" + aktif.getSeferNo() + ") rıhtımdan ayrıldı. (" + neden + ")",
                MainFrame.GOLD);
        aktifIdx++;
        siradakiYol1 = true;
        frame.yenile();
    }

    private void zamanliDakikaArtir() {
        // İster Oynat modunda olsun ister Adımla modunda, her tetiklemede ekrandaki saati tam 5 dakika ilerletir.
        frame.simM += 5;

        if (frame.simM >= 60) {
            frame.simM = frame.simM % 60;
            frame.simH++;
        }
        if (frame.simH > 23) {
            frame.simH = 0;
        }

        frame.yenile();
    }

    public int getAktifIdx() { return aktifIdx; }
    public int getYuklenenSayi() { return yuklenenSayi; }

    public Feribot getAktifFeribot() {
        List<Feribot> f = yonetici.getFeribotlar();
        if (f == null || f.isEmpty() || aktifIdx >= f.size()) return null;
        return f.get(aktifIdx);
    }
}
