/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;
import models.Arac;
import models.Feribot;
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
                    boolean hazirMi = Boolean.parseBoolean(getTagValue("kalkisaHazirMi", element));
                    
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
                    String plaka = getTagValue("plaka", element);
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
        araclariYaz(mevcutAraclar);
    }
    
    
    public static void araclariYaz(List<Arac> araclar) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            Element root = doc.createElement("araclar");
            doc.appendChild(root);
            
            for (Arac arac : araclar) {
                Element aracElem = doc.createElement("arac");
                root.appendChild(aracElem);
                
                addElement(doc, aracElem, "seferNo", arac.getSeferNo());
                addElement(doc, aracElem, "aracNo", String.valueOf(arac.getAracNo()));
                addElement(doc, aracElem, "plaka", arac.getPlaka());
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
                addElement(doc, feribotElem, "kalkisaHazirMi", data[4]);
            }
            
            saveDocument(doc, FERIBOT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private static void createDefaultAracXml() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            Element root = doc.createElement("araclar");
            doc.appendChild(root);
            
            String[][] aracData = {
                {"", "1", "34ABC01", "9.30", "1"},
                {"", "2", "34ABC02", "9.35", "2"},
                {"", "3", "34ABC03", "9.40", "2"},
                {"", "4", "34ABC04", "9.45", "1"},
                {"", "5", "34ABC05", "10.00", "2"},
                {"", "6", "34ABC06", "10.05", "2"},
                {"", "7", "34ABC07", "10.10", "1"},
                {"", "8", "34ABC08", "10.15", "2"}
            };
            
            for (String[] data : aracData) {
                Element aracElem = doc.createElement("arac");
                root.appendChild(aracElem);
                addElement(doc, aracElem, "seferNo", data[0]);
                addElement(doc, aracElem, "aracNo", data[1]);
                addElement(doc, aracElem, "plaka", data[2]);
                addElement(doc, aracElem, "giseGirisSaati", data[3]);
                addElement(doc, aracElem, "aracTipi", data[4]);
            }
            
            saveDocument(doc, ARAC_FILE);
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
