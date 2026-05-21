package gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.Duration;
import logic.LimanYonetici;
import models.Arac;
import models.Feribot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainFrame extends Application {

    // ═══ PREMIUM AYDINLIK RENK PALETİ ══════════════════════
    static final String ORANGE      = "#f97316";
    static final String BG_GRADIENT = "linear-gradient(to bottom right, #e2e8f0, #e0f2fe)";
    static final String SURF        = "#ffffff";
    static final String BORDER      = "#e2e8f0";
    static final String TEXT        = "#1e293b";
    static final String TEXT2       = "#64748b";

    static final String WATER_GRAD  = "linear-gradient(to bottom, #a1c4fd 0%, #c2e9fb 100%)";

    static final String SKY         = "#0ea5e9";
    static final String CAR_C       = "#3b82f6";
    static final String TRK_C       = "#f97316";
    static final String GOLD        = "#f59e0b";
    static final String EMERALD     = "#10b981";
    static final String PURPLE      = "#8b5cf6";
    static final String RED         = "#ef4444";

    // ═══ SVG İKON PATHLERİ ══════════════════════════════════
    static final String SHIP_SVG = "M20 21c-1.39 0-2.78-.47-4-1.32-2.44 1.71-5.56 1.71-8 0C6.78 20.53 5.39 21 4 21H2v2h2c1.38 0 2.74-.35 4-.99 2.52 1.29 5.48 1.29 8 0 1.26.65 2.62.99 4 .99h2v-2h-2zM3.95 19H4c1.6 0 3.02-.88 4-2 .98 1.12 2.4 2 4 2s3.02-.88 4-2c.98 1.12 2.4 2 4 2h.05l1.89-6.68c.08-.26.06-.54-.06-.78s-.34-.42-.6-.5L20 10.9V3c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v4H6V5H4v8l-1.42 1.15c-.26.08-.48.26-.6.5s-.15.52-.06.78L3.95 19zM6 7h12v4H6V7z";
    static final String CAR_SVG = "M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z";
    static final String TRUCK_SVG = "M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4zM6 18c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm13.5-8.5l1.96 2.5H17V9.5h2.5zm-1.5 8.5c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1z";

    // ═══ STATE ══════════════════════════════════════════════
    LimanYonetici yonetici;
    SimulasyonMotoru motor;

    // ═══ UI ALANLARI ════════════════════════════════════════
    private Label  saatLbl;
    private Label  statFerLbl, statAracLbl, statHashLbl, statKuyLbl;
    private VBox   yol1Inner, yol2Inner, giseInner;
    private Label  yol1Cnt, yol2Cnt, giseCnt;
    private VBox   olayInner;
    private HBox   seferCards;
    StackPane      merkezPane;
    private Button oynatBtn;
    
    // MANTIKLI ERİŞİM DÜZELTMESİ: Simülasyon motorunun doğrudan erişebilmesi için public yapıldı
    public int simH = 9;
    public int simM = 0;
    private Timeline saatTL;

    private int oncekiAlt = -1, oncekiUst = -1;
    private List<VBox> altSlotlar = new ArrayList<>();
    private List<VBox> ustSlotlar = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        yonetici = new LimanYonetici();
        motor    = new SimulasyonMotoru(this, yonetici);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_GRADIENT + "; -fx-font-family:'Segoe UI';");
        root.setTop(buildTop());
        root.setCenter(buildCenter());
        root.setBottom(buildBottom());

        Scene scene = new Scene(root, 1500, 920);

        String customScrollbarCss = "data:text/css," +
                ".scroll-pane { -fx-background-color: transparent; -fx-background: transparent; }" +
                ".scroll-pane > .viewport { -fx-background-color: transparent; }" +
                ".scroll-bar:vertical { -fx-background-color: transparent; -fx-width: 6px; }" +
                ".scroll-bar:vertical .thumb { -fx-background-color: #cbd5e1; -fx-background-radius: 10px; }" +
                ".scroll-bar:vertical .thumb:hover { -fx-background-color: #94a3b8; }" +
                ".scroll-bar:vertical .track, .scroll-bar:vertical .increment-button, .scroll-bar:vertical .decrement-button { -fx-background-color: transparent; -fx-padding: 0; }";
        scene.getStylesheets().add(customScrollbarCss);

        stage.setScene(scene);
        stage.setTitle("Liman Simülasyon Sistemi · Premium Kontrol Ekranı");
        stage.setMinWidth(1300); stage.setMinHeight(800);
        stage.show();

        startSaatTL();
        Platform.runLater(this::veriYukle);
    }

    private VBox buildTop() {
        VBox v = new VBox(0);
        v.getChildren().addAll(buildHeader(), buildStats(), buildKontrol());
        return v;
    }

    private HBox buildHeader() {
        HBox h = new HBox(20);
        h.setPadding(new Insets(15, 35, 15, 35));
        h.setAlignment(Pos.CENTER_LEFT);
        h.setStyle("-fx-background-color: transparent;");

        StackPane logo = new StackPane();
        Circle c = new Circle(26, Color.web(SKY));
        logo.getChildren().addAll(c, svgIcon(SHIP_SVG, SURF, 1.4));
        logo.setEffect(new DropShadow(20, Color.web(SKY + "88")));

        VBox titleBox = new VBox(2);
        HBox t1row = new HBox(6);
        t1row.getChildren().addAll(lbl("LİMAN", 26, true, TEXT), lbl("SİMÜLASYONU", 26, true, SKY));
        titleBox.getChildren().addAll(t1row, lbl("Gelişmiş Simülasyon Motoru · V2.0 Premium", 12, false, TEXT2));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        HBox saatBox = new HBox(12);
        saatBox.setAlignment(Pos.CENTER);
        saatBox.setPadding(new Insets(10, 25, 10, 25));
        saatBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 30;");
        saatBox.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15,23,42,0.04), 10, 0, 0, 4));
        saatLbl = lbl("09:00", 18, true, SKY);
        saatBox.getChildren().addAll(lbl("Sistem Saati", 12, false, TEXT2), saatLbl);

        h.getChildren().addAll(logo, titleBox, sp, saatBox);
        return h;
    }

    private HBox buildStats() {
        HBox row = new HBox(25);
        row.setPadding(new Insets(0, 35, 15, 35));
        row.setStyle("-fx-background-color: transparent;");

       statFerLbl  = lbl("0/0", 32, true, TEXT);
        statAracLbl = lbl("0", 32, true, TEXT);
        statHashLbl = lbl("0", 32, true, TEXT);
        statKuyLbl  = lbl("0", 32, true, TEXT);

        VBox c1 = statCard(SHIP_SVG, "KALKAN FERİBOT", statFerLbl, SKY);
        VBox c2 = statCard(CAR_SVG, "YÜKLENEN ARAÇ", statAracLbl, EMERALD);
        VBox c3 = statCard(SHIP_SVG, "HASH KAYIT", statHashLbl, PURPLE);
        VBox c4 = statCard(TRUCK_SVG, "KUYRUK BEKLEYEN", statKuyLbl, ORANGE);

        for (VBox v : List.of(c1, c2, c3, c4)) HBox.setHgrow(v, Priority.ALWAYS);
        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private VBox statCard(String svg, String title, Label val, String accent) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #fefefe); -fx-background-radius: 20px;");
        card.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15, 23, 42, 0.04), 15, 0, 0, 6));

        Label titleLbl = lbl(title, 11, true, TEXT2);

        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        val.setStyle("-fx-font-size: 30px; -fx-font-weight: 800; -fx-text-fill: " + accent + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane iconBg = new StackPane();
        iconBg.setPrefSize(40, 40);
        iconBg.setStyle("-fx-background-color: " + accent + "15; -fx-background-radius: 12px;");
        iconBg.getChildren().add(svgIcon(svg, accent, 1.0));

        content.getChildren().addAll(val, spacer, iconBg);
        card.getChildren().addAll(titleLbl, content);
        return card;
    }

    private HBox buildKontrol() {
        HBox h = new HBox(15);
        h.setPadding(new Insets(10, 35, 20, 35));
        h.setAlignment(Pos.CENTER_LEFT);

        oynatBtn = ctrlBtn("▶ Oynat", "linear-gradient(to right, #10b981, #059669)", SURF);
        Button adimBtn = ctrlBtn("→ Adımla", "linear-gradient(to right, #0ea5e9, #0284c7)", SURF);
        Button sifirBtn = ctrlBtn("↺ Sıfırla", SURF, TEXT);
        sifirBtn.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15,23,42,0.04), 10, 0, 0, 4));

        oynatBtn.setOnAction(e -> toggleOynat());
        adimBtn.setOnAction(e -> motor.adimAt());
        sifirBtn.setOnAction(e -> sifirla());

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button ekleBtn = ctrlBtn("+ Yeni Araç Ekle", "linear-gradient(to right, #f59e0b, #d97706)", SURF);
        ekleBtn.setOnAction(e -> new AracEkleForm((Stage) ekleBtn.getScene().getWindow(), yonetici, this)
                .showAndWait().ifPresent(a -> { yenile(); olay(saatStr(), a.getPlaka() + " eklendi", EMERALD); }));

        h.getChildren().addAll(oynatBtn, adimBtn, sifirBtn, sp, ekleBtn);
        return h;
    }

    private void toggleOynat() {
        if (motor.isCalisiyor()) {
            motor.durdur();
            oynatBtn.setText("▶ Oynat");
            oynatBtn.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); -fx-text-fill: white; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:10 24; -fx-background-radius:30; -fx-cursor:hand;");
        } else {
            motor.oynat();
            oynatBtn.setText("⏸ Duraklat");
            oynatBtn.setStyle("-fx-background-color: linear-gradient(to right, #ef4444, #dc2626); -fx-text-fill: white; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:10 24; -fx-background-radius:30; -fx-cursor:hand;");
        }
    }

    private HBox buildCenter() {
        HBox center = new HBox(20);
        center.setPadding(new Insets(0, 35, 15, 35));
        VBox.setVgrow(center, Priority.ALWAYS);

        VBox sol = buildSol();
        sol.setMinWidth(320); sol.setMaxWidth(340);

        merkezPane = new StackPane();
        merkezPane.setStyle("-fx-background-color: " + WATER_GRAD + "; -fx-background-radius: 25;");
        merkezPane.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15,23,42,0.08), 20, 0, 0, 10));
        HBox.setHgrow(merkezPane, Priority.ALWAYS);
        merkezBos();

        VBox sag = buildSag();
        sag.setMinWidth(340); sag.setMaxWidth(360);

        center.getChildren().addAll(sol, merkezPane, sag);
        return center;
    }

    private VBox buildSol() {
        VBox panel = new VBox(15);
        panel.setStyle("-fx-background-color: transparent;");

        giseCnt = badge("0", PURPLE);
        giseInner = new VBox(8); giseInner.setPadding(new Insets(5, 10, 5, 0));
        VBox giseSec = queueSection("GİŞE ÖNCESİ", "Zaman Sıralı", giseCnt, giseInner);

        yol1Cnt = badge("0", SKY);
        yol1Inner = new VBox(8); yol1Inner.setPadding(new Insets(5, 10, 5, 0));
        VBox y1Sec = queueSection("1. YÜKLEME YOLU", "FIFO Akışı", yol1Cnt, yol1Inner);

        yol2Cnt = badge("0", SKY);
        yol2Inner = new VBox(8); yol2Inner.setPadding(new Insets(5, 10, 5, 0));
        VBox y2Sec = queueSection("2. YÜKLEME YOLU", "FIFO Akışı", yol2Cnt, yol2Inner);

        giseSec.setMinHeight(150); giseSec.setPrefHeight(300); giseSec.setMaxHeight(Double.MAX_VALUE);
        y1Sec.setMinHeight(150); y1Sec.setPrefHeight(300); y1Sec.setMaxHeight(Double.MAX_VALUE);
        y2Sec.setMinHeight(150); y2Sec.setPrefHeight(300); y2Sec.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(giseSec, Priority.ALWAYS);
        VBox.setVgrow(y1Sec, Priority.ALWAYS);
        VBox.setVgrow(y2Sec, Priority.ALWAYS);

        panel.getChildren().addAll(giseSec, y1Sec, y2Sec);
        return panel;
    }

    private VBox queueSection(String title, String sub, Label badge, VBox inner) {
        VBox v = new VBox(10);
        v.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #fcfdfe); -fx-background-radius: 20px; -fx-padding: 15; -fx-border-color: rgba(226,232,240,0.7); -fx-border-width: 1.5px; -fx-border-radius: 20px;");
        v.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15,23,42,0.03), 15, 0, 0, 8));

        HBox hdr = new HBox(8);
        hdr.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        hdr.getChildren().addAll(new VBox(lbl(title, 13, true, TEXT), lbl(sub, 10, false, TEXT2)), sp, badge);

        ScrollPane spane = new ScrollPane(inner);
        spane.setFitToWidth(true);
        spane.setStyle("-fx-background-color:transparent; -fx-background: transparent; -fx-border-color:transparent;");
        VBox.setVgrow(spane, Priority.ALWAYS);

        v.getChildren().addAll(hdr, hr(), spane);
        return v;
    }

    private VBox buildSag() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #fcfdfe); -fx-background-radius: 20px; -fx-padding: 15; -fx-border-color: rgba(226,232,240,0.7); -fx-border-width: 1.5px; -fx-border-radius: 20px;");
        panel.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15,23,42,0.03), 15, 0, 0, 8));

        HBox hdr = new HBox(8);
        hdr.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button clr = new Button("Temizle");
        clr.setStyle("-fx-background-color:#f1f5f9; -fx-text-fill:" + TEXT + "; -fx-font-size:11px; -fx-cursor:hand; -fx-background-radius:12; -fx-padding: 4 12;");
        clr.setOnAction(e -> olayInner.getChildren().clear());
        hdr.getChildren().addAll(lbl("SİSTEM LOGLARI", 13, true, TEXT), sp, clr);

        olayInner = new VBox(6);
        olayInner.setPadding(new Insets(5, 10, 5, 0));
        ScrollPane sp2 = new ScrollPane(olayInner);
        sp2.setFitToWidth(true);
        sp2.setStyle("-fx-background-color:transparent; -fx-background: transparent; -fx-border-color:transparent;");
        VBox.setVgrow(sp2, Priority.ALWAYS);

        panel.getChildren().addAll(hdr, hr(), sp2);
        return panel;
    }

    void merkezBos() {
        merkezPane.getChildren().clear();
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(svgIcon(SHIP_SVG, "#ffffff", 5.0), lbl("Simülasyonu başlatmak için Oynat'a tıklayın", 16, true, "#ffffff"));
        merkezPane.getChildren().add(box);
    }

    void refreshMerkez(Feribot f) {
        if (f == null) { merkezBos(); return; }
        merkezPane.getChildren().clear();

        VBox v = new VBox(22);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(20));

        HBox topRow = new HBox(); topRow.setAlignment(Pos.CENTER);
        Label fb = new Label("FERİBOT " + f.getFeribotNo() + "  •  " + f.getSeferNo());
        fb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #1e293b; -fx-padding: 8 26; -fx-background-radius: 20px;");
        fb.setEffect(new DropShadow(10, Color.rgb(15,23,42,0.15)));
        topRow.getChildren().add(fb);

        VBox hull = new VBox(18);
        hull.setMaxWidth(420);
        hull.setPadding(new Insets(15, 35, 35, 35));
        hull.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f8fafc); " +
                "-fx-background-radius: 180px 180px 25px 25px; " +
                "-fx-border-color: #94a3b8; -fx-border-width: 3.5px; " +
                "-fx-border-radius: 180px 180px 25px 25px;");
        hull.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15,23,42,0.12), 25, 0, 4, 12));

        HBox bridge = new HBox(); bridge.setAlignment(Pos.CENTER); bridge.setPadding(new Insets(6, 0, 6, 0));
        bridge.setStyle("-fx-background-color: linear-gradient(to right, #7dd3fc, #38bdf8); -fx-background-radius: 60px 60px 4px 4px;");
        bridge.getChildren().add(lbl("KAPTAN KÖŞKÜ / BRIDGE", 10, true, "#ffffff"));

        int altD = f.getAltKatDoluluk(), ustD = f.getUstKatDoluluk();
        VBox ustKat = buildKat("ÜST KAT (Otomobil)", ustD, 5, false);
        VBox altKat = buildKat("GİRİŞ KATI (Ağır Vasıta)", altD, 5, true);

        HBox times = new HBox(30);
        times.setAlignment(Pos.CENTER);
        times.getChildren().addAll(
                lbl("Giriş: " + fmt(f.getRihtimGirisSaati()), 13, true, TEXT2),
                lbl("Kalkış: " + fmt(f.getRihtimKalkisSaati()), 13, true, TEXT2)
        );

        hull.getChildren().addAll(bridge, times, ustKat, altKat);

        VBox sart = buildKalkisSartlari(f);
        sart.setMaxWidth(420);

        v.getChildren().addAll(topRow, hull, sart);
        merkezPane.getChildren().add(v);

        animateNewSlots(altSlotlar, oncekiAlt, altD);
        animateNewSlots(ustSlotlar, oncekiUst, ustD);
        oncekiAlt = altD; oncekiUst = ustD;
    }

    private VBox buildKat(String title, int dolu, int kap, boolean agir) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        String renk = agir ? TRK_C : CAR_C;

        Label baslik = lbl(title + "  [" + dolu + "/" + kap + "]", 13, true, renk);
        baslik.setStyle("-fx-background-color: " + renk + "11; -fx-padding: 4 15; -fx-background-radius: 15; -fx-text-fill: " + renk + "; -fx-font-weight: bold;");
        box.getChildren().add(baslik);

        HBox slots = new HBox(12);
        slots.setAlignment(Pos.CENTER);
        List<VBox> list = agir ? altSlotlar : ustSlotlar;
        if(agir) altSlotlar.clear(); else ustSlotlar.clear();

        for (int i = 0; i < kap; i++) {
            VBox slot = buildSlot(i, dolu, agir, renk);
            slots.getChildren().add(slot);
            list.add(slot);
        }
        box.getChildren().add(slots);
        return box;
    }

    private VBox buildSlot(int idx, int dolu, boolean agir, String renk) {
        VBox slot = new VBox(6);
        slot.setAlignment(Pos.CENTER);
        slot.setPrefSize(58, 70);
        boolean isDolu = idx < dolu;

        if (isDolu) {
            slot.setStyle("-fx-background-color: white; -fx-background-radius:12; -fx-border-radius:12; -fx-border-color:" + renk + "; -fx-border-width:2;");
            slot.getChildren().addAll(svgIcon(agir ? TRUCK_SVG : CAR_SVG, renk, 1.3), lbl("L" + (idx+1), 11, true, TEXT));
            slot.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.web(renk + "44"), 10, 0, 0, 4));
        } else {
            slot.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius:12; -fx-border-radius:12; -fx-border-color:#cbd5e1; -fx-border-width:2; -fx-border-style: dashed;");
            slot.getChildren().add(lbl("Boş", 12, true, "#94a3b8"));
        }
        return slot;
    }

    private void animateNewSlots(List<VBox> slots, int eskiDolu, int yeniDolu) {
        if (eskiDolu < 0) return;
        for (int i = eskiDolu; i < yeniDolu && i < slots.size(); i++) {
            ScaleTransition st = new ScaleTransition(Duration.millis(400), slots.get(i));
            st.setFromX(0.3); st.setToX(1); st.setFromY(0.3); st.setToY(1);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        }
    }

    private VBox buildKalkisSartlari(Feribot f) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(16, 20, 16, 20));
        box.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 16px; -fx-border-color: #e2e8f0; -fx-border-width: 1px;");
        box.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15,23,42,0.04), 15, 0, 0, 5));

        int alt = f.getAltKatDoluluk(), ust = f.getUstKatDoluluk();
        double aP = alt/5.0, uP = ust/5.0;

        box.getChildren().addAll(
                lbl("Gerçek Zamanlı Kalkış Koşulları Kontrolü", 13, true, TEXT),
                hr(),
                sartRow("Her iki katın da en az %50 dolu olması durumu", aP>=0.5 && uP>=0.5, EMERALD),
                sartRow("Yalnızca bir katın %100 dolu olması durumu", aP>=1.0 || uP>=1.0, EMERALD),
                sartRow("Her iki katın da %100 dolu olması (Anlık Erken Kalkış)", aP>=1.0 && uP>=1.0, RED)
        );
        return box;
    }

    private HBox sartRow(String t, boolean ok, String col) {
        HBox h = new HBox(12); h.setAlignment(Pos.CENTER_LEFT);
        Label ic = lbl(ok ? "✓" : "○", 16, true, ok ? col : "#cbd5e1");
        if(ok) ic.setEffect(new DropShadow(6, Color.web(col)));
        h.getChildren().addAll(ic, lbl(t, 13, ok, ok ? TEXT : TEXT2));
        return h;
    }

    private VBox buildBottom() {
        VBox outer = new VBox(10);
        outer.setPadding(new Insets(0, 35, 20, 35));
        outer.setStyle("-fx-background-color: transparent;");
        outer.getChildren().add(lbl("PLANLANAN SEFER PROGRAMI", 13, true, TEXT2));

        seferCards = new HBox(15);
        ScrollPane sp = new ScrollPane(seferCards);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:transparent; -fx-background: transparent; -fx-border-color:transparent;");
        outer.getChildren().add(sp);
        return outer;
    }

    void refreshSeferBar() {
        seferCards.getChildren().clear();
        List<Feribot> fl = yonetici.getFeribotlar();
        int aktif = motor.getAktifIdx();
        for (int i = 0; i < fl.size(); i++) {
            Feribot f = fl.get(i);
            boolean gecti = i < aktif, anlik = i == aktif;
            String dRenk = gecti ? EMERALD : (anlik ? SKY : TEXT2);

            VBox c = new VBox(8);
            c.setPadding(new Insets(14, 22, 14, 22)); c.setMinWidth(190);
            c.setStyle("-fx-background-color: " + SURF + "; -fx-background-radius: 16px; -fx-border-color:" + dRenk + "44; -fx-border-width: 2px; -fx-border-radius: 16px;");
            c.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(15,23,42,0.04), 10, 0, 0, 4));

            c.getChildren().addAll(
                    lbl("Feribot " + f.getFeribotNo(), 14, true, TEXT),
                    badge(gecti ? "KALKTI" : (anlik ? "RIHTIMDA" : "BEKLİYOR"), dRenk),
                    lbl("Plan. Saat: " + fmt(f.getRihtimKalkisSaati()), 12, false, TEXT2)
            );
            seferCards.getChildren().add(c);
        }
    }

    public void yenile() {
        refreshKuyruklar();
        refreshMerkez(motor.getAktifFeribot());
        refreshSeferBar();

        statFerLbl.setText(motor.getAktifIdx() + "/" + yonetici.getFeribotlar().size());
        statAracLbl.setText(String.valueOf(motor.getYuklenenSayi()));
        statHashLbl.setText(String.valueOf(yonetici.getTumAraclar().size()));
        statKuyLbl.setText(String.valueOf(yonetici.getYuklemeYolu1().size() + yonetici.getYuklemeYolu2().size()));
    }

    private void refreshKuyruklar() {
        drainQueue(yonetici.getYuklemeYolu1(), yol1Inner, yol1Cnt);
        drainQueue(yonetici.getYuklemeYolu2(), yol2Inner, yol2Cnt);

        giseInner.getChildren().clear();
        List<Arac> all = new ArrayList<>(yonetici.getTumAraclar());
        all.sort(Comparator.comparingDouble(Arac::getGiseGirisSaati));
        giseCnt.setText(String.valueOf(all.size()));

        for (int i = 0; i < Math.min(all.size(), 15); i++) {
            Arac a = all.get(i);
            giseInner.getChildren().add(queueItem(a, false, false));
        }
    }

    private void drainQueue(datastructures.MyQueue<Arac> q, VBox box, Label badge) {
        List<Arac> items = new ArrayList<>();
        while (!q.isEmpty()) items.add(q.dequeue());
        for (Arac a : items) q.enqueue(a);

        box.getChildren().clear(); badge.setText(String.valueOf(items.size()));
        if (items.isEmpty()) { box.getChildren().add(lbl("Kuyrukta Araç Yok", 12, false, TEXT2)); return; }

        for (int i = 0; i < items.size(); i++) box.getChildren().add(queueItem(items.get(i), i==0, i==items.size()-1));
    }

    private VBox queueItem(Arac a, boolean isHead, boolean isTail) {
        boolean agir = a.getAracTipi() == 1;
        String col = agir ? TRK_C : CAR_C;
        VBox wrap = new VBox(4);

        if (isHead) wrap.getChildren().add(badge("BAŞ (HEAD) ↓", EMERALD));

        HBox h = new HBox(12);
        h.setAlignment(Pos.CENTER_LEFT); h.setPadding(new Insets(10, 15, 10, 15));
        h.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-border-color: " + col + "33; -fx-border-radius: 12px; -fx-border-width: 1.5px;");

        VBox info = new VBox(3);
        info.getChildren().addAll(
                lbl(a.getPlaka(), 14, true, TEXT),
                lbl("No: " + a.getAracNo() + "  |  Saat: " + fmt(a.getGiseGirisSaati()), 11, false, TEXT2)
        );

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        h.getChildren().addAll(svgIcon(agir ? TRUCK_SVG : CAR_SVG, col, 1.2), info, sp);
        wrap.getChildren().add(h);

        if (isTail && !isHead) wrap.getChildren().add(badge("SON (TAIL) ↑", ORANGE));
        return wrap;
    }

    void veriYukle() {
        try { yonetici.yukle(); oncekiAlt=-1; oncekiUst=-1; yenile(); olay(saatStr(), "Sistem aktif hale getirildi, veriler başarıyla yüklendi.", SKY); }
        catch (Exception e) { olay("HATA", e.getMessage(), RED); }
    }

    private void sifirla() {
        motor.durdur(); oynatBtn.setText("▶ Oynat"); oynatBtn.setStyle("-fx-background-color: linear-gradient(to right, #10b981, #059669); -fx-text-fill: white; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:10 24; -fx-background-radius:30; -fx-cursor:hand;");
        yonetici = new LimanYonetici(); motor = new SimulasyonMotoru(this, yonetici);
        oncekiAlt = -1; oncekiUst = -1; simH = 9; simM = 0; olayInner.getChildren().clear();
        merkezBos(); veriYukle();
    }

    void olay(String saat, String msg, String rnk) {
        Platform.runLater(() -> {
            HBox row = new HBox(10); row.setPadding(new Insets(10, 12, 10, 12));
            row.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 10;");

            Label sLbl = lbl(saat, 11, true, TEXT2);
            sLbl.setMinWidth(40);

            Label mLbl = lbl(msg, 12, true, rnk);
            mLbl.setWrapText(true);
            mLbl.setMaxWidth(250);

            row.getChildren().addAll(sLbl, mLbl);

            FadeTransition ft = new FadeTransition(Duration.millis(500), row);
            ft.setFromValue(0); ft.setToValue(1); ft.play();

            olayInner.getChildren().add(0, row);
            if (olayInner.getChildren().size() > 50) olayInner.getChildren().remove(50);
        });
    }
    
    // MANTIKLI SÜREÇ ENTEGRASYONU: Simülasyon motorunun araç gişe saatiyle karşılaştırabileceği double zamanı döner.
    public double getSimulasyonSaati() {
        return simH + (simM / 100.0);
    }

    // GÜVENLİ SETTER METODU: Simülasyon motoru saati ileri sardığında ana ekran verilerini güvenle günceller.
    public void simulasyonSaatiniSetEt(int h, int m) {
        this.simH = h;
        this.simM = m;
        saatLbl.setText(saatStr());
        yenile();
    }

 
    private void startSaatTL() {
        // Gerçek dünyadaki her 1 saniyede bir tetiklenir (1.0 saniye)
        saatTL = new Timeline(new KeyFrame(Duration.seconds(1.0), e -> {
            if (motor.isCalisiyor()) {
                // EĞER SİMÜLASYON OYNATILIYORSA (PLAY):
                // Motor içerisinden adımlama fonksiyonunu çağırır.
                motor.adimAt();
                // KRİTİK DÜZELTME: Saat yazısını her saniye ekranda canlı canlı güncelliyoruz!
                saatLbl.setText(saatStr());
            } else {
                // EĞER SİMÜLASYON DURAKLATILDIYSA VEYA BAŞLAMADIYSA (PAUSE):
                // Zaman kendi kendine akmaz, "Adımla" butonuna basılmasını bekler.
                saatLbl.setText(saatStr());
            }
        }));
        saatTL.setCycleCount(Timeline.INDEFINITE);
        saatTL.play();
    }
   
    String saatStr() { return String.format("%02d:%02d", simH, simM); }
    private static String fmt(double d) { return String.format("%.2f", d); }

    static Label lbl(String t, double s, boolean b, String c) {
        Label l = new Label(t); l.setStyle("-fx-font-size:" + s + "px; " + (b ? "-fx-font-weight:900; " : "") + "-fx-text-fill:" + c + ";"); return l;
    }
    static Label badge(String t, String c) {
        Label l = new Label(t); l.setStyle("-fx-text-fill: white; -fx-background-color:" + c + "; -fx-font-size:10px; -fx-font-weight:bold; -fx-padding:4 10; -fx-background-radius:15;"); return l;
    }
    static Button ctrlBtn(String t, String bg, String fg) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:" + bg + "; -fx-text-fill:" + fg + "; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:10 24; -fx-background-radius:30; -fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setOpacity(0.85)); b.setOnMouseExited(e -> b.setOpacity(1.0)); return b;
    }
    static SVGPath svgIcon(String d, String c, double scale) {
        SVGPath p = new SVGPath(); p.setContent(d); p.setFill(Color.web(c)); p.setScaleX(scale); p.setScaleY(scale); return p;
    }
    private static Node hr() { Region r = new Region(); r.setPrefHeight(1); r.setStyle("-fx-background-color: #e2e8f0;"); return r; }

    public static void main(String[] args) { launch(args); }
}
