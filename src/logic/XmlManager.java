package logic;

import models.Arac;
import models.Feribot;
import datastructures.MyHashTable; // Hazır HashSet yerine kendi yapımızı entegre ettik
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class XmlManager {

    private static final String DATA_DIR = "data/";
    private static final String FERIBOT_FILE = DATA_DIR + "feribotlar.xml";
    private static final String ARAC_FILE = DATA_DIR + "araclar.xml";

    static {
        new File(DATA_DIR).mkdirs();
    }

    public static List<Feribot> feribotlariOku() {
        List<Feribot> feribotlar = new ArrayList<>();
        File file = new File(FERIBOT_FILE);

        if (!file.exists()) {
            createDefaultFeribotXml();
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("feribot");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String seferNo = getTagValue("seferNo", element);
                    int feribotNo = Integer.parseInt(getTagValue("feribotNo", element));
                    double girisSaati = Double.parseDouble(getTagValue("rihtimGirisSaati", element));
                    double kalkisSaati = Double.parseDouble(getTagValue("rihtimKalkisSaati", element));
                    boolean hazirMi = Boolean.parseBoolean(getTagValue("feribotKalkisaHazirMi", element));

                    feribotlar.add(new Feribot(seferNo, feribotNo, girisSaati, kalkisSaati, hazirMi));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feribotlar;
    }

    public static List<Arac> araclariOku() {
        List<Arac> araclar = new ArrayList<>();
        File file = new File(ARAC_FILE);

        if (!file.exists()) {
            createDefaultAracXml();
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("arac");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String seferNo = getTagValue("seferNo", element);
                    int aracNo = Integer.parseInt(getTagValue("aracNo", element));
                    
                    // Şemadaki orijinal "aracPlaka" adına göre okuma yapılıyor
                    String plaka = getTagValue("aracPlaka", element);
                    if (plaka.isEmpty()) {
                        plaka = getTagValue("plaka", element); // Eski kayıtları kırmamak için koruma
                    }

                    double girisSaati = Double.parseDouble(getTagValue("giseGirisSaati", element));
                    int aracTipi = Integer.parseInt(getTagValue("aracTipi", element));

                    araclar.add(new Arac(seferNo, aracNo, plaka, girisSaati, aracTipi));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return araclar;
    }

    public static void aracEkle(Arac yeniArac, List<Arac> mevcutAraclar) {
        mevcutAraclar.add(yeniArac);
        tumAraclariKaydet(mevcutAraclar);
    }

    public static void tumAraclariKaydet(List<Arac> araclar) {
        try {
            araclar.sort(Comparator.comparingDouble(Arac::getGiseGirisSaati));

            int aracNo = 1;
            for (Arac arac : araclar) {
                arac.setAracNo(aracNo++);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("araclar");
            doc.appendChild(root);

            // ÖDEV YASAĞI KORUMASI: Hazır kütüphane yerine kendi yazdığın MyHashTable'ı entegre ettik
            MyHashTable<String, Boolean> plakaKontrolTablosu = new MyHashTable<>();

            for (Arac arac : araclar) {
                if (plakaKontrolTablosu.containsKey(arac.getAracPlaka())) {
                    continue;
                }
                plakaKontrolTablosu.put(arac.getAracPlaka(), true);

                Element aracElem = doc.createElement("arac");
                root.appendChild(aracElem);

                addElement(doc, aracElem, "seferNo", arac.getSeferNo());
                addElement(doc, aracElem, "aracNo", String.valueOf(arac.getAracNo()));
                
                // PDF Sayfa 3 Bölüm 3'teki "aracPlaka" ismine harfi harfine eşitlendi
                addElement(doc, aracElem, "aracPlaka", arac.getAracPlaka());
                
                addElement(doc, aracElem, "giseGirisSaati", String.valueOf(arac.getGiseGirisSaati()));
                addElement(doc, aracElem, "aracTipi", String.valueOf(arac.getAracTipi()));
            }

            saveDocument(doc, ARAC_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultFeribotXml() {
    try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("feribotlar");
        doc.appendChild(root);

        String[][] feribotData = {
                {"S101", "1", "9.00", "10.00", "false"},
                {"S102", "2", "11.00", "12.00", "false"},
                {"S103", "3", "13.00", "14.00", "false"},
                {"S104", "4", "15.00", "16.00", "false"}
        };

        for (String[] data : feribotData) {
            Element feribotElem = doc.createElement("feribot");
            root.appendChild(feribotElem);

            addElement(doc, feribotElem, "seferNo", data[0]);
            addElement(doc, feribotElem, "feribotNo", data[1]);
            addElement(doc, feribotElem, "rihtimGirisSaati", data[2]);
            addElement(doc, feribotElem, "rihtimKalkisSaati", data[3]);
            
            // HATA DÜZELTİLDİ: data[4] içerisindeki "false" değeri etiket içerisine başarıyla yazdırılıyor
            addElement(doc, feribotElem, "feribotKalkisaHazirMi", data[4]); 
        }
        saveDocument(doc, FERIBOT_FILE);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private static void createDefaultAracXml() {
        try {
            List<Arac> varsayilan = new ArrayList<>();
            // Hocanın paylaştığı tüm orijinal girdi test listesi sırasıyla işlendi
            varsayilan.add(new Arac("", 0, "63HRU19", 08.00, 1));
            varsayilan.add(new Arac("", 0, "63HRU24", 08.10, 2));
            varsayilan.add(new Arac("", 0, "63HRU16", 08.20, 1));
            varsayilan.add(new Arac("", 0, "63HRU30", 08.30, 2));
            varsayilan.add(new Arac("", 0, "63HRU05", 08.40, 2));
            varsayilan.add(new Arac("", 0, "63HRU32", 08.50, 2));
            varsayilan.add(new Arac("", 0, "63HRU08", 09.05, 2));
            varsayilan.add(new Arac("", 0, "63HRU15", 09.10, 1));
            varsayilan.add(new Arac("", 0, "63HRU31", 09.20, 1));
            varsayilan.add(new Arac("", 0, "63HRU33", 09.40, 2));
            varsayilan.add(new Arac("", 0, "63HRU02", 10.05, 2));
            varsayilan.add(new Arac("", 0, "63HRU06", 10.15, 1));
            varsayilan.add(new Arac("", 0, "63HRU11", 10.35, 2));
            varsayilan.add(new Arac("", 0, "63HRU14", 10.50, 2));
            varsayilan.add(new Arac("", 0, "63HRU17", 10.55, 1));
            varsayilan.add(new Arac("", 0, "63HRU26", 10.55, 1));
            varsayilan.add(new Arac("", 0, "63HRU29", 11.05, 2));
            varsayilan.add(new Arac("", 0, "63HRU28", 11.25, 1));
            varsayilan.add(new Arac("", 0, "63HRU04", 11.35, 1));
            varsayilan.add(new Arac("", 0, "63HRU25", 12.10, 2));
            varsayilan.add(new Arac("", 0, "63HRU01", 13.05, 1));
            varsayilan.add(new Arac("", 0, "63HRU10", 13.25, 1));
            varsayilan.add(new Arac("", 0, "63HRU20", 13.45, 2));
            varsayilan.add(new Arac("", 0, "63HRU07", 14.05, 1));
            varsayilan.add(new Arac("", 0, "63HRU03", 14.15, 2));
            varsayilan.add(new Arac("", 0, "63HRU12", 14.35, 1));
            varsayilan.add(new Arac("", 0, "63HRU09", 14.40, 2));
            varsayilan.add(new Arac("", 0, "63HRU18", 14.45, 1));
            varsayilan.add(new Arac("", 0, "63HRU23", 15.10, 1));
            varsayilan.add(new Arac("", 0, "63HRU22", 15.20, 1));
            varsayilan.add(new Arac("", 0, "63HRU27", 15.25, 2));
            varsayilan.add(new Arac("", 0, "63HRU21", 15.30, 1));
            varsayilan.add(new Arac("", 0, "63HRU13", 15.35, 2));

            tumAraclariKaydet(varsayilan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node.getFirstChild() != null) {
                return node.getFirstChild().getNodeValue();
            }
        }
        return "";
    }

    private static void addElement(Document doc, Element parent, String tag, String value) {
        Element elem = doc.createElement(tag);
        elem.appendChild(doc.createTextNode(value));
        parent.appendChild(elem);
    }

    private static void saveDocument(Document doc, String filePath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
}
