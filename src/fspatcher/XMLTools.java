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
    
    /**
     *
     * Adds configured Mods to the XML
     * 
     * @param merger
     */
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

            /* Add Armor Keys to XML */
            /* For every Mod Configured */
            for (Pair<Mod, ArrayList<Pair<ARMO, KYWD>>> p : FSPatcher.modArmors) {
                boolean found = false;
                Node theMod = null;
                /* Check if Mod is already created in XML */
                for (Pair<String, Node> q : modNodes) {
                    if (p.getBase().getName().contentEquals(q.getBase())) {
                        theMod = q.getVar();
                        found = true;
                        break;
                    }
                }
                /* If not creates new MOD in XML */
                if (!found) {
                    Element newElement = newDoc.createElement("mod");
                    newElement.setAttribute("modName", p.getBase().getName());
                    rootElement.appendChild(newElement);
                    theMod = newElement;
                    Pair<String, Node> q = new Pair<>(newElement.getAttribute("modName"), theMod);
                    modNodes.add(q);
                }
                /* For every Configured Armor: */
                for (Pair<ARMO, KYWD> akPair : p.getVar()) {
                    boolean armorFound = false;
                    Node theArmor = null;
                    NodeList items = theMod.getChildNodes();
                    /* Checks if armor is already in XML */
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
                    /* IF not it creates armor in XML */
                    if (!armorFound) {
                        Element newElement = newDoc.createElement("item");
                        newElement.setAttribute("type", "armor");
                        newElement.setAttribute("EDID", akPair.getBase().getEDID());
                        theMod.appendChild(newElement);
                        theArmor = newElement;
                    }
                    /* Add Armor keyword to Armor */
                    Element key = newDoc.createElement("keyword");
                    key.setTextContent(akPair.getVar().getEDID());
                    theArmor.appendChild(key);
                }
            }

            /* Add Weapon Keys to XML */
            /* For every Mod Configured: */
            for (Pair<Mod, ArrayList<Pair<WEAP, KYWD>>> p : FSPatcher.modWeapons) {
                boolean found = false;
                Node theMod = null;
                /* Check if Mod is already created in XML */
                for (Pair<String, Node> q : modNodes) {
                    if (p.getBase().getName().contentEquals(q.getBase())) {
                        theMod = q.getVar();
                        found = true;
                        break;
                    }
                }
                /* If not creates Mod in XML */
                if (!found) {
                    Element newElement = newDoc.createElement("mod");
                    newElement.setAttribute("modName", p.getBase().getName());
                    rootElement.appendChild(newElement);
                    theMod = newElement;
                    Pair<String, Node> q = new Pair<>(newElement.getAttribute("modName"), theMod);
                    modNodes.add(q);
                }
                /* For every Weapon Configured: */
                for (Pair<WEAP, KYWD> akPair : p.getVar()) {
                    boolean armorFound = false;
                    Node theArmor = null;
                    NodeList items = theMod.getChildNodes();
                    /* Check if Weapon is already in XML */
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
                    /* If not creates Weapon in XML */
                    if (!armorFound) {
                        Element newElement = newDoc.createElement("item");
                        newElement.setAttribute("type", "weapon");
                        newElement.setAttribute("EDID", akPair.getBase().getEDID());
                        theMod.appendChild(newElement);
                        theArmor = newElement;
                    }
                    /* Add Weapon Keyword to weapon */
                    Element key = newDoc.createElement("keyword");
                    key.setTextContent(akPair.getVar().getEDID());
                    theArmor.appendChild(key);
                }
            }

            /* Add Outfit Keys to XML */
            /* For every Mod Configured */
            for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                //String master = p.getVar().get(0).getFormMaster().print();
                //master = master.substring(0, master.length() - 4);
                /* For every Outfit Configured */
                for (ARMO arm : p.getVar()) {
                    NodeList items = newDoc.getElementsByTagName("item");
                    /* Find Armor Configured in XML (If armor is not configured it doesn's add it!) */
                    for (int i = 0; i < items.getLength(); i++) {
                        Element eItem = (Element) items.item(i);
                        /* If armor is found */
                        if (eItem.getAttribute("EDID").contentEquals(arm.getEDID())) {
                            Element newKey = newDoc.createElement("keyword");
                            //newKey.setTextContent("dienes_outfit_" + master + p.getBase());
                            /* Changed this to allow outfits to have armors from different mods */
                            newKey.setTextContent("dienes_outfit_" + p.getBase());
                            eItem.appendChild(newKey);
                            /* Find and add configured Tiers */
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
            
            /* Add Faction Weapon Keys to XML */
            
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

    /**
     *
     * Goes through both XML and get's configured Armors, Weapons and Outfits
     *  to be added to the game
     * 
     * @param merger
     * @param patch
     */
    public static void processXML(Mod merger, Mod patch) {
        try {
            List<String> lines = Files.readAllLines(FileSystems.getDefault().getPath(SPGlobal.getPluginsTxt()), StandardCharsets.UTF_8);

            /* LOAD XML's AND MERGE THEM */
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
            /* --------------------------- */

            NodeList nList = doc.getElementsByTagName("mod");

            /* Go through every Mod in the merged XML */
            for (int i = 0; i < nList.getLength(); i++) {
                Node theMod = nList.item(i);
                Element eElement = (Element) theMod;
                /* Check if Mod is in the list OR if user disabled skiping inactive mods */
                if (lines.contains(eElement.getAttribute("modName")) || !(FSPatcher.save.getBool(YourSaveFile.Settings.SKIP_INACTIVE_MODS))) {
                    /* Check if is Drangonborn DLC and user allowed Drangonborn to be lootified */
                    if (!eElement.getAttribute("modName").contentEquals("Dragonborn.esm") || (eElement.getAttribute("modName").contentEquals("Dragonborn.esm") && FSPatcher.save.getBool(YourSaveFile.Settings.LOOTIFY_DRAGONBORN))) {
                        NodeList items = theMod.getChildNodes();
                        /* For every item (Weapon or Armor) in the mod */
                        for (int j = 0; j < items.getLength(); j++) {
                            Node item = items.item(j);
                            if (item.getNodeType() == Node.ELEMENT_NODE) {
                                Element eItem = (Element) item;
                                /* WEAPON */
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
                                    /* ARMOR */
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

    /**
     *
     * Merges two XML documents into one. In case of conflicting keyword nodes
     *  the first apearence wins.
     * 
     * @param doc1
     * @param doc2
     * @return Merged Document
     */
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
