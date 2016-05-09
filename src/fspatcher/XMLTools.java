/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fspatcher;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import skyproc.*;

/**
 *
 * @author Francisco
 */
public class XMLTools {
    
    public static void addModsToXML(Mod merger) {
        try {
            File fXmlFile = new File("Custom.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document custom = dBuilder.parse(fXmlFile);


            Document newDoc = dBuilder.newDocument();


            ArrayList<Pair<String, Node>> modNodes = new ArrayList<>(0);
            Element rootElement = newDoc.createElement("lootification");
            newDoc.appendChild(rootElement);


            for (Pair<Mod, ArrayList<Pair<ARMO, KYWD>>> p : FSPatcher.modArmors) {
                boolean found = false;
                Node theMod = null;
                for (Pair<String, Node> q : modNodes) {
                    if (p.getBase().getName().contentEquals(q.getBase())) {
                        theMod = q.getVar();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Element newElement = newDoc.createElement("mod");
                    newElement.setAttribute("modName", p.getBase().getName());
                    rootElement.appendChild(newElement);
                    theMod = newElement;
                    Pair<String, Node> q = new Pair<>(newElement.getAttribute("modName"), theMod);
                    modNodes.add(q);
                }
                for (Pair<ARMO, KYWD> akPair : p.getVar()) {
                    boolean armorFound = false;
                    Node theArmor = null;
                    NodeList items = theMod.getChildNodes();
                    for (int i = 0; i < items.getLength(); i++) {
                        Node item = items.item(i);
                        if (item.getNodeType() == Node.ELEMENT_NODE) {
                            Element eItem = (Element) item;
                            if (eItem.getAttribute("EDID").contentEquals(akPair.getBase().getEDID())) {
                                theArmor = item;
                                armorFound = true;
                                break;
                            }
                        }
                    }
                    if (!armorFound) {
                        Element newElement = newDoc.createElement("item");
                        newElement.setAttribute("type", "armor");
                        newElement.setAttribute("EDID", akPair.getBase().getEDID());
                        theMod.appendChild(newElement);
                        theArmor = newElement;
                    }
                    Element key = newDoc.createElement("keyword");
                    key.setTextContent(akPair.getVar().getEDID());
                    theArmor.appendChild(key);
                }
            }

            for (Pair<Mod, ArrayList<Pair<WEAP, KYWD>>> p : FSPatcher.modWeapons) {
                boolean found = false;
                Node theMod = null;
                for (Pair<String, Node> q : modNodes) {
                    if (p.getBase().getName().contentEquals(q.getBase())) {
                        theMod = q.getVar();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Element newElement = newDoc.createElement("mod");
                    newElement.setAttribute("modName", p.getBase().getName());
                    rootElement.appendChild(newElement);
                    theMod = newElement;
                    Pair<String, Node> q = new Pair<>(newElement.getAttribute("modName"), theMod);
                    modNodes.add(q);
                }
                for (Pair<WEAP, KYWD> akPair : p.getVar()) {
                    boolean armorFound = false;
                    Node theArmor = null;
                    NodeList items = theMod.getChildNodes();
                    for (int i = 0; i < items.getLength(); i++) {
                        Node item = items.item(i);
                        if (item.getNodeType() == Node.ELEMENT_NODE) {
                            Element eItem = (Element) item;
                            if (eItem.getAttribute("EDID").contentEquals(akPair.getBase().getEDID())) {
                                theArmor = item;
                                armorFound = true;
                                break;
                            }
                        }
                    }
                    if (!armorFound) {
                        Element newElement = newDoc.createElement("item");
                        newElement.setAttribute("type", "weapon");
                        newElement.setAttribute("EDID", akPair.getBase().getEDID());
                        theMod.appendChild(newElement);
                        theArmor = newElement;
                    }
                    Element key = newDoc.createElement("keyword");
                    key.setTextContent(akPair.getVar().getEDID());
                    theArmor.appendChild(key);
                }
            }

            for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                String master = p.getVar().get(0).getFormMaster().print();
                master = master.substring(0, master.length() - 4);
                for (ARMO arm : p.getVar()) {
                    NodeList items = newDoc.getElementsByTagName("item");
                    for (int i = 0; i < items.getLength(); i++) {
                        Element eItem = (Element) items.item(i);
                        if (eItem.getAttribute("EDID").contentEquals(arm.getEDID())) {
                            Element newKey = newDoc.createElement("keyword");
                            newKey.setTextContent("aa_outfit_" + master + p.getBase());
                            eItem.appendChild(newKey);
                            for (Pair<String, ArrayList<String>> q : FSPatcher.tiers) {
                                if (q.getBase().contentEquals(p.getBase())) {
                                    for (String s : q.getVar()) {
                                        Element newTier = newDoc.createElement("keyword");
                                        newTier.setTextContent(s.replace(" ", ""));
                                        eItem.appendChild(newTier);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            newDoc.getDocumentElement().normalize();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(newDoc);

            StreamResult result = new StreamResult(new File("out.xml"));
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, "xml");
            transformer.transform(source, result);

            Document merged = mergeDocs(custom, newDoc);
            DOMSource sourceMerged = new DOMSource(merged);
            StreamResult resultMerged = new StreamResult(new File("Custom.xml"));
            transformer.transform(sourceMerged, resultMerged);


        } catch (Exception e) {
            SPGlobal.logException(e);
            JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
            SPGlobal.closeDebug();
        }
    }

    public static void processXML(Mod merger, Mod patch) {
        try {
            List<String> lines = Files.readAllLines(FileSystems.getDefault().getPath(SPGlobal.getPluginsTxt()), StandardCharsets.UTF_8);

            File fXmlFile = new File("Lootification.xml");
            File customXmlFile = new File("Custom.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document loot = dBuilder.parse(fXmlFile);
            Document custom = dBuilder.parse(customXmlFile);

            loot.getDocumentElement().normalize();
            custom.getDocumentElement().normalize();

            Document doc = mergeDocs(loot, custom);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("mod");

            for (int i = 0; i < nList.getLength(); i++) {
                Node theMod = nList.item(i);
                Element eElement = (Element) theMod;
                if (lines.contains(eElement.getAttribute("modName")) || !(FSPatcher.save.getBool(YourSaveFile.Settings.SKIP_INACTIVE_MODS))) {
                    if (!eElement.getAttribute("modName").contentEquals("Dragonborn.esm") || (eElement.getAttribute("modName").contentEquals("Dragonborn.esm") && FSPatcher.save.getBool(YourSaveFile.Settings.LOOTIFY_DRAGONBORN))) {
                        NodeList items = theMod.getChildNodes();
                        for (int j = 0; j < items.getLength(); j++) {
                            Node item = items.item(j);
                            if (item.getNodeType() == Node.ELEMENT_NODE) {
                                Element eItem = (Element) item;
                                if (eItem.getAttribute("type").contentEquals("weapon")) {
                                    WEAP weapon = (WEAP) merger.getMajor(eItem.getAttribute("EDID"), GRUP_TYPE.WEAP);
                                    if (weapon != null) {
                                        KeywordSet keys = weapon.getKeywordSet();
                                        NodeList kList = eItem.getElementsByTagName("keyword");
                                        for (int k = 0; k < kList.getLength(); k++) {
                                            Element eKey = (Element) kList.item(k);
                                            KYWD newKey = (KYWD) merger.getMajor(eKey.getTextContent(), GRUP_TYPE.KYWD);
                                            if (newKey != null) {
                                                keys.addKeywordRef(newKey.getForm());
                                                patch.addRecord(weapon);
                                                merger.addRecord(weapon);
                                            }
                                        }
                                    }
                                } else {
                                    if (eItem.getAttribute("type").contentEquals("armor")) {
                                        ARMO armor = (ARMO) merger.getMajor(eItem.getAttribute("EDID"), GRUP_TYPE.ARMO);
                                        if (armor != null) {
                                            KeywordSet keys = armor.getKeywordSet();
                                            NodeList kList = eItem.getElementsByTagName("keyword");
                                            for (int k = 0; k < kList.getLength(); k++) {
                                                Element eKey = (Element) kList.item(k);
                                                KYWD newKey = (KYWD) merger.getMajor(eKey.getTextContent(), GRUP_TYPE.KYWD);
                                                if (newKey == null) {
                                                    newKey = new KYWD(eKey.getTextContent());
                                                    merger.addRecord(newKey);
                                                }
                                                keys.addKeywordRef(newKey.getForm());
                                                patch.addRecord(armor);
                                                merger.addRecord(armor);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

        } catch (Exception e) {
            SPGlobal.logException(e);
            JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
            SPGlobal.closeDebug();
        }

    }

    public static Document mergeDocs(Document doc1, Document doc2) {
        Document newDoc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            newDoc = dBuilder.newDocument();

            ArrayList<Pair<String, Node>> modNodes = new ArrayList<>(0);
            Element rootElement = newDoc.createElement("lootification");
            newDoc.appendChild(rootElement);

            NodeList modList = doc1.getElementsByTagName("mod");
            for (int i = 0; i < modList.getLength(); i++) {
                Node mod = modList.item(i);
                Element eElement = (Element) mod;
                Node newMod = newDoc.importNode(mod, true);
                rootElement.appendChild(newMod);
                Pair<String, Node> p = new Pair<>(eElement.getAttribute("modName"), newMod);
                modNodes.add(p);
            }

            modList = doc2.getElementsByTagName("mod");
            for (int i = 0; i < modList.getLength(); i++) {
                Node mod = modList.item(i);
                Element eElement = (Element) mod;
                boolean foundMod = false;
                for (Pair<String, Node> p : modNodes) {
                    if (p.getBase().contentEquals(eElement.getAttribute("modName"))) {
                        foundMod = true;
                        NodeList newItems = mod.getChildNodes();
                        NodeList oldItems = p.getVar().getChildNodes();
                        for (int j = 0; j < newItems.getLength(); j++) {
                            Node newItem = newItems.item(j);
                            boolean foundItem = false;
                            if (newItem.getNodeType() == Node.ELEMENT_NODE) {
                                Element eNewItem = (Element) newItem;
                                for (int k = 0; k < oldItems.getLength(); k++) {
                                    Node oldItem = oldItems.item(j);
                                    if (oldItem.getNodeType() == Node.ELEMENT_NODE) {
                                        Element eOldItem = (Element) oldItem;
                                        if (eNewItem.getAttribute("EDID").contentEquals(eOldItem.getAttribute("EDID"))) {
                                            foundItem = true;
                                            NodeList newKeys = newItem.getChildNodes();
                                            for (int m = 0; m < newKeys.getLength(); m++) {
                                                if (newKeys.item(m).getNodeType() == Node.ELEMENT_NODE) {
                                                    Element newKey = (Element) newKeys.item(m);
                                                    if (newKey.getNodeName().contentEquals("keyword")) {
                                                        boolean foundKey = false;
                                                        NodeList oldKeys = oldItem.getChildNodes();
                                                        for (int l = 0; l < oldKeys.getLength(); l++) {
                                                            if (oldKeys.item(l).getNodeType() == Node.ELEMENT_NODE) {
                                                                Element oldKey = (Element) oldKeys.item(l);
                                                                if (oldKey.getNodeName().contentEquals("keyword")) {
                                                                    if (oldKey.getTextContent().contentEquals(newKey.getTextContent())) {
                                                                        foundKey = true;
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (!foundKey) {
                                                            newItem.appendChild(newDoc.importNode(newKey, true));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!foundItem) {
                                    p.getVar().appendChild(newDoc.importNode(newItem, true));
                                }
                            }
                        }
                    }
                }
                if (!foundMod) {
                    rootElement.appendChild(newDoc.importNode(mod, true));
                }
            }
            newDoc.normalize();

        } catch (Exception e) {
            SPGlobal.logException(e);
            JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
            SPGlobal.closeDebug();
        }
        return newDoc;
    }
    
}
